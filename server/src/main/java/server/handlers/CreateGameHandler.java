package server.handlers;

import com.google.gson.Gson;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.DataAccessException;
import model.GameData;
import java.util.Map;

public class CreateGameHandler implements Route {


    private final CreateGameService createGameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }


    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            // not sure why yellow this time
            Map<String, String> requestBody = gson.fromJson(req.body(), Map.class);
            String gameName = requestBody.get("gameName");

            GameData newGame = createGameService.createGame(authToken, gameName);

            res.status(200);
            return gson.toJson(newGame);

        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
