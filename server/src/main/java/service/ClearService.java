package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;

public class ClearService {

    private final DataAccessObject dao;

    public ClearService(DataAccessObject dao) {
        this.dao = dao;
    }

    public void clear() throws DataAccessException {
        dao.clear();
    }

}
