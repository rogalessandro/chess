package server;


import dataaccess.*;
import server.handlers.ClearHandler;
import server.handlers.RegisterHandler;
import service.UserService;
import service.ClearService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        //DOA instances
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        //Service using the shared DAO

        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        UserService userService = new UserService(userDAO);

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", new ClearHandler(clearService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
