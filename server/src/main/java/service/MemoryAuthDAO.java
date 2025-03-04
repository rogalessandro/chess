package service;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;


public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    public void insertAuth(AuthData authdata) {
        authTokens.put(authdata.authToken(), authdata);
    }

    public AuthData getAuth(String authToken)  {
        return authTokens.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Invalid auth token");
        }
        authTokens.remove(authToken);
    }

    public void clear() {
        authTokens.clear();
    }
}
