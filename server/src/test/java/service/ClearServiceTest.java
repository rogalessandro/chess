package service;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import servicefiles.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    public void creacionDeUtilidades() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void testBorrarBaseDeDatos() throws DataAccessException {
        // add stuff tp fake base
        userDAO.insertUser(new UserData("testUsuarioDePrueba", "ContraDePrueba", "test@email.com"));
        authDAO.insertAuth(new AuthData("authToken", "testUsuarioDePrueba"));
        int gameID = gameDAO.generateGameID();
        gameDAO.insertGame(new GameData(gameID, null, null, "juegoDePrueba", new chess.ChessGame()));

        assertNotNull(userDAO.getUser("testUsuarioDePrueba"));
        assertNotNull(authDAO.getAuth("authToken"));
        assertNotNull(gameDAO.getGame(gameID));

        clearService.clear();

        assertNull(userDAO.getUser("testUsuarioDePrueba"));
        assertNull(authDAO.getAuth("authToken"));
        assertNull(gameDAO.getGame(gameID));
    }
}
