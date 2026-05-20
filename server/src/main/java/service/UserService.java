package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess.*;
import model.*;
import service.Request.*;
import service.Result.*;
import server.Server;
import java.util.ArrayList;
import java.util.UUID;

public class UserService {

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData u = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        if (Server.userMemory.getUser(u)) { throw new AlreadyTakenException("Error: username already taken");}

        Server.userMemory.createUser(u);
        String auth = generateToken();
        Server.authMemory.createAuth(new AuthData(auth, registerRequest.username()));
        return new RegisterResult(registerRequest.username(), auth);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), "email");
        if (!Server.userMemory.findUser(userData)) { throw new IncorrectLoginException("Error: incorrect login");}

        String auth = generateToken();
        Server.authMemory.createAuth(new AuthData(auth, loginRequest.username()));
        return new LoginResult(loginRequest.username(), auth);
    }

    public void logout(LogoutRequest logoutRequest) {

    }

    public ArrayList<UserData> list() {
        //for testing purposes
        return Server.userMemory.listUsers();
    }
}
