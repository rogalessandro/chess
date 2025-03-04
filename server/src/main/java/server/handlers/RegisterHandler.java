package server.handlers;

import com.google.gson.Gson;
import service.DataAccessException;
import service.UserAlreadyExistsException;
import serviceFiles.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class RegisterHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }


    public Object handle(Request req, Response res) {
        try {
            Map<String, String> requestBody = gson.fromJson(req.body(), Map.class);

            String username = requestBody.get("username");
            String password = requestBody.get("password");
            String email = requestBody.get("email");

            if (username == null || password == null || email == null) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: no completo lo que necesitamos"));
            }

            String authToken = userService.registerUser(username, password, email);

            res.status(200);
            res.type("application/json");
            return gson.toJson(Map.of("username", username, "authToken", authToken));

        } catch (UserAlreadyExistsException e) {
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));

        } catch (DataAccessException e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));

        }
    }
}
