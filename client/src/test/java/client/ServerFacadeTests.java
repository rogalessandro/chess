package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.*;
import server.Server;
import service.DataAccessException;

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
    void registerNegativeDuplicateUsername() throws Exception {
        facade.register("dupe", "123", "dupe@email.com");

        var exception = assertThrows(RuntimeException.class, () -> {
            facade.register("dupe", "123", "different@email.com");
        });
        System.out.println("reg failed: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("l usuario ya existe"));
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
    void loginNegativeWrongPassword() throws Exception {
        facade.register("juan", "correctpw", "juan@email.com");

        var exception = assertThrows(RuntimeException.class, () -> {
            facade.login("juan", "wrongpw");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("password"));
    }



    @Test
    void logoutPositive() throws Exception {

        AuthData auth = facade.register("loggy", "pw", "loggy@email.com");


        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutNegativeInvalidToken() {
        var exception = assertThrows(RuntimeException.class, () -> {
            facade.logout("invalid-token-123");
        });
        System.out.println("Logout failed: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("t"));
    }








    @Test
    void createGamePositive() throws Exception {
        AuthData auth = facade.register("gameplayer", "pw", "player@email.com");
        GameData game = facade.createGame(auth.authToken(), "El juegazo");

        assertNotNull(game);
        assertEquals("El juegazo", game.gameName());
        assertTrue(game.gameID() > 0);
    }

    @Test
    void createGameNegativeInvalidToken() {
        var exception = assertThrows(RuntimeException.class, () -> {
            facade.createGame("bad-token", "NoGame");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }



    @Test
    void listGamesPositive() throws Exception {
        var auth = facade.register("viewer", "pass", "viewer@email.com");

        facade.createGame(auth.authToken(), "Uno");
        facade.createGame(auth.authToken(), "Dos");

        var games = facade.listGames(auth.authToken());

        assertNotNull(games);
        assertEquals(2, games.size());
        assertEquals("Uno", games.get(0).gameName());
        assertEquals("Dos", games.get(1).gameName());
    }

    @Test
    void listGamesNegativeInvalidToken() {
        var exception = assertThrows(RuntimeException.class, () -> {
            facade.listGames("bad-token");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }



    @Test
    void joinGamePositive() throws Exception {
        var auth = facade.register("player", "pass", "player@email.com");
        var game = facade.createGame(auth.authToken(), "Team Game");

        assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE));
    }

    @Test
    void joinGameNegativeSeatTaken() throws Exception {
        var auth1 = facade.register("p1", "pw", "p1@email.com");
        var auth2 = facade.register("p2", "pw", "p2@email.com");

        var game = facade.createGame(auth1.authToken(), "The Showdown");
        facade.joinGame(auth1.authToken(), game.gameID(), ChessGame.TeamColor.BLACK);

        var exception = assertThrows(RuntimeException.class, () -> {
            facade.joinGame(auth2.authToken(), game.gameID(), ChessGame.TeamColor.BLACK);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("taken"));
    }




    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
