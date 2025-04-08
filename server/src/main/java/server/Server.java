package server;

import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import service.*;
import server.handlers.*;
import servicefiles.*;
import spark.Spark;
import websocket.WebSocketHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // DAO setup
        UserDAO userDAO;
        GameDAO gameDAO;
        AuthDAO authDAO;

        try {
            userDAO = new MySQLUserDAO();
            gameDAO = new MySQLGameDAO();
            authDAO = new MySQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error initializing DAOs: " + e.getMessage());
        }

        // WebSocket endpoint registration
        Spark.webSocket("/ws", new WebSocketHandler());  // <- this line is the key
        System.out.println("✅ WebSocket /ws registered using Spark's native support.");

        // Services
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        AuthService authService = new AuthService(userDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        CreateGameService createGameService = new CreateGameService(gameDAO, authDAO);
        JoinGameService joinGameService = new JoinGameService(gameDAO, authDAO);

        // Endpoints
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegisterHandler(userService));
        Spark.post("/session", new LoginHandler(authService));
        Spark.delete("/session", new LogoutHandler(logoutService));
        Spark.get("/game", new ListGamesHandler(listGamesService));
        Spark.post("/game", new CreateGameHandler(createGameService));
        Spark.put("/game", new JoinGameHandler(joinGameService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
