package dataaaccess.memorydataaccess;

import chess.ChessGame;
import dataaaccess.DataAccessException;
import model.*;
import dataaaccess.GameDAO;
import server.Server;
import request.JoinRequest;

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

    public boolean findGame(int id) {
        return games.stream().anyMatch(gameData -> gameData.gameID() == id);
    }

    public GameData getGame(int id) {
        return games.stream().filter(gameData -> gameData.gameID() == id).findFirst().orElse(null);
    }

    public void join(GameData gameData, JoinRequest joinRequest) throws DataAccessException {
        games.remove(gameData);
        AuthData auth = Server.AUTH_MEMORY.getAuth(joinRequest.auth());
        if (joinRequest.playerColor().equals("WHITE")) {
            GameData updated = new GameData(gameData.gameID(), auth.username(), gameData.blackUsername(), gameData.gameName(), new ChessGame());
            games.add(updated);
        } else {
            GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(), auth.username(), gameData.gameName(), new ChessGame());
            games.add(updated);
        }
    }
}
