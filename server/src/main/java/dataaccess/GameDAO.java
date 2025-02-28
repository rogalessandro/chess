package dataaccess;

import model.GameData;

public interface GameDAO {

    void insertGame(GameData game) throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    void updateGame(String gameID, GameData updateGame) throws DataAccessException;

    void listGame() throws DataAccessException;

    void clear();
}



