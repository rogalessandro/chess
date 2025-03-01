package dataaccess;

import java.util.HashMap;
import java.util.Map;
import model.UserData;
import model.GameData;
import model.AuthData;

public class DataAccessObject {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();

    public void clear() {
        users.clear();
        games.clear();
        authTokens.clear();
    }
}
