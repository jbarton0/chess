package service;

import service.Request.*;
import service.Result.*;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        return new RegisterResult("abc", "123");
    }

    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult("abc,", "123");
    }

    public void logout(LogoutRequest logoutRequest) {

    }
}
