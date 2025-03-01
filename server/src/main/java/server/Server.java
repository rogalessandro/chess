package server;


import server.handlers.ClearHandler;
import service.ClearService;
import dataaccess.DataAccessObject;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        //DOA instances
        DataAccessObject dao = new DataAccessObject();


        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", new ClearHandler());

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
