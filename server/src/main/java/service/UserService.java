package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess.*;
import model.*;
import service.Request.*;
import service.Result.*;

import java.util.ArrayList;
import java.util.UUID;

public class UserService {
    public UserMemory userDao = new UserMemory();
    public AuthMemory authDao = new AuthMemory();

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData u = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        if (userDao.getUser(u)) { throw new AlreadyTakenException("Error: username already taken");}

        userDao.createUser(u);
        String auth = generateToken();
        authDao.createAuth(new AuthData(auth, registerRequest.username()));
        return new RegisterResult(registerRequest.username(), auth);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult("abc,", "123");
    }

    public void logout(LogoutRequest logoutRequest) {

    }

    public ArrayList<UserData> list() {
        //for testing purposes
        return userDao.listUsers();
    }
}
