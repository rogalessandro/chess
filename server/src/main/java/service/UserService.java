package service;

import dataaccess.UserAlreadyExistsException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String registerUser(String username, String password, String email) throws DataAccessException {


        if (userDAO.getUser(username) != null) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }



        UserData nuevoUsuario = new UserData(username, password, email);
        userDAO.insertUser(nuevoUsuario);

        // Provided by the intrcutions
        String authToken = UUID.randomUUID().toString();


        authDAO.insertAuth(new AuthData(authToken, username));

        return authToken;
    }
}
