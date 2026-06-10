package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

//    private final CommandType commandType;
//    private final String authToken;
//    private final Integer gameID;
    private final ChessMove move;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
//        this.commandType = commandType;
//        this.authToken = authToken;
//        this.gameID = gameID;
        this.move = move;
    }

//    public CommandType getCommandType() {
//        return commandType;
//    }
//
//    public String getAuthToken() {
//        return authToken;
//    }
//
//    public Integer getGameID() {
//        return gameID;
//    }

    public ChessMove getMove() { return move; }
}
