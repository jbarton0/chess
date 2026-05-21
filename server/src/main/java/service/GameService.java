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
        if (createRequest.auth()==null || createRequest.gameName()==null) throw new BadRequestException("Error: bad request");

        if (!Server.authMemory.findAuth(createRequest.auth())) throw new NoAuthException("Error: not authorized");

        AuthData authData = Server.authMemory.getAuth(createRequest.auth());
        GameData gameData = new GameData(new Random().nextInt(100), null, null, createRequest.gameName(), new ChessGame());
        int gameID = Server.gameMemory.create(gameData);
        return new CreateResult(gameID);
    }

    public void join(JoinRequest joinRequest) throws DataAccessException {
        if (joinRequest.playerColor()==null || joinRequest.auth()==null) throw new BadRequestException("Error: bad request");
        boolean matches = joinRequest.playerColor().equals("WHITE") ^ joinRequest.playerColor().equals("BLACK");
        if (!matches) throw new BadRequestException("Error: bad request");

        if (!Server.authMemory.findAuth(joinRequest.auth())) throw new NoAuthException("Error: not authorized");

        if (!Server.gameMemory.findGame(joinRequest.gameID())) throw new NoGameException("Error: bad request");

        GameData gameData = Server.gameMemory.getGame(joinRequest.gameID());

        if (gameData.blackUsername() != null && gameData.whiteUsername() != null) throw new AlreadyTakenException("Error: already taken");
        else if (gameData.whiteUsername()!=null && joinRequest.playerColor().equals("WHITE")) throw new AlreadyTakenException("Error: already taken");
        else if (gameData.blackUsername()!=null && joinRequest.playerColor().equals("BLACK")) throw new AlreadyTakenException("Error: already taken");

        Server.gameMemory.join(gameData, joinRequest);
    }

    public ListResult listGames(ListRequest listRequest) throws DataAccessException {
        AuthData authData = new AuthData(listRequest.auth(), "username");
        if (!Server.authMemory.findAuth(listRequest.auth())) throw new NoAuthException("Error: unauthorized");

        return new ListResult(Server.gameMemory.list());
    }
}
