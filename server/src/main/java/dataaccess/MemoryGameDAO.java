package dataaccess;

import model.GameData;

public class MemoryGameDAO implements GameDAO {


    @Override
    public void insertGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(String gameID, GameData updateGame) throws DataAccessException {

    }

    @Override
    public void listGame() throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}

