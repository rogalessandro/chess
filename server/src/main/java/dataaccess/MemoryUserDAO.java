package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO {
    @Override
    public void insertUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteUser(UserData user) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
