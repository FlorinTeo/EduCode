package testctrl;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHubOAuthClient {
    private final Context _context;
    private final HttpClient _httpClient;

    public GitHubOAuthClient(Context context) {
        _context = context;
        _httpClient = HttpClient.newBuilder().build();
    }

    public String buildAuthorizeUrl(String redirectUri, String state) {
        Context.Config config = _context.getConfig();
        return String.format(
            "%s?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
            config.github_oauth_authorize_url,
            urlEncode(config.github_client_id),
            urlEncode(redirectUri),
            urlEncode("read:user"),
            urlEncode(state)
        );
    }

    private String buildTokenRequestBody(String redirectUri, String code, String state) {
        Context.Config config = _context.getConfig();
        return String.format(
                    "client_id=%s&client_secret=%s&code=%s&state=%s&redirect_uri=%s",
                    urlEncode(config.github_client_id),
                    urlEncode(config.github_client_secret),
                    urlEncode(code),
                    urlEncode(state),
                    urlEncode(redirectUri)
                );
    }

    public String getGitHubLogin(String redirectUri, String code, String state) {
        Context.Config config = _context.getConfig();

        // request an access token from GitHub backend
        String accessToken = null;
        String tokenRequestBody = buildTokenRequestBody(redirectUri, code, state);
        HttpRequest tokenRequest = HttpRequest.newBuilder()
            .uri(URI.create(config.github_oauth_token_url))
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(tokenRequestBody))
            .build();
        try {
            HttpResponse<String> tokenResponse = _httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
            Servlet.checkTrue(tokenResponse.statusCode() == 200, String.format("GitHub OAuth token request returned %d!", tokenResponse.statusCode()));
            JsonObject tokenJson = JsonParser.parseString(tokenResponse.body()).getAsJsonObject();
            accessToken = tokenJson.has("access_token") ? tokenJson.get("access_token").getAsString() : null;
            Servlet.checkTrue(accessToken != null && !accessToken.isEmpty(), "GitHub OAuth token invalid!");
        } catch(IOException | InterruptedException e) {
            Servlet.checkTrue(false, String.format("GithHub OAuth token exchange failed: %s!", e.getMessage()));
        }

        // request information about the authenticated user
        String ghLogin = null;
        try {
            HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create(config.github_user_api_url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
            HttpResponse<String> userResponse = _httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
            Servlet.checkTrue(userResponse.statusCode() == 200, String.format("GitHub user lookup returned %d!", userResponse.statusCode()));
            JsonObject userJson = JsonParser.parseString(userResponse.body()).getAsJsonObject();
            Servlet.checkTrue(userJson.has("login"), "GitHub user invalid!");
            ghLogin = userJson.get("login").getAsString();
        } catch(IOException | InterruptedException e) {
            Servlet.checkTrue(false, String.format("GithHub OAuth token exchange failed: %s", e.getMessage()));
        }

        return ghLogin;
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
