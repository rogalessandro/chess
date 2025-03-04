package server.handlers;

import com.google.gson.Gson;
import service.AuthService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class LoginHandler implements Route {


    private final AuthService authService;
    private final Gson gson = new Gson();

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }


    public Object handle(Request req, Response res) {
        try {
            UserData loginReq = gson.fromJson(req.body(), UserData.class);
            String authToken = authService.login(loginReq.username(), loginReq.password());

            res.status(200);
            return gson.toJson(new AuthData(authToken, loginReq.username()));

        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new Message("Error: " + e.getMessage()));
        }
    }

    // created for easier usgae of the catch in every handler
    private record Message(String message) {}
}
