package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import service.JoinGameService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTest {
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private JoinGameService joinGameService;
    private String validAuthToken;
    private int gameID;

    @BeforeEach
    public void CreacionDeUtilidades() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        joinGameService = new JoinGameService(gameDAO, authDAO);


        validAuthToken = "token correcto";
        authDAO.insertAuth(new AuthData(validAuthToken, "test_user"));


        gameID = gameDAO.generateGameID();
        GameData newGame = new GameData(gameID, "", "", "Test Game", new ChessGame());
        gameDAO.insertGame(newGame);
    }

    @Test
    public void testUnirBlanco() throws DataAccessException {
        joinGameService.joinGame(validAuthToken, gameID, ChessGame.TeamColor.WHITE);
        GameData game = gameDAO.getGame(gameID);
        assertEquals("test_user", game.whiteUsername(), "User should  White.");
    }


    @Test
    public void testUnirNegro() throws DataAccessException {
        joinGameService.joinGame(validAuthToken, gameID, ChessGame.TeamColor.BLACK);
        GameData game = gameDAO.getGame(gameID);
        assertEquals("test_user", game.blackUsername(), "User shoul Black.");
    }

    @Test
    public void testJuegoQueNoExiste() {
        int iDmuyFalsa = 999;
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthToken, iDmuyFalsa, ChessGame.TeamColor.WHITE);
        });

        assertEquals("Error: Game does not exist", thrown.getMessage());
    }

    @Test
    public void testUnirConMalAuthToken() {
        String tokenMuyFalso = "token malo";
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(tokenMuyFalso, gameID, ChessGame.TeamColor.WHITE);
        });

        assertEquals("Unauthorized - Invalid token", thrown.getMessage());
    }

    @Test
    public void testUnirConColorYaTomado() throws DataAccessException {

        joinGameService.joinGame(validAuthToken, gameID, ChessGame.TeamColor.WHITE);


        String segundoAuthToken = "segundo-token";
        authDAO.insertAuth(new AuthData(segundoAuthToken, "segundo_user"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(segundoAuthToken, gameID, ChessGame.TeamColor.WHITE);
        });

        assertEquals("White seat already taken", thrown.getMessage());
    }
}
