package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.*;
import server.Server;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void clearServer() throws Exception {
        URL url = new URL("http://localhost:" + port + "/db");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int status = connection.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to clear database. Status: " + status);
        }
    }



    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register("testuser123", "password", "test123@email.com");

        assertNotNull(auth);
        assertEquals("testuser123", auth.username());
        assertTrue(auth.authToken().length() > 10);
    }



    @Test
    void loginPositive() throws Exception {
        facade.register("juanito", "1234", "juan@email.com");
        AuthData auth = facade.login("juanito", "1234");

        assertNotNull(auth);
        assertEquals("juanito", auth.username());
        assertTrue(auth.authToken().length() > 5);
    }






    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
