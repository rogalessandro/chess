package dataaccess;

import model.GameData;
import chess.ChessGame;
import com.google.gson.Gson;
import service.DataAccessException;
import service.GameDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MySQLGameDAO implements GameDAO {
    @Override
    public void insertGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public int generateGameID() {
        return 0;
    }

    @Override
    public void joinGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM games";
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
            System.out.println("All games deleted!");
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

}
