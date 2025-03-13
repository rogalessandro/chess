package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {

    private MySQLAuthDAO authDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        authDAO.clear();

        MySQLUserDAO userDAO = new MySQLUserDAO();
        userDAO.clear();
        userDAO.insertUser(new UserData("testUser", "password123", "test@example.com"));
    }

    @Test
    void insertAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.insertAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("token123");

        assertNotNull(retrievedAuth, "Auth token should be successfully inserted and retrieved.");
        assertEquals("token123", retrievedAuth.authToken());
        assertEquals("testUser", retrievedAuth.username());
    }


    @Test
    void getAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("findToken", "testUser");
        authDAO.insertAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("findToken");

        assertNotNull(retrievedAuth, "Auth token should be found.");
        assertEquals("findToken", retrievedAuth.authToken());
        assertEquals("testUser", retrievedAuth.username());
    }


    @Test
    void getAuthNotFound() throws DataAccessException {
        AuthData retrievedAuth = authDAO.getAuth("nonExistentToken");
        assertNull(retrievedAuth, "Should return null for a non-existent auth token.");
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("deleteToken", "testUser");
        authDAO.insertAuth(auth);

        authDAO.deleteAuth("deleteToken");
        AuthData retrievedAuth = authDAO.getAuth("deleteToken");

        assertNull(retrievedAuth, "Auth token should be deleted successfully.");
    }


    @Test
    void clearAllAuth() throws DataAccessException {
        AuthData auth = new AuthData("clearTestToken", "testUser");
        authDAO.insertAuth(auth);

        authDAO.clear();
        AuthData retrievedAuth = authDAO.getAuth("clearTestToken");

        assertNull(retrievedAuth, "Clear method should remove all auth tokens.");
    }
}
