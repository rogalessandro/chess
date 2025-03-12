package server.handlers;

import com.google.gson.Gson;
import servicefiles.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ClearHandler implements Route {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Object handle(Request req, Response res) {

        try {
            clearService.clear();
            res.status(200);
            res.type("application/json");
            return gson.toJson(Map.of("message", "Database cleared successfully"));

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error clearing database: " + e.getMessage()));
        }
    }
}
