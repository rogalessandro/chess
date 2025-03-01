package dataaccess;

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


    public void deleteAuth(AuthData authdata)   {
        authTokens.remove(authdata.authToken());
    }


    public void clear() {
        authTokens.clear();
    }
}
