package service;

import model.AuthData;
import org.junit.jupiter.api.*;
import servicefiles.LogoutService;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private MemoryAuthDAO authDAO;
    private LogoutService logoutService;
    private String authToken;

    @BeforeEach
    public void creaUtilidades() {
        authDAO = new MemoryAuthDAO();
        logoutService = new LogoutService(authDAO);


        authToken = "Token123";
        authDAO.insertAuth(new AuthData(authToken, "UsuarioDePrueba"));
    }


    @Test
    public void logoutTokenIncorrecto() {

        assertThrows(DataAccessException.class, () -> logoutService.logout("invalidToken456"));
    }

    @Test
    public void logoutExitoso() throws DataAccessException {

        logoutService.logout(authToken);
        assertNull(authDAO.getAuth(authToken));
    }
}
