package service;

import chess.ChessGame;
import services.AuthDAO;
import services.GameDAO;
import services.DataAccessException;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {
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

        try {
            gameDAO.joinGame(gameID, authData.username(), color);
            System.out.println("User " + authData.username() + " successfully joined game " + gameID + " as " + color);
        } catch (DataAccessException e) {
            System.err.println("JoinGame Error: " + e.getMessage());
            throw e;
        }
    }
}
