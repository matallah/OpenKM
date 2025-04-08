package com.openkm.core;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import com.openkm.api.OKMUserConfig;
import com.openkm.dao.bean.UserConfig;
import com.openkm.module.db.DbAuthModule;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomOAuth2Filter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(CustomOAuth2Filter.class);
	private final String clientId;
	private final String clientSecret;
	private final String authorizationEndpoint;
	private final String tokenEndpoint;
	private final String userInfoEndpoint;
	public static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY = 1000;

	public CustomOAuth2Filter(String clientId, String clientSecret, String authorizationEndpoint,
							  String tokenEndpoint, String userInfoEndpoint) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.authorizationEndpoint = authorizationEndpoint;
		this.tokenEndpoint = tokenEndpoint;
		this.userInfoEndpoint = userInfoEndpoint;
	}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			threadLocalRequest.set(req);

			// Handle API requests first (stateless)
			if (isApiRequest(req)) {
				handleApiRequest(req, res, chain);
				return;
			}

			// Existing browser-based OAuth2 flow
			if (!req.isSecure() && req.getHeader("X-Forwarded-Proto") != null) {
				res.sendRedirect("https://" + req.getServerName() + req.getRequestURI()
					+ (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
				return;
			}

			HttpSession session = req.getSession(true);

			if (isAuthenticated(session)) {
				SecurityContext context = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT);
				SecurityContextHolder.setContext(context);
				chain.doFilter(request, response);
				return;
			}

			String code = req.getParameter("code");
			if (code != null) {
				handleAuthCallbackWithRetry(req, res, code, chain);
			} else {
				redirectToAuthEndpoint(req, res);
			}
		}

		private boolean isApiRequest(HttpServletRequest req) {
			// Adjust the path pattern as needed for your API endpoints
			return req.getRequestURI().startsWith(req.getContextPath() + "/services/rest/");
		}

		private void handleApiRequest(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
			String authHeader = req.getHeader("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				sendUnauthorized(res, "Missing Bearer token");
				return;
			}

			String token = authHeader.substring(7);
			try {
				JSONObject userInfo = new JSONObject(getUserInfo(token));
				String username = userInfo.getString("preferred_username");

				Set<GrantedAuthority> authorities = new HashSet<>();
				extractRoles(userInfo, authorities);
				// Optionally add default roles if necessary
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

				synchronizeUser(username);
				setupSecurityContext(username, authorities, null, req); // No session for API

				chain.doFilter(req, res);
			} catch (Exception e) {
				log.error("API authentication failed", e);
				sendUnauthorized(res, "Invalid token");
			}
		}

		private void sendUnauthorized(HttpServletResponse res, String message) throws IOException {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().write(message);
			res.getWriter().flush();
		}

	private boolean isAuthenticated(HttpSession session) {
		if (session == null) {
			log.info("No session found");
			return false;
		}

		SecurityContext ctx = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT);
		if (ctx == null) {
			log.info("No SecurityContext in session");
			return false;
		}

		Authentication auth = ctx.getAuthentication();
		if (auth == null) {
			log.info("No Authentication in SecurityContext");
			return false;
		}

		if (!auth.isAuthenticated()) {
			log.info("Authentication exists but not marked as authenticated");
			return false;
		}

		return true;
	}

	private void handleAuthCallbackWithRetry(HttpServletRequest req, HttpServletResponse res,
											 String code, FilterChain chain) throws IOException, ServletException {
		Exception lastError = null;
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				chain.doFilter(processOAuthCallback(req, code), res);
				return;
			} catch (Exception e) {
				lastError = e;
				log.warn("Auth attempt {} failed: {}", i+1, e.getMessage());
				try { Thread.sleep(RETRY_DELAY); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
			}
		}
		log.error("Auth failed after {} attempts", MAX_RETRIES, lastError);
		res.sendRedirect(req.getContextPath() + "/login?error=max_retries_exceeded");
	}

	private HttpServletRequest processOAuthCallback(HttpServletRequest req, String code) throws Exception {
		HttpSession session = req.getSession(true);
		try {
			String redirectUri = buildRedirectUrl(req);
			JSONObject token = new JSONObject(exchangeCodeForToken(code, redirectUri));
			JSONObject userInfo = new JSONObject(getUserInfo(token.getString("access_token")));

			String username = userInfo.getString("preferred_username");
			Set<GrantedAuthority> authorities = extractAuthorities(token, userInfo);

			synchronizeUser(username);
			setupSecurityContext(username, authorities, session, req);
			configureUserSession(session, username);

			return new AuthRequestWrapper(req, username);
		} catch (Exception e) {
			cleanup(session);
			throw e;
		}
	}

	private String exchangeCodeForToken(String code, String redirectUri) throws IOException {
		HttpURLConnection conn = configureSSL(new URL(tokenEndpoint).openConnection());
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
			.encodeToString((clientId + ":" + clientSecret).getBytes()));
		conn.setDoOutput(true);
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			httpsConn.setHostnameVerifier((hostname, session) -> true);
		}
		String params = "grant_type=authorization_code&code=" + URLEncoder.encode(code, "UTF-8") +
			"&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");
		conn.getOutputStream().write(params.getBytes());
		return readResponse(conn);
	}

	private String getUserInfo(String accessToken) throws IOException {
		HttpURLConnection conn = configureSSL(new URL(userInfoEndpoint).openConnection());
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			httpsConn.setHostnameVerifier((hostname, session) -> true);
		}
		return readResponse(conn);
	}

	private Set<GrantedAuthority> extractAuthorities(JSONObject token, JSONObject userInfo) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		extractRoles(token, authorities);
		extractRoles(userInfo, authorities);
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		return authorities;
	}

	private void extractRoles(JSONObject json, Set<GrantedAuthority> authorities) {
		Optional.ofNullable(json.optJSONObject("realm_access"))
			.map(ra -> ra.optJSONArray("roles"))
			.ifPresent(roles -> roles.forEach(r ->
				authorities.add(new SimpleGrantedAuthority("ROLE_" + r))));
	}

	private void synchronizeUser(String username) {
		try {
			DbAuthModule.loadUserData(username);
		} catch (Exception e) {
			log.info("Creating new user: {}", username);
		}
	}

		private void setupSecurityContext(String user, Set<GrantedAuthority> authorities,
										  HttpSession session, HttpServletRequest req) {
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				user, null, authorities);
			auth.setDetails(new WebAuthenticationDetails(req));
			context.setAuthentication(auth);
			SecurityContextHolder.setContext(context);

			if (session != null) { // Only set session attributes for non-API requests
				session.setAttribute(SPRING_SECURITY_CONTEXT, context);
				session.setAttribute("user", user);
				session.setMaxInactiveInterval(1800);
			}
		}

	private void configureUserSession(HttpSession session, String user) throws AccessDeniedException, RepositoryException, DatabaseException {
		Optional.ofNullable(OKMUserConfig.getInstance().getConfig(null))
			.ifPresent(
				cfg -> {
					session.setAttribute("userConfig", cfg);
					getNewThreadLocalRequest().getSession().setAttribute("user", user);
				}
			);
	}

	private static final ThreadLocal<HttpServletRequest> threadLocalRequest = new ThreadLocal<>();
	public static HttpServletRequest getNewThreadLocalRequest() {
		return threadLocalRequest.get();
	}

	private HttpURLConnection configureSSL(URLConnection conn) {
		if (conn instanceof HttpsURLConnection) {
			((HttpsURLConnection) conn).setHostnameVerifier((h, s) -> true);
		}
		return (HttpURLConnection) conn;
	}

	private String readResponse(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() != 200) throw new IOException("HTTP " + conn.getResponseCode());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			return reader.lines().collect(Collectors.joining());
		}
	}

	private void redirectToAuthEndpoint(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String state = UUID.randomUUID().toString();
		req.getSession().setAttribute("oauthState", state);
		res.sendRedirect(authorizationEndpoint + "?response_type=code&client_id=" + clientId +
			"&redirect_uri=" + URLEncoder.encode(buildRedirectUrl(req), "UTF-8") +
			"&state=" + state + "&scope=openid roles");
	}

	private String buildRedirectUrl(HttpServletRequest req) {
		StringBuffer url = req.getRequestURL();
		String query = req.getQueryString();
		 return url.toString();
	}

	private void cleanup(HttpSession session) {
		SecurityContextHolder.clearContext();
		if (session != null) session.invalidate();
	}

	@Override public void init(FilterConfig filterConfig) {}
	@Override public void destroy() {}

	private static class AuthRequestWrapper extends HttpServletRequestWrapper {
		private final String user;
		public AuthRequestWrapper(HttpServletRequest req, String user) {
			super(req);
			this.user = user;
		}

		@Override public String getRemoteUser() { return user; }
		@Override public Principal getUserPrincipal() { return () -> user; }
	}
}
