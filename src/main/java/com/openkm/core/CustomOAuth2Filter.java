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
		if (session != null && session.getAttribute("user") != null) {
			chain.doFilter(request, response);
			return;
		}

		// Check for OAuth2 callback with authorization code
		String code = httpRequest.getParameter("code");
		if (code != null) {
			handleOAuthCallback(httpRequest, httpResponse, code); // Pass the code to the method
			return;
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

		// Validate state parameter
		if (savedState == null || !savedState.equals(state)) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid state parameter");
			return;
		}

		// Exchange code for access token
		String redirectUri = httpRequest.getRequestURL().toString();
		String tokenResponse;
		try {
			tokenResponse = exchangeCodeForToken(code, redirectUri);
		} catch (IOException e) {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token exchange failed");
			return;
		}

		String accessToken = parseAccessToken(tokenResponse);
		if (accessToken == null) {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token response");
			return;
		}

		// Retrieve user info
		String userInfo;
		try {
			userInfo = getUserInfo(accessToken);
		} catch (IOException e) {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to fetch user info");
			return;
		}

		String username = parseUsername(userInfo);
		if (username == null) {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user info");
			return;
		}

		// Create new session and store user
		session = httpRequest.getSession();
		session.setAttribute("user", username);
		session.removeAttribute("oauthState");

		// Redirect to original URL without OAuth2 parameters
		String redirectUrl = buildRedirectUrl(httpRequest);
		httpResponse.sendRedirect(redirectUrl);
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
		String redirectUri = httpRequest.getRequestURL().toString();
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
