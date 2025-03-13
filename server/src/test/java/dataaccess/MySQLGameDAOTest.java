package dataaccess;

import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import service.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySQLGameDAOTest {
    private static MySQLGameDAO gameDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void insertGameAndRetrieveTest() throws DataAccessException {
        GameData newGame = new GameData(1, "whitePlayer", "blackPlayer", "TestGame", new ChessGame());
        gameDAO.insertGame(newGame);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("TestGame", retrievedGame.gameName());
        assertEquals("whitePlayer", retrievedGame.whiteUsername());
        assertEquals("blackPlayer", retrievedGame.blackUsername());
    }

    @Test
    void listGamesTest() throws DataAccessException {
        gameDAO.clear();
        gameDAO.insertGame(new GameData(1, "whitePlayer", "blackPlayer", "TestGame1", new ChessGame()));
        gameDAO.insertGame(new GameData(2, null, null, "TestGame2", new ChessGame()));

        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    void joinGameTest() throws DataAccessException {
        gameDAO.clear();
        gameDAO.insertGame(new GameData(1, null, null, "JoinGameTest", new ChessGame()));

        gameDAO.joinGame(1, "player1", ChessGame.TeamColor.WHITE);
        GameData updatedGame = gameDAO.getGame(1);

        assertNotNull(updatedGame);
        assertEquals("player1", updatedGame.whiteUsername());
        assertNull(updatedGame.blackUsername());
    }

    @Test
    void clearGamesTest() throws DataAccessException {
        gameDAO.insertGame(new GameData(1, "whitePlayer", "blackPlayer", "TestGame", new ChessGame()));
        gameDAO.clear();
        List<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }
}
