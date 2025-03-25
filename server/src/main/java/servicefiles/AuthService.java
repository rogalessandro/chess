package servicefiles;

import org.mindrot.jbcrypt.BCrypt;
import service.UserDAO;
import service.AuthDAO;
import service.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class AuthService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public AuthService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String login(String username, String password) throws DataAccessException {
        UserData user = userDAO.getUser(username);

        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("Error: The user or password esta mal");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authDAO.insertAuth(authData);

        return authToken;
    }
}
