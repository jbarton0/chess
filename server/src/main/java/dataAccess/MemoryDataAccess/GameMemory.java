package dataAccess.MemoryDataAccess;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.*;
import dataAccess.GameDAO;
import server.Server;
import service.Request.JoinRequest;

import java.util.ArrayList;
import java.util.Collection;

public class GameMemory implements GameDAO {
    final ArrayList<GameData> games = new ArrayList<>();

    public void clearGames() throws DataAccessException {
        games.clear();
    }

    public Collection<GameData> list() throws DataAccessException {
        return games;
    }

    public int create(GameData gameData) {
        games.add(gameData);
        return gameData.gameID();
    }

    public boolean findGame(int ID) {
        return games.stream().anyMatch(GameData -> GameData.gameID() == ID);
    }

    public GameData getGame(int ID) {
        return games.stream().filter(GameData -> GameData.gameID() == ID).findFirst().orElse(null);
    }

    public void join(GameData gameData, JoinRequest joinRequest) throws DataAccessException {
        games.remove(gameData);
        AuthData auth = Server.authMemory.getAuth(joinRequest.auth());
        if (joinRequest.playerColor().equals("WHITE")) {
            GameData updated = new GameData(gameData.gameID(), auth.username(), gameData.blackUsername(), gameData.gameName(), new ChessGame());
            games.add(updated);
        } else {
            GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(), auth.username(), gameData.gameName(), new ChessGame());
            games.add(updated);
        }
    }
}
