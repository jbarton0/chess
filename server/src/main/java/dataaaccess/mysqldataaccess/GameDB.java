package dataaaccess.mysqldataaccess;

import chess.ChessGame;
import dataaaccess.DataAccessException;
import dataaaccess.DatabaseManager;
import dataaaccess.GameDAO;
import model.AuthData;
import model.GameData;
import com.google.gson.Gson;

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
}
