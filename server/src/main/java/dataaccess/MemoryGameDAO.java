package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();


    public void insertGame(GameData game) {
        games.put(game.gameID(), game);
    }


    public GameData getGame(int gameID)  {
        return games.get(gameID);
    }


    public void updateGame(int gameID, GameData updateGame)   {
        games.put(gameID, updateGame);
    }


    public void listGame()  {
        for (GameData game : games.values()) {
            System.out.println(game);
        }
    }


    public void clear() {
        games.clear();
    }
}
