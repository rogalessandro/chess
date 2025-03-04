package service;

import model.GameData;
import chess.ChessGame;

import java.util.List;

public interface GameDAO {

    void insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    //void updateGame(int gameID, GameData updateGame) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    int generateGameID();

    void joinGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException;

    void clear();
}



