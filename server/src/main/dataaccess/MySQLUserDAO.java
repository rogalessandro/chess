package dataaccess;

import model.UserData;
import service.DataAccessException;
import service.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {

    @Override
    public void insertUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }


}
