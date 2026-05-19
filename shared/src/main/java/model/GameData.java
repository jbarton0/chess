package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
//    GameData rename(String newWhiteUsername) {
//        return new GameData(gameID, newWhiteUsername, blackUsername, gameName, game);
//    }
}
