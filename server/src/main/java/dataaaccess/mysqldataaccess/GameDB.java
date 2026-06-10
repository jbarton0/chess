package dataaaccess.mysqldataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaaccess.DataAccessException;
import dataaaccess.DatabaseManager;
import dataaaccess.GameDAO;
import model.AuthData;
import model.GameData;
import com.google.gson.Gson;
import server.Server;
import request.JoinRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


public class GameDB implements GameDAO {

    UserDB userDB = new UserDB();

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        new UserDB().executeUpdate(statement);
    }

    public int create(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteusername, blackusername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        var json = new Gson().toJson(gameData.game());
        userDB.executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json);
        return gameData.gameID();
    }

    public void updateGame(GameData oldGame, ChessMove move) throws DataAccessException, InvalidMoveException {
        oldGame.game().makeMove(move);
        GameData newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        var statement = "UPDATE games SET game = ? WHERE gameID = ?";
        var json = new Gson().toJson(newGame.game());
        userDB.executeUpdate(statement, json, newGame.gameID());
    }

    public void updateGameNoMove(GameData oldGame) throws DataAccessException, InvalidMoveException {
        GameData newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        var statement = "UPDATE games SET game = ? WHERE gameID = ?";
        var json = new Gson().toJson(newGame.game());
        userDB.executeUpdate(statement, json, newGame.gameID());
    }

    public void updateGameWhite(GameData oldGame) throws DataAccessException {
        GameData newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        var statement = "UPDATE games SET whiteusername = ? WHERE gameID = ?";
        userDB.executeUpdate(statement, oldGame.whiteUsername(), newGame.gameID());
    }

    public void updateGameBlack(GameData oldGame) throws DataAccessException {
        GameData newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        var statement = "UPDATE games SET blackusername = ? WHERE gameID = ?";
        userDB.executeUpdate(statement, oldGame.blackUsername(), newGame.gameID());
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteusername");
        String blackUsername = rs.getString("blackusername");
        String gameName = rs.getString("gameName");
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public Collection<GameData> list() throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteusername, blackusername, gameName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(readGame(rs));
                    }
                }
                return gameList;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to retrieve from database");
        }
    }

    public void join(GameData gameData, JoinRequest joinRequest) throws DataAccessException {
        AuthData auth = Server.AUTH_MEMORY.getAuth(joinRequest.auth());
        remove(gameData);
        if (joinRequest.playerColor().equals("WHITE")) {
            GameData updated = new GameData(gameData.gameID(), auth.username(), gameData.blackUsername(), gameData.gameName(), new ChessGame());
            create(updated);
        } else {
            GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(), auth.username(), gameData.gameName(), new ChessGame());
            create(updated);
        }
    }

    public void remove(GameData gameData) throws DataAccessException {
        var statement = "DELETE FROM games WHERE gameID = ?";
        userDB.executeUpdate(statement, gameData.gameID());
    }

    public boolean findGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to retrieve from database");
        }
    }

    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteusername, blackusername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { return readGame(rs); }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve from database");
        }
    }
}
