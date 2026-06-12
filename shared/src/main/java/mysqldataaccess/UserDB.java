package mysqldataaccess;

import chess.ChessGame;
import dataaaccess.DataAccessException;
import dataaaccess.DatabaseManager;
import dataaaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class UserDB implements UserDAO {

    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        executeUpdate(statement, userData.username(), hashedPassword, userData.email());
    }

    public boolean getUser(UserData u) throws DataAccessException {
        // finds by only username (sees if username exists)
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, u.username());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to retrieve from database");
        }
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        ArrayList<UserData> userList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) { userList.add(readUser(rs)); }
                }
                return userList;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to retrieve from database");
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    public boolean findUser(UserData u) throws DataAccessException {
        //finds user by username && password
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, u.username());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var user = readUser(rs);
                        return BCrypt.checkpw(u.password(), user.password());
                    }
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to retrieve from database");
        }
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param instanceof Integer p) { ps.setInt(i + 1, p); }
                    else if (param instanceof ChessGame p) { ps.setString(i + 1, p.toString()); }
                    else if (param == null) { ps.setNull(i + 1, NULL); }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database");
        }
    }
}
