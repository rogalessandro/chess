package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void registerUser(String username, String password, String email) {

        UserData newUser = new UserData(username, password, email);
        userDAO.insertUser(newUser);
    }

}


