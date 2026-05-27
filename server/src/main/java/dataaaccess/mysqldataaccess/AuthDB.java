package dataaaccess.mysqldataaccess;

import dataaaccess.AuthDAO;
import dataaaccess.DataAccessException;
import dataaaccess.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class AuthDB implements AuthDAO {

    UserDB userDB = new UserDB();

    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE authTokens";
        userDB.executeUpdate(statement);
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        userDB.executeUpdate(statement, authData.authToken(), authData.username());
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    public ArrayList<AuthData> list() throws DataAccessException {
        ArrayList<AuthData> authList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authTokens";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) { authList.add(readAuth(rs)); }
                }
                return authList;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to retrieve from database");
        }
    }

    public boolean findAuth(String auth) throws DataAccessException {
        // finds by only authToken (sees if authToken exists)
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM authTokens WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve from database");
        }
    }

    public void deleteAuth(String auth) throws DataAccessException {
        var statement = "DELETE FROM authTokens WHERE authToken = ?";
        userDB.executeUpdate(statement, auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authTokens WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { return readAuth(rs); }
                }
            }
           return null;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve from database");
        }
    }
}
