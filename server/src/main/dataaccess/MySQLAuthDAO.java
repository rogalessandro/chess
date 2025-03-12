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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            conn = DatabaseManager.getConnection();


            String sql = "SELECT users.username FROM auth_tokens JOIN users ON auth_tokens.user_id = users.id WHERE token = ?";
            stmt = conn.prepareStatement(sql);


            stmt.setString(1, token);


            resultSet = stmt.executeQuery();


            if (resultSet.next()) {
                return new AuthData(token, resultSet.getString("username"));
            } else {
                //No hay token
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("No se cerro las conexiones, por que?");
            }
        }
    }


    public void deleteAuth(String token) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM auth_tokens WHERE token = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, token);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Intento borrar, No Token encontrado");
            } else {
                System.out.println("Borrado con exito");
            }
        } catch (SQLException e) {
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


    public void clear() throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = DatabaseManager.getConnection();

            String sql = "DELETE FROM auth_tokens";
            stmt = conn.prepareStatement(sql);

            stmt.executeUpdate();

        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("No se cerro las conexiones, por que?");
            }
        }
    }



}