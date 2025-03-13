package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import service.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {

    private MySQLUserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    void insertUserExistoso() throws DataAccessException {
        String rawPassword = "Contrasena";
        UserData user = new UserData("UsuarioPrueba", rawPassword, "mail@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("UsuarioPrueba");

        assertNotNull(retrievedUser, "User should be successfully inserted and retrieved.");
        assertEquals("UsuarioPrueba", retrievedUser.username());
        assertTrue(BCrypt.checkpw(rawPassword, retrievedUser.password()), "Password should match the hashed.");
        assertEquals("mail@example.com", retrievedUser.email());
    }

    @Test
    void insertDuplicateUser() throws DataAccessException {
        UserData user1 = new UserData("duplicateUser", "contra123", "mail@example.com");
        UserData user2 = new UserData("duplicateUser", "contra456", "mail2@example.com");

        userDAO.insertUser(user1);

        Exception exception = assertThrows(DataAccessException.class, () -> userDAO.insertUser(user2));
        assertTrue(exception.getMessage().contains("Duplicate entry"), "Should not allow duplicate usernames.");
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        String rawPassword = "passFind";
        UserData user = new UserData("getMe", rawPassword, "find@example.com");
        userDAO.insertUser(user);

        UserData retrievedUser = userDAO.getUser("getMe");

        assertNotNull(retrievedUser, "User should be found.");
        assertEquals("getMe", retrievedUser.username());
        assertTrue(BCrypt.checkpw(rawPassword, retrievedUser.password()), "Retrieved password should match stored hash.");
    }

    @Test
    void getUserNotFound() throws DataAccessException {
        UserData retrievedUser = userDAO.getUser("nonExistentUser");
        assertNull(retrievedUser, "Should return null for a non-existent user.");
    }

    @Test
    void clearUsers() throws DataAccessException {
        UserData user = new UserData("clearTest", "clearPass", "clear@example.com");
        userDAO.insertUser(user);

        userDAO.clear();
        UserData retrievedUser = userDAO.getUser("clearTest");

        assertNull(retrievedUser, "Clear method should remove all users.");
    }
}
