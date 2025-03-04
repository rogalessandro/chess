package service;

import chess.ChessGame;
import services.AuthDAO;
import services.GameDAO;
import services.DataAccessException;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Missing auth token");
        }

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        int gameID = gameDAO.generateGameID();
        ChessGame newChessGame = new ChessGame();
        //  dont use "" but null as discoveredd
        GameData newGame = new GameData(gameID, null, null, gameName, newChessGame);
        gameDAO.insertGame(newGame);

        return newGame;
    }
}
