package server.handlers;

import com.google.gson.Gson;
import servicefiles.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;
import service.DataAccessException;
import model.GameData;
import java.util.List;
import java.util.Map;

public class ListGamesHandler implements Route {
    private final ListGamesService listGamesService;
    private final Gson gson = new Gson();

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            List<GameData> games = listGamesService.listGames(authToken);

            res.status(200);
            return gson.toJson(Map.of("games", games));

        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
