package dataaccess;

import model.AuthData;

public interface AuthDAO {


    void insertAuth(AuthData authdata) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear();

}
