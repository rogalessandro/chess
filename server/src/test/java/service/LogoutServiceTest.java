package service;

import model.AuthData;
import org.junit.jupiter.api.*;
import serviceFiles.LogoutService;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private MemoryAuthDAO authDAO;
    private LogoutService logoutService;
    private String authToken;

    @BeforeEach
    public void CreaUtilidades() {
        authDAO = new MemoryAuthDAO();
        logoutService = new LogoutService(authDAO);


        authToken = "Token123";
        authDAO.insertAuth(new AuthData(authToken, "UsuarioDePrueba"));
    }


    @Test
    public void LogoutTokenIncorrecto() {

        assertThrows(DataAccessException.class, () -> logoutService.logout("invalidToken456"));
    }

    @Test
    public void LogoutExitoso() throws DataAccessException {

        logoutService.logout(authToken);
        assertNull(authDAO.getAuth(authToken));
    }
}
