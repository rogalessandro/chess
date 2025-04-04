package service;

import model.UserData;
import java.util.HashMap;


public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void insertUser(UserData user) throws DataAccessException{
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: Username exists");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}
