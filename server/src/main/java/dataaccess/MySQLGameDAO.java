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
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO games (game_name, white_username, black_username, game_state) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, "{}");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Failed to insert game.");
            }


            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("Game inserted with ID: " + generatedId);
            } else {
                throw new DataAccessException("Failed to retrieve game ID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }



    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();


            String sql = "SELECT * FROM games WHERE game_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameID);


            System.out.println("Executing query: " + stmt.toString());

            rs = stmt.executeQuery();

            if (rs == null) {
                throw new DataAccessException("Query execution failed. ResultSet is null.");
            }

            if (rs.next()) {
                ChessGame game = new Gson().fromJson(rs.getString("game_state"), ChessGame.class);
                System.out.println("Game found: " + rs.getString("game_name"));
                return new GameData(
                        rs.getInt("game_id"),
                        rs.getString("white_username"),
                        rs.getString("black_username"),
                        rs.getString("game_name"),
                        game
                );
            } else {
                System.out.println("No game found with ID: " + gameID);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Database error in getGame(): " + e.getMessage());
            throw new DataAccessException(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        List<GameData> games = new ArrayList<>();
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM games";
            stmt = conn.prepareStatement(sql);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                ChessGame game = gson.fromJson(resultSet.getString("game_state"), ChessGame.class);
                games.add(new GameData(resultSet.getInt("game_id"), resultSet.getString("white_username"),
                        resultSet.getString("black_username"), resultSet.getString("game_name"), game));
            }

            return games;

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

    @Override
    public int generateGameID() {
        return 1;
    }

    @Override
    public void joinGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            conn = DatabaseManager.getConnection();
            String sql;
            if (color == ChessGame.TeamColor.WHITE) {
                sql = "UPDATE games SET white_username = ? WHERE game_id = ?";
            } else {
                sql = "UPDATE games SET black_username = ? WHERE game_id = ?";
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("No games");
                System.out.println(username + " joined game " + gameID + " as " + color);
            }

        } catch (SQLException e) {
            System.out.println("Cant join game");
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

    @Override
    public void clear() throws DataAccessException {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.createStatement();


            // i cant figure out how else to reset but this works so xd
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.executeUpdate("TRUNCATE TABLE games;");
            stmt.executeUpdate("ALTER TABLE games AUTO_INCREMENT = 1;");
            stmt.executeUpdate("TRUNCATE TABLE auth_tokens;");
            stmt.executeUpdate("ALTER TABLE auth_tokens AUTO_INCREMENT = 1;");
            stmt.executeUpdate("TRUNCATE TABLE users;");
            stmt.executeUpdate("ALTER TABLE users AUTO_INCREMENT = 1;");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");

            System.out.println("RESETED");

        } catch (SQLException e) {
            throw new DataAccessException("Error resetting database: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

}
