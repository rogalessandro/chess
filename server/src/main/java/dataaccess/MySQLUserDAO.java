package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());

            stmt.executeUpdate();

            System.out.println("Usuario: " + user.username() + " insertado");

        } catch (SQLException e) {
            System.out.println("No se inserto el usuario, paso algo");
            throw new DataAccessException(e.getMessage());
        }finally {

            try {
                if (stmt != null) {stmt.close();}
                if (conn != null) {conn.close();}
            } catch (SQLException e) {
                System.out.println("No se cerro las conexiones, por que?");
            }

        }
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT username, password, email FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String email = rs.getString("email");



                return new UserData(username, storedHashedPassword, email);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {rs.close();}

                if (stmt != null) {stmt.close();}
                if (conn != null) {conn.close();}
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }



    public void clear() throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = DatabaseManager.getConnection();

            String sql = "DELETE FROM users";
            stmt = conn.prepareStatement(sql);

            stmt.executeUpdate();

        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }finally {
            try {
                if (stmt != null) {stmt.close();}
                if (conn != null) {conn.close();}
            } catch (SQLException e) {
                System.out.println("No se cerro las conexiones, por que?");
            }
        }
    }


}
