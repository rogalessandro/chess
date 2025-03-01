package server.handlers;

import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            clearService.clear();
            res.status(200);
            return "Database cleared successfully";
        } catch (Exception e) {
            res.status(500);
            return "Error clearing database: " + e.getMessage();
        }
    }
}
