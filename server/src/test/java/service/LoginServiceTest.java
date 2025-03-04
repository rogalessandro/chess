package service;

import model.UserData;
import org.junit.jupiter.api.*;
import servicefiles.AuthService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private AuthService authService;

    @BeforeEach
    public void creacionDeUtilidadess() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(userDAO, authDAO);

        userDAO.insertUser(new UserData("UsuarioDePrueba", "contra123", "test@email.com"));
    }


    @Test
    public void testLoginContrasenaMala() {

        assertThrows(DataAccessException.class, () -> authService.login("UsuarioDePrueba", "contraIncorrecta"));
    }

    @Test
    public void testLoginExitoso() throws DataAccessException {

        String authToken = authService.login("UsuarioDePrueba", "contra123");

        assertNotNull(authToken);
        assertNotNull(authDAO.getAuth(authToken));
    }

    @Test
    public void testLoginUsuarioNoExiste() {

        assertThrows(DataAccessException.class, () -> authService.login("fakeUser", "password"));
    }
}
