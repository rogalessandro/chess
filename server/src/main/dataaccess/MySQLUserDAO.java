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
        }finally {

            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("No se cerro las conexiones, por que?");
            }

        }
    }


    public UserData getUser(String username) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try{
            conn = DatabaseManager.getConnection();

            String sqlBuscarUsuario = "SELECT * FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sqlBuscarUsuario);

            stmt.setString(1, username);
            stmt.executeQuery();

            if(resultSet.next()){
                return new UserData(resultSet.getString("username"),
                        resultSet.getString("password"), resultSet.getString("email"));

            }else{
                //No hay usuario
                return null;
            }

        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());

        }

    }


    public void clear() throws DataAccessException {

    }


}
