package com.openkm.core;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.openkm.api.OKMAuth;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.PathNotFoundException;
import com.openkm.module.db.DbAuthModule;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomOAuth2Filter implements Filter {

	private final String clientId;
	private final String clientSecret;
	private final String authorizationEndpoint;
	private final String tokenEndpoint;
	private final String userInfoEndpoint;

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
		throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false);

		// Check if user is already authenticated
		if (session != null && session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
			chain.doFilter(request, response);
			return;
		}

		// Check for OAuth2 callback with authorization code
		String code = httpRequest.getParameter("code");
		if (code != null) {
			handleOAuthCallback(httpRequest, httpResponse, code);
		} else {
			// Redirect to authorization endpoint
			redirectToAuthorizationEndpoint(httpRequest, httpResponse);
		}
	}

	private void handleOAuthCallback(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String code)
		throws IOException {
		HttpSession session = httpRequest.getSession(false);
		String state = httpRequest.getParameter("state");
		String savedState = session != null ? (String) session.getAttribute("oauthState") : null;

		try {
			// Validate state parameter
			if (savedState == null || !savedState.equals(state)) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=invalid_state");
				return;
			}

			String tokenResponse = exchangeCodeForToken(code, buildRedirectUrl(httpRequest));
			String accessToken = parseAccessToken(tokenResponse);

			if (accessToken == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=token_failure");
				return;
			}

			// Get user info
			String userInfo = getUserInfo(accessToken);
			String username = parseUsername(userInfo);

			if (username == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=user_info_failure");
				return;
			}

			// Load user data
			try {
				DbAuthModule.loadUserData(username);
			} catch (PathNotFoundException | AccessDeniedException e) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=access_denied");
				return;
			} catch (ItemExistsException | DatabaseException e) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=database_error");
				return;
			}

			// Retrieve user authorities
			Set<GrantedAuthority> authorities = new HashSet<>();
			try {
				List<String> roles = OKMAuth.getInstance().getRoles(null);
				for (String role : roles) {
					authorities.add(new SimpleGrantedAuthority(role));
				}
			} catch (Exception e) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=role_retrieval_failed");
				return;
			}

			// Create authentication token
			User principal = new User(
				username,
				"",
				true, true, true, true,
				authorities
			);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				principal,
				null,
				authorities
			);

			// Add request details
			authentication.setDetails(new WebAuthenticationDetails(httpRequest));

			// Set security context
			SecurityContextHolder.clearContext();
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);

			// Create new session and invalidate old one
			session = httpRequest.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", context);
			session.removeAttribute("oauthState");
			session.setMaxInactiveInterval(1800); // 30 minutes

			// Redirect to original URL or root
			String redirectUrl = "/";
			if (session.getAttribute("ORIGINAL_REQUEST") != null) {
				redirectUrl = (String) session.getAttribute("ORIGINAL_REQUEST");
				session.removeAttribute("ORIGINAL_REQUEST");
			}

			httpResponse.sendRedirect(httpRequest.getContextPath() + redirectUrl);

		} catch (Exception e) {
			System.out.println("Exception occurred: " + e.getMessage());
			SecurityContextHolder.clearContext();
			if (session != null) {
				session.invalidate();
			}
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=authentication_failed");
		}
	}

	private String exchangeCodeForToken(String code, String redirectUri) throws IOException {
		URL url = new URL(tokenEndpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoOutput(true);

		String params = "grant_type=authorization_code" +
			"&code=" + code +
			"&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name()) +
			"&client_id=" + clientId +
			"&client_secret=" + clientSecret;

		try (OutputStream os = conn.getOutputStream()) {
			os.write(params.getBytes(StandardCharsets.UTF_8));
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
			"&state=" + state;

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
}
