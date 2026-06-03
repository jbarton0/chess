package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import request.*;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clearAll() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    public AuthData register(RegisterRequest registerRequest) throws ResponseException {
        if (registerRequest.username() == null | registerRequest.password() == null) { throw new ResponseException("Error: invalid input"); }
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException {
        if (loginRequest.username() == null | loginRequest.password() == null) { throw new ResponseException("Error: invalid input"); }
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException {
        if (logoutRequest.auth() == null) { throw new ResponseException("Error: invalid input"); }
        var request = buildRequest("DELETE", "/session", null, logoutRequest.auth());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameList listGames(ListRequest listRequest) throws ResponseException {
        var request = buildRequest("GET", "/game", null, listRequest.auth());
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public GameData create(CreateRequest createRequest) throws ResponseException {
        var request = buildRequest("POST", "/game", createRequest, createRequest.auth());
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }

    public void join(JoinRequest joinRequest) throws ResponseException {
        var request = buildRequest("PUT", "/game", joinRequest, joinRequest.auth());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String auth) {
        HttpRequest.Builder request;
        if (auth != null) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + path))
                    .header("authorization", auth)
                    .method(method, makeRequestBody(body));
        }
        else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + path))
                    .method(method, makeRequestBody(body));
        }

        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            throw new ResponseException("Error: bad input");
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
