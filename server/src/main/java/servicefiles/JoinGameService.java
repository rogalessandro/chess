package servicefiles;

import chess.ChessGame;
import service.AuthDAO;
import service.GameDAO;
import service.DataAccessException;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Missing auth token");
        }

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            System.err.println("Error: Unauthorized - Invalid token");
            throw new DataAccessException("Unauthorized - Invalid token");
        }

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            System.err.println("Error: Game ID " + gameID + " does not exist");
            throw new DataAccessException("Error: Game does not exist");
        }

        gameDAO.joinGame(gameID, authData.username(), color);
        return gameDAO.getGame(gameID);
    }

}
