package dataaccess;

import model.GameData;

public interface GameDAO {

    void insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, GameData updateGame) throws DataAccessException;

    void listGame() throws DataAccessException;

    void clear();
}



