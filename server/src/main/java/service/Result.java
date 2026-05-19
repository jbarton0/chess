package service;

import model.GameData;
import java.util.Collection;


record LoginResult(String username, String auth) {}

record RegisterResult(String username, String auth) {}

// record LogoutResult() {}

record ListResult(Collection<GameData> games) {}

record CreateResult(int gameID) {}

// record JoinResult() {}
