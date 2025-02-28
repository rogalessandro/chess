package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public void insertAuth(AuthData authdata) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authdata) throws DataAccessException {

    }
}
