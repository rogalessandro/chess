package serviceFiles;

import service.AuthDAO;
import service.DataAccessException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("No hay Token");
        }
        authDAO.deleteAuth(authToken);
    }
}
