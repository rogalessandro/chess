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

    public MySQLGameDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        createGamesTable();
    }

    private void createGamesTable() throws DataAccessException {
        String sql = """
        CREATE TABLE IF NOT EXISTS games (
            game_id INT NOT NULL AUTO_INCREMENT,
            game_name VARCHAR(255) NOT NULL,
            white_username VARCHAR(255),
            black_username VARCHAR(255),
            game_state JSON NOT NULL,
            PRIMARY KEY (game_id)
        )
    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating games table: " + e.getMessage());
        }
    }



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
            } else {
                throw new DataAccessException("Failed to retrieve game ID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
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
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT game_id, game_name, white_username, black_username, game_state FROM games";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int gameID = rs.getInt("game_id");
                String gameName = rs.getString("game_name");
                String whiteUsername = rs.getString("white_username");
                String blackUsername = rs.getString("black_username");
                String gameStateJson = rs.getString("game_state");

                whiteUsername = (whiteUsername != null && !whiteUsername.isEmpty()) ? whiteUsername : null;
                blackUsername = (blackUsername != null && !blackUsername.isEmpty()) ? blackUsername : null;

                ChessGame gameState = new Gson().fromJson(gameStateJson, ChessGame.class);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, gameState));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game list: " + e.getMessage());
        }
        return games;
    }



    @Override
    public int generateGameID() throws DataAccessException {
        int nextID = 1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT MAX(game_id) FROM games";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error generating game ID: " + e.getMessage());
        }


        return nextID;
    }




    @Override
    public void joinGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();


            String selectQuery = "SELECT white_username, black_username FROM games WHERE game_id = ?";
            stmt = conn.prepareStatement(selectQuery);
            stmt.setInt(1, gameID);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new DataAccessException("Game does not exist");
            }

            String currentWhite = rs.getString("white_username");
            String currentBlack = rs.getString("black_username");


            if (color == ChessGame.TeamColor.WHITE && currentWhite != null) {
                throw new DataAccessException("White seat already taken");
            }
            if (color == ChessGame.TeamColor.BLACK && currentBlack != null) {
                throw new DataAccessException("Black seat already taken");
            }


            String sql;
            if (color == ChessGame.TeamColor.WHITE) {
                sql = "UPDATE games SET white_username = ? WHERE game_id = ?";
            } else {
                sql = "UPDATE games SET black_username = ? WHERE game_id = ?";
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();

        } catch (SQLException e) {

        }

    }



    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("ALTER TABLE games AUTO_INCREMENT = 1");
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }

}
