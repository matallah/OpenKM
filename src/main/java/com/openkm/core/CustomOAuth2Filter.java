package com.openkm.core;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMUserConfig;
import com.openkm.dao.bean.UserConfig;
import com.openkm.module.db.DbAuthModule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomOAuth2Filter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(CustomOAuth2Filter.class);
	private final String clientId;
	private final String clientSecret;
	private final String authorizationEndpoint;
	private final String tokenEndpoint;
	private final String userInfoEndpoint;
	private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY = 1000; // 1 second
	public CustomOAuth2Filter(String clientId, String clientSecret,
							  String authorizationEndpoint, String tokenEndpoint, String userInfoEndpoint) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.authorizationEndpoint = authorizationEndpoint;
		this.tokenEndpoint = tokenEndpoint;
		this.userInfoEndpoint = userInfoEndpoint;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Initialization logic if needed
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// Force HTTPS if behind proxy
		if ("http".equals(httpRequest.getScheme()) && httpRequest.getHeader("X-Forwarded-Proto") != null) {
			String redirectUrl = "https://" + httpRequest.getServerName() + httpRequest.getRequestURI();
			if (httpRequest.getQueryString() != null) {
				redirectUrl += "?" + httpRequest.getQueryString();
			}
			httpResponse.sendRedirect(redirectUrl);
			return;
		}

		HttpSession session = httpRequest.getSession(false);

		// Check if user is already authenticated
		if (isUserAuthenticated(session)) {
			chain.doFilter(request, response);
			return;
		}

		String code = httpRequest.getParameter("code");
		if (code != null) {
			handleCallbackWithRetry(httpRequest, httpResponse, code, chain);
		} else {
			try {
				redirectToAuthorizationEndpoint(httpRequest, httpResponse);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private boolean isUserAuthenticated(HttpSession session) {
		if (session != null) {
			SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT);
			return securityContext != null && securityContext.getAuthentication() != null &&
				securityContext.getAuthentication().isAuthenticated();
		}
		return false;
	}

	private void handleCallbackWithRetry(HttpServletRequest request, HttpServletResponse response,
										 String code, FilterChain chain) throws IOException, ServletException {
		Exception lastException = null;

		for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
			try {
				CustomHttpServletRequestWrapper wrapper = handleOAuthCallback(request, response, code);
				if (wrapper != null) {
					chain.doFilter(wrapper, response);
					return;
				}
				break;
			} catch (Exception e) {
				lastException = e;
				log.warn("Authentication attempt {} failed: {}", attempt + 1, e.getMessage());
				try {
					Thread.sleep(RETRY_DELAY);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		if (lastException != null) {
			log.error("Authentication failed after {} attempts", MAX_RETRIES, lastException);
			response.sendRedirect(request.getContextPath() + "/login?error=max_retries_exceeded");
		}
	}

	private CustomHttpServletRequestWrapper handleOAuthCallback(HttpServletRequest request,
																HttpServletResponse response, String code) throws IOException {
		HttpSession session = request.getSession(true);
		try {
			// 1. Exchange code for token with validation
			String redirectUrl = buildRedirectUrl(request);
			String tokenResponse = exchangeCodeForToken(code, redirectUrl);

			if (tokenResponse == null || tokenResponse.isEmpty()) {
				throw new IOException("Empty token response from server");
			}

			JSONObject tokenJson = new JSONObject(tokenResponse);
			if (!tokenJson.has("access_token")) {
				throw new IOException("Missing access token in response");
			}
			String accessToken = tokenJson.getString("access_token");

			// 2. Get user info with validation
			String userInfo = getUserInfo(accessToken);
			if (userInfo == null || userInfo.isEmpty()) {
				throw new IOException("Empty user info response");
			}

			JSONObject userInfoJson = new JSONObject(userInfo);
			if (!userInfoJson.has("preferred_username")) {
				throw new IOException("Missing username in user info");
			}
			String username = userInfoJson.getString("preferred_username");

			// 3. Extract roles with multiple fallbacks
			Set<GrantedAuthority> authorities = new HashSet<>();

			// First try token claims
			if (tokenJson.has("realm_access")) {
				try {
					JSONObject realmAccess = tokenJson.getJSONObject("realm_access");
					if (realmAccess.has("roles")) {
						JSONArray roles = realmAccess.getJSONArray("roles");
						for (int i = 0; i < roles.length(); i++) {
							authorities.add(new SimpleGrantedAuthority("ROLE_" + roles.getString(i)));
						}
					}
				} catch (JSONException e) {
					log.warn("Failed to extract roles from token", e);
				}
			}

			// Fallback to userinfo
			if (authorities.isEmpty() && userInfoJson.has("realm_access")) {
				try {
					JSONObject realmAccess = userInfoJson.getJSONObject("realm_access");
					if (realmAccess.has("roles")) {
						JSONArray roles = realmAccess.getJSONArray("roles");
						for (int i = 0; i < roles.length(); i++) {
							authorities.add(new SimpleGrantedAuthority("ROLE_" + roles.getString(i)));
						}
					}
				} catch (JSONException e) {
					log.warn("Failed to extract roles from userinfo", e);
				}
			}

			// Add default roles if missing
			if (!authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			}
			if (!authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}

			// 4. User synchronization
			synchronizeUser(username, authorities);

			// 5. Create security context
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			Authentication authentication = new UsernamePasswordAuthenticationToken(
				username,
				null,
				authorities
			);
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
			session.setAttribute(SPRING_SECURITY_CONTEXT, context);
			session.setMaxInactiveInterval(1800);

			// 6. User configuration with null checks
			OKMUserConfig userConfigInstance = OKMUserConfig.getInstance();
			if (userConfigInstance == null) {
				throw new IllegalStateException("OKMUserConfig instance not initialized");
			}

			UserConfig userConfig = userConfigInstance.getConfig(null);
			if (userConfig != null) {
				session.setAttribute("userConfig", userConfig);
			} else {
				log.warn("No user configuration found for: {}", username);
			}

			return new CustomHttpServletRequestWrapper(request, username);

		} catch (JSONException e) {
			log.error("JSON parsing error during authentication", e);
			if (session != null) {
				try { session.invalidate(); } catch (IllegalStateException ex) {}
			}
			SecurityContextHolder.clearContext();
			throw new IOException("Authentication data format error", e);
		} catch (IllegalStateException e) {
			log.error("System configuration error", e);
			if (session != null) {
				try { session.invalidate(); } catch (IllegalStateException ex) {}
			}
			SecurityContextHolder.clearContext();
			throw new IOException("System configuration error", e);
		} catch (Exception e) {
			log.error("Authentication error", e);
			if (session != null) {
				try { session.invalidate(); } catch (IllegalStateException ex) {}
			}
			SecurityContextHolder.clearContext();
			throw new IOException("Authentication failed", e);
		}
	}
	private Set<GrantedAuthority> extractKeycloakRoles(JSONObject tokenJson) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		try {
			JSONObject realmAccess = tokenJson.getJSONObject("realm_access");
			if (realmAccess.has("roles")) {
				realmAccess.getJSONArray("roles").forEach(role ->
					authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
				);
			}
		} catch (Exception e) {
			log.warn("Could not extract realm roles", e);
		}
		return authorities;
	}

	private void synchronizeUser(String username, Set<GrantedAuthority> authorities) throws Exception {
		try {
			DbAuthModule.loadUserData(username);
		} catch (Exception e) {
			log.info("Creating new user: {}", username);
//			List<String> roles = authorities.stream()
//				.map(GrantedAuthority::getAuthority)
//				.filter(role -> role.startsWith("ROLE_"))
//				.map(role -> role.substring(5))
//				.toList();
			//DbAuthModule.createUser(null, username, "", true, roles);
		}
	}

	private SecurityContext createSecurityContext(HttpServletRequest request,
												  String username, Set<GrantedAuthority> authorities) {
		User principal = new User(username, "", authorities);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(principal, null, authorities);
		authentication.setDetails(new WebAuthenticationDetails(request));

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}

	private void cleanup(HttpSession session) {
		SecurityContextHolder.clearContext();
		if (session != null) {
			session.invalidate();
		}
	}

	private String exchangeCodeForToken(String code, String redirectUri) throws IOException {
		URL url = new URL(tokenEndpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// Bypass SSL checks (for development only)
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			httpsConn.setHostnameVerifier((hostname, session) -> true);
		}

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		// Basic Authentication
		String auth = clientId + ":" + clientSecret;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
		conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

		conn.setDoOutput(true);

		// URL-encode parameters
		String params = "grant_type=authorization_code" +
			"&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8.name()) +
			"&redirect_uri=" + URLEncoder.encode("https://localhost:8443/OpenKM/", StandardCharsets.UTF_8.name());

		try (OutputStream os = conn.getOutputStream()) {
			os.write(params.getBytes(StandardCharsets.UTF_8));
		}

		// Log response for debugging
		int statusCode = conn.getResponseCode();
		String responseBody = new BufferedReader(new InputStreamReader(conn.getInputStream()))
			.lines().collect(Collectors.joining("\n"));

		if (statusCode != 200) {
			throw new IOException("HTTP " + statusCode + ": " + responseBody);
		}

		return responseBody;
	}

	private String parseAccessToken(String json) {
		int start = json.indexOf("\"access_token\":\"");
		if (start == -1) return null;
		start += "\"access_token\":\"".length();
		int end = json.indexOf("\"", start);
		return end > start ? json.substring(start, end) : null;
	}

	private String getUserInfo(String accessToken) throws IOException {
		URL url = new URL(userInfoEndpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		// Bypass SSL checks (for development only)
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			httpsConn.setHostnameVerifier((hostname, session) -> true);
		}
		if (conn.getResponseCode() != 200) {
			throw new IOException("HTTP " + conn.getResponseCode());
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			return response.toString();
		}
	}

	private String parseUsername(String userInfoJson) {
		int start = userInfoJson.indexOf("\"preferred_username\":\"");
		if (start == -1) return null;
		start += "\"preferred_username\":\"".length();
		int end = userInfoJson.indexOf("\"", start);
		return end > start ? userInfoJson.substring(start, end) : null;
	}

	private void redirectToAuthorizationEndpoint(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
		throws IOException {
		String redirectUri = buildRedirectUrl(httpRequest);
		String state = generateState();

		HttpSession session = httpRequest.getSession();
		session.setAttribute("oauthState", state);

		String authUrl = authorizationEndpoint +
			"?response_type=code" +
			"&client_id=" + clientId +
			"&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name()) +
			"&state=" + state+
			"&scope=openid roles";

		httpResponse.sendRedirect(authUrl);
	}

	private String generateState() {
		return UUID.randomUUID().toString();
	}

	private String buildRedirectUrl(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String query = request.getQueryString();

		if (query == null) {
			return url.toString();
		}

		String[] params = query.split("&");
		List<String> newParams = new ArrayList<>();
		for (String param : params) {
			if (!param.startsWith("code=") && !param.startsWith("state=")) {
				newParams.add(param);
			}
		}

		if (newParams.isEmpty()) {
			return url.toString();
		} else {
			return url.append("?").append(String.join("&", newParams)).toString();
		}
	}

	@Override
	public void destroy() {
		// Cleanup logic if needed
	}

	// Custom request wrapper to override getRemoteUser()
	private static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private final String remoteUser;

		public CustomHttpServletRequestWrapper(HttpServletRequest request, String remoteUser) {
			super(request);
			this.remoteUser = remoteUser;
		}

		@Override
		public String getRemoteUser() {
			return remoteUser;
		}

		@Override
		public Principal getUserPrincipal() {
			return new UserPrincipal(remoteUser);
		}
	}

	// Custom Principal implementation
	private static class UserPrincipal implements Principal {
		private final String name;

		public UserPrincipal(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
