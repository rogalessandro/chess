package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;

public class ClearService {
    private final MemoryUserDAO userDAO;
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public ClearService(MemoryUserDAO userDAO, MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear() {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
