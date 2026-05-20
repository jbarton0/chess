package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess.*;
import model.*;
import service.Request.*;
import service.Result.*;
import java.util.UUID;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (new UserMemory().getUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()))) { throw new AlreadyTakenException("Error: username already taken");}

        new UserMemory().createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        String auth = generateToken();
        new AuthMemory().createAuth(new AuthData(auth, registerRequest.username()));
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
}
