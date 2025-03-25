package server.handlers;

import com.google.gson.Gson;
import servicefiles.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;
import service.DataAccessException;

public class LogoutHandler implements Route {
    private final LogoutService logoutService;
    private final Gson gson = new Gson();

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            logoutService.logout(authToken);

            res.status(200);
            return gson.toJson(new Message("Logged out efecivo"));

        // sin permiso
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new Message((String) e.getMessage()));
        }
    }

    private record Message(String message) {}
}
