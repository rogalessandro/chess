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
    private final Gson gson = new Gson();

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;


        try{
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO games (game_name, white_username, black_username, game_state) VALUES (?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, gson.toJson(game.game()));

            // we need the id of the game, take it here
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int generatedID = keys.getInt(1);
                System.out.println("Game and id " + generatedID);
            }
        }catch (SQLException e) {
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
