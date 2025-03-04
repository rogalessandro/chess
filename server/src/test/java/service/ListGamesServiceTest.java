package service;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import servicefiles.ListGamesService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private ListGamesService listGamesService;
    private String authToken;

    @BeforeEach
    public void creaUtilidades() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        listGamesService = new ListGamesService(gameDAO, authDAO);

        authToken = "Token123";
        authDAO.insertAuth(new AuthData(authToken, "UsuarioPrueba"));
    }

    @Test
    public void testMalToken() {

        assertThrows(DataAccessException.class, () -> listGamesService.listGames("TokenMal"));
    }

    @Test
    public void testListaHecha() throws DataAccessException {

        gameDAO.insertGame(new GameData(1, null, null, "Game 1", new chess.ChessGame()));
        gameDAO.insertGame(new GameData(2, null, null, "Game 2", new chess.ChessGame()));

        List<GameData> games = listGamesService.listGames(authToken);

        assertEquals(2, games.size());
        assertEquals("Game 1", games.get(0).gameName());
        assertEquals("Game 2", games.get(1).gameName());
    }


}
