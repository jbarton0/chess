package service;

import dataaaccess.DataAccessException;
import model.*;
import service.request.*;
import service.result.*;
import server.Server;
import java.util.ArrayList;
import java.util.UUID;

public class UserService {

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username()==null || registerRequest.password()==null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData u = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        if (Server.USER_MEMORY.getUser(u)) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        Server.USER_MEMORY.createUser(u);
        String auth = generateToken();
        Server.AUTH_MEMORY.createAuth(new AuthData(auth, registerRequest.username()));
        return new RegisterResult(registerRequest.username(), auth);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username()==null || loginRequest.password()==null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), "email");
        if (!Server.USER_MEMORY.findUser(userData)) {
            throw new IncorrectLoginException("Error: incorrect login");
        }

        String auth = generateToken();
        Server.AUTH_MEMORY.createAuth(new AuthData(auth, loginRequest.username()));
        return new LoginResult(loginRequest.username(), auth);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.auth();
        if (!Server.AUTH_MEMORY.findAuth(authToken)) {
            throw new NoAuthException("Error: unauthorized");
        }

        Server.AUTH_MEMORY.deleteAuth(authToken);
    }

    public ArrayList<UserData> list() {
        //for testing purposes
        return Server.USER_MEMORY.listUsers();
    }

    public ArrayList<AuthData> listAuth() {
        //for testing purposes
        return Server.AUTH_MEMORY.list();
    }
}
