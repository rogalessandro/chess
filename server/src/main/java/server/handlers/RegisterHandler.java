package server.handlers;

import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.DataAccessException;
import model.UserData;

public class RegisterHandler implements Route {


    // primero en usar gson xdxd
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            userService.registerUser(user.username(), user.password(), user.email());

            res.status(200);
            return gson.toJson(user);
        } catch (DataAccessException e) {
            res.status(400);
            return gson.toJson(new Message("Error: " + e.getMessage()));
        }
    }

    private record Message(String message) {}
}
