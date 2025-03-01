package server;


import server.handlers.ClearHandler;
import service.ClearService;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        //DOA instances
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        //Service using the shared DAO

        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

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
