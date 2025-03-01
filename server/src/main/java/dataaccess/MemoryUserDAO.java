package dataaccess;

import model.UserData;
import java.util.HashMap;


public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();


    public void insertUser(UserData user) {
        users.put(user.username(), user);
    }


    public UserData getUser(String username) {
        return users.get(username);
    }


    public void deleteUser(UserData user) {
        users.remove(user.username());
    }


    public void clear() {
        users.clear();
    }
}
