package service;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import serviceFiles.CreateGameService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private CreateGameService createGameService;
    private String authToken;

    @BeforeEach
    public void CreaUtilidades() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        createGameService = new CreateGameService(gameDAO, authDAO);

        authToken = "Token123";
        authDAO.insertAuth(new AuthData(authToken, "UsuarioPrueba"));
    }

    @Test
    public void testCrearUnJuegoExitoso() throws DataAccessException {

        GameData game = createGameService.createGame(authToken, "JuegoPrueba");

        assertNotNull(game);
        assertEquals("JuegoPrueba", game.gameName());
        assertNotNull(gameDAO.getGame(game.gameID()));
    }

    @Test
    public void testJuegoPeroInvalidAuth() {

        assertThrows(DataAccessException.class, () -> createGameService.createGame("token malo", "Juego Inco"));
    }


}
