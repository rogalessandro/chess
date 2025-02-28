package server.handlers;

import com.google.gson.Gson;
import service.ClearService;
import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        ClearService service = new ClearService();
        Gson gson = new Gson();

        try {
            service.clearDatabase();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            return gson.toJson(error);
        }
    }
}
