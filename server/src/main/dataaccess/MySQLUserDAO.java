package dataaccess;

import model.UserData;
import service.DataAccessException;
import service.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {


    public void insertUser(UserData user) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getConnection();

            String sqlInsert = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sqlInsert);

            // insertar los valores
            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            stmt.setString(3, user.email());

            stmt.executeUpdate();

            System.out.println("Usuario: " + user.username() + " insertado");

        } catch (SQLException e) {
            System.out.println("No se inserto el usuario, paso algo");
            throw new DataAccessException(e.getMessage());
        }
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {

        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }


}
