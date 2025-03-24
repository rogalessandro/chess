package server;

import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.DatabaseManager;
import dataaccess.MySQLUserDAO;
import service.*;
import server.handlers.ListGamesHandler;
import servicefiles.ListGamesService;
import server.handlers.ClearHandler;
import servicefiles.ClearService;
import server.handlers.RegisterHandler;
import servicefiles.UserService;
import server.handlers.LoginHandler;
import server.handlers.LogoutHandler;
import server.handlers.JoinGameHandler;
import servicefiles.JoinGameService;
import servicefiles.LogoutService;
import server.handlers.CreateGameHandler;
import servicefiles.CreateGameService;
import servicefiles.AuthService;
import spark.*;





public class Server {


    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //DOA instances

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

        //Service using the shared DAO as TA said to create here
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);





        AuthService authService = new AuthService(userDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        CreateGameService createGameService = new CreateGameService(gameDAO, authDAO);
        JoinGameService joinGameService = new JoinGameService(gameDAO, authDAO);

        // Register your endpoints and handle exceptions here.
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
