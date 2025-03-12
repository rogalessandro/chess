package dataaccess;

import model.AuthData;
import service.AuthDAO;
import service.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {


    public void insertAuth(AuthData authData) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO auth_tokens (token, user_id) VALUES (?, (SELECT id FROM users WHERE username = ?))";

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, authData.authToken());
            stmt.setString(2, authData.username());
            stmt.executeUpdate();

            System.out.println("Auth token: " + authData.username());

        } catch (SQLException e) {
            System.out.println("No se inserto el usuario, paso algo");
            throw new DataAccessException(e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("No se cerro las conexiones, por que?");
            }
        }
    }


    public AuthData getAuth(String token) throws DataAccessException {

        return null;
    }


    public void deleteAuth(String token) throws DataAccessException {

    }


    public void clear() throws DataAccessException {

    }


}