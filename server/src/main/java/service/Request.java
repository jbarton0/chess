package service;


record LoginRequest(String username, String password) {}

record RegisterRequest(String username, String password, String email) {}

record LogoutRequest(String auth) {}

record ListRequest(String auth) {}

record CreateRequest(String auth, String gameName) {}

record JoinRequest(String auth, String color, int gameID) {}
