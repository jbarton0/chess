package service;

import dataAccess.DataAccessException;
import model.*;
import server.Server;
import service.Request.*;
import service.Result.*;
import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    public CreateResult create(CreateRequest createRequest) {
        return new CreateResult(1234);
    }

    public void join(JoinRequest joinRequest) {

    }

    public ListResult listGames(ListRequest listRequest) throws DataAccessException {
        AuthData authData = new AuthData(listRequest.auth(), "username");
        if (!Server.authMemory.findAuth(listRequest.auth())) throw new NoAuthException("Error: unauthorized");

        return new ListResult(Server.gameMemory.list());
    }
}
