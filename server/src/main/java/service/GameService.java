package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.*;
import server.Server;
import service.Request.*;
import service.Result.*;
import java.util.Random;

public class GameService {
    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        if (!Server.authMemory.findAuth(createRequest.auth())) throw new NoAuthException("Error: not authorized");

        GameData gameData = new GameData(new Random().nextInt(100), null, null, createRequest.gameName(), new ChessGame());
        int gameID = Server.gameMemory.create(gameData);
        return new CreateResult(gameID);
    }

    public void join(JoinRequest joinRequest) {

    }

    public ListResult listGames(ListRequest listRequest) throws DataAccessException {
        AuthData authData = new AuthData(listRequest.auth(), "username");
        if (!Server.authMemory.findAuth(listRequest.auth())) throw new NoAuthException("Error: unauthorized");

        return new ListResult(Server.gameMemory.list());
    }
}
