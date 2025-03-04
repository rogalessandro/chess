package server.handlers;

import com.google.gson.Gson;
import servicefiles.JoinGameService;
import spark.Request;
import spark.Response;
import spark.Route;
import service.DataAccessException;
import chess.ChessGame;
import java.util.Map;

public class JoinGameHandler implements Route {
    private final JoinGameService joinGameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");

            //also not sure why yelloww
            Map requestBody = gson.fromJson(req.body(), Map.class);

            if (!requestBody.containsKey("gameID") || !requestBody.containsKey("playerColor")) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: no  gameID or playerColor"));
            }

            //check if gameID or color good
            int gameID;
            try {
                Object gameIDObj = requestBody.get("gameID");

                if (gameIDObj instanceof Double) {
                    gameID = ((Double) gameIDObj).intValue();
                } else {
                    gameID = Integer.parseInt(gameIDObj.toString());
                }

            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request - bad gameID"));
            }

            ChessGame.TeamColor color;
            try {
                color = ChessGame.TeamColor.valueOf(requestBody.get("playerColor").toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request - bad playerColor"));
            }
            //#####
            joinGameService.joinGame(authToken, gameID, color);

            res.status(200);
            return "{}";

        } catch (DataAccessException e) {
            System.err.println("JoinGame Failed: " + e.getMessage());
            String errorMsg = e.getMessage();

            if (errorMsg.contains("Unauthorized")) {
                res.status(401);
            } else if (errorMsg.contains("already taken")) {
                res.status(403);
            } else {
                res.status(400);
            }

            return gson.toJson(Map.of("message", "Error: " + errorMsg));

        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));

        }
    }
}
