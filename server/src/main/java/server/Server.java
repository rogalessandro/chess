package server;


import dataaccess.*;
import server.handlers.ListGamesHandler;
import service.ListGamesService;

import server.handlers.ClearHandler;
import service.ClearService;

import server.handlers.RegisterHandler;
import service.UserService;

import server.handlers.LoginHandler;
import server.handlers.LogoutHandler;

import server.handlers.JoinGameHandler;
import service.JoinGameService;

import service.LogoutService;

import server.handlers.CreateGameHandler;
import service.CreateGameService;

import service.AuthService;
import spark.*;






public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        //DOA instances
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

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
