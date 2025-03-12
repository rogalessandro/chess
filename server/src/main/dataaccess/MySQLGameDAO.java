package dataaccess;

import model.GameData;
import chess.ChessGame;
import com.google.gson.Gson;
import model.UserData;
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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try{
            conn = DatabaseManager.getConnection();

            String sqlBuscarUsuario = "SELECT * FROM games WHERE game_id = ?";
            stmt = conn.prepareStatement(sqlBuscarUsuario);

            stmt.setInt(1, gameID);
            stmt.executeQuery();

            if(resultSet.next()){
                ChessGame game = gson.fromJson(resultSet.getString("game_state"), ChessGame.class);
                return new GameData(
                        resultSet.getInt("game_id"),
                        resultSet.getString("white_username"),
                        resultSet.getString("black_username"),
                        resultSet.getString("game_name"),
                        game
                );
            }else{
                //No hay game
                return null;
            }

        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());

        }finally {
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

            if (resultSet.next()) {
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
