package client;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import com.google.gson.Gson;
import java.util.List;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var requestBody = Map.of(
                "username", username,
                "password", password,
                "email", email
        );

        URL url = new URL(serverUrl + "/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os)) {
            gson.toJson(requestBody, writer);
            writer.flush();
        }


        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
            throw new RuntimeException("Error: " + errorMessage);
        }

        try (InputStream responseBody = connection.getInputStream()) {
            var map = gson.fromJson(new InputStreamReader(responseBody), Map.class);
            return new AuthData((String) map.get("authToken"), (String) map.get("username"));
        }
    }


    public AuthData login(String username, String password) throws Exception {
        var requestBody = Map.of(
                "username", username,
                "password", password
        );

        URL url = new URL(serverUrl + "/session");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os)) {
            gson.toJson(requestBody, writer);
            writer.flush();
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String error = new BufferedReader(new InputStreamReader(connection.getErrorStream()))
                    .lines().reduce("", (a, b) -> a + b);
            throw new RuntimeException("Error: " + error);
        }

        try (InputStream responseBody = connection.getInputStream()) {
            var map = gson.fromJson(new InputStreamReader(responseBody), Map.class);
            return new AuthData((String) map.get("authToken"), (String) map.get("username"));
        }
    }


    public void logout(String authToken) throws Exception {
        URL url = new URL(serverUrl + "/session");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", authToken);

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
            throw new RuntimeException("Logout failed: " + errorMessage);
        }
    }


    public GameData createGame(String authToken, String gameName) throws Exception {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);


        try (OutputStream os = connection.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os)) {
            gson.toJson(Map.of("gameName", gameName), writer);
            writer.flush();
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                    .lines().reduce("", (acc, line) -> acc + line);
            throw new RuntimeException("Create game failed: " + errorMessage);
        }

        try (InputStream responseBody = connection.getInputStream();
             InputStreamReader reader = new InputStreamReader(responseBody)) {
            return gson.fromJson(reader, GameData.class);
        }
    }


    public List<GameData> listGames(String authToken) throws Exception {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                    .lines()
                    .reduce("", (a, b) -> a + b);
            throw new RuntimeException("List games failed: " + errorMessage);
        }

        try (InputStream responseBody = connection.getInputStream();
             InputStreamReader reader = new InputStreamReader(responseBody)) {
            Map response = gson.fromJson(reader, Map.class);
            var gamesJson = gson.toJson(response.get("games"));
            GameData[] games = gson.fromJson(gamesJson, GameData[].class);
            return List.of(games);
        }
    }


    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws Exception {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);

        var requestBody = Map.of(
                "gameID", gameID,
                "playerColor", color.name()
        );

        try (OutputStream os = connection.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os)) {
            gson.toJson(requestBody, writer);
            writer.flush();
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                    .lines().reduce("", (a, b) -> a + b);
            throw new RuntimeException("Join game failed: " + errorMessage);
        }
    }

}
