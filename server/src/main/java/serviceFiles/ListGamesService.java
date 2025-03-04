package serviceFiles;

import service.AuthDAO;
import service.GameDAO;
import service.DataAccessException;
import model.AuthData;
import model.GameData;
import java.util.List;

public class ListGamesService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("No auth token");
        }

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        return gameDAO.listGames();
    }
}
