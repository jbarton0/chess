package service;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;

class ServiceTests {

    @BeforeEach
    void clear() throws DataAccessException {
        new ClearService().clearAll();
    }

}
