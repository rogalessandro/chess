package service;

import model.GameData;
import chess.ChessGame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public void insertGame(GameData game) {
        games.put(game.gameID(), game);
    }


    public GameData getGame(int gameID)  {
        return games.get(gameID);
    }


//    public void updateGame(int gameID, GameData updateGame)   {
//        games.put(gameID, updateGame);
//    }


    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public int generateGameID() {
        return nextGameID++;
    }

    public void joinGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {

        GameData game = games.get(gameID);

        if (game == null) {
            throw new DataAccessException("Game does not exist");
        }

        if (color == ChessGame.TeamColor.WHITE && game.whiteUsername() != null && !game.whiteUsername().isEmpty()) {
            throw new DataAccessException("White seat already taken");
        }
        if (color == ChessGame.TeamColor.BLACK && game.blackUsername() != null && !game.blackUsername().isEmpty()) {
            throw new DataAccessException("Black seat already taken");
        }

        String whiteUsername;
        String blackUsername;

        if (color == ChessGame.TeamColor.WHITE) {
            whiteUsername = username;
        } else {
            whiteUsername = game.whiteUsername();
        }

        if (color == ChessGame.TeamColor.BLACK) {
            blackUsername = username;
        } else {
            blackUsername = game.blackUsername();
        }

        GameData updatedGame = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
        games.put(gameID, updatedGame);

    }

    // example see in ptshop code
    public void clear() {
        games.clear();
        nextGameID = 1;
    }
}
