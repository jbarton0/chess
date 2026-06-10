package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaaccess.DataAccessException;
import dataaaccess.mysqldataaccess.GameDB;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

import dataaaccess.mysqldataaccess.AuthDB;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDB authDB = new AuthDB();
    private final GameDB gameDB = new GameDB();
//    private final ChessGame chessGame = new ChessGame();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            MakeMoveCommand command2 = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(ctx.session, command.getGameID(), command2.getMove(), command.getAuthToken());
//                case LEAVE -> exit(action.visitorName(), ctx.session);
//                case RESIGN -> exit(action.visitorName(), ctx.session);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String auth, Integer gameID, Session session) throws Exception {
        if (findBadGameID(session, gameID)) { return; }
        if (findBadAuth(session, auth)) { return; }

        connections.add(session, gameID);
        AuthData authData = authDB.getAuth(auth);
        GameData gameData = gameDB.getGame(gameID);
        sendLoadGame(session, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
        sendJoined(session, gameData, authData);
    }

    private void sendLoadGame(Session session, LoadGameMessage msg) throws IOException {
        var ms = new Gson().toJson(msg);
        session.getRemote().sendString(ms);
    }

    private void sendJoined(Session session, GameData gameData, AuthData authData) throws IOException {
        String message;
        if (gameData.whiteUsername().equals(authData.username())) {
            message = String.format("%s has joined the game as WHITE", authData.username());
        } else if (gameData.blackUsername().equals(authData.username())){
            message = String.format("%s has joined the game as BLACK", authData.username());
        } else {
            message = String.format("%s is observing the game", authData.username());
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
    }

    private void sendError(Session session, ErrorMessage msg) throws Exception{
        var ms = new Gson().toJson(msg);
        session.getRemote().sendString(ms);
    }

    private void makeMove(Session session, Integer gameID, ChessMove move, String auth) throws Exception {
        if (findBadGameID(session, gameID)) { return; }
        if (findBadAuth(session, auth)) { return; }

        GameData gameData = gameDB.getGame(gameID);
        if (checkGameOver(session, gameData)) { return; }
        if (findWrongTurn(session, auth, gameData)) { return; }
        if (findWrongTeam(session, gameData, move)) { return; }

        if (gameData.game().validMoves(move.getStartPosition()).contains(move)) {
            gameDB.updateGame(gameData, move);

            GameData newData = gameDB.getGame(gameID);
            connections.broadcast(null, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, newData));
            var message = String.format("%s has been moved to %s", move.getStartPosition().toString(), move.getEndPosition().toString());
            connections.broadcast(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            checkForCheck(session, newData);

        } else {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid move"));
        }
    }

    private void updateGame(Session session, Integer gameID, ChessMove move, GameData gameData) throws Exception {
//        ChessGame game = gameData.game();
//        var wUser = gameData.whiteUsername();
//        var bUser = gameData.blackUsername();
//        var name = gameData.gameName();
//
//        gameDB.remove(gameData);
//        game.makeMove(move);
//        gameDB.create(new GameData(gameID, wUser, bUser, name, game));
        gameDB.updateGame(gameData, move);
    }

    private void checkForCheck(Session session, GameData gameData) throws Exception {
        ChessGame.TeamColor color = gameData.game().getTeamTurn();
        String colorStr = new Gson().toJson(color);
        if (gameData.game().isInCheck(color)) {
            String message = String.format("%s is in check.", colorStr);
            connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));

        } else if (gameData.game().isInCheckmate(color)) {
            String message = String.format("%s is in checkmate.", colorStr);
            connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));

        } else if (gameData.game().isInStalemate(color)) {
            String message = String.format("%s is in stalemate.", colorStr);
            connections.broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
        }
    }

    private boolean findBadGameID(Session session, Integer gameID) throws Exception {
        if (gameID == null | gameDB.getGame(gameID) == null) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid game ID"));
            return true;
        }
        return false;
    }

    private boolean findBadAuth(Session session, String auth) throws Exception {
        if (!authDB.findAuth(auth)) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid authentication"));
            return true;
        }
        return false;
    }

    private boolean findWrongTurn(Session session, String auth, GameData gameData) throws Exception {
        String username = authDB.getAuth(auth).username();
        ChessGame.TeamColor correctTeam = gameData.game().getTeamTurn();
        if (username.equals(gameData.whiteUsername()) && correctTeam.equals(ChessGame.TeamColor.BLACK)) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: other team's turn"));
            return true;
        } else if (username.equals(gameData.blackUsername()) && correctTeam.equals(ChessGame.TeamColor.WHITE)) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: other team's turn"));
            return true;
        } else if (!username.equals(gameData.blackUsername()) && !username.equals(gameData.whiteUsername())) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: observers may not make moves"));
            return true;
        }
        return false;
    }

    private boolean findWrongTeam(Session session, GameData gameData, ChessMove move) throws Exception {
        ChessGame.TeamColor correctTeam = gameData.game().getTeamTurn();
        var start = move.getStartPosition();
        var board = gameData.game().getBoard();
        if (!correctTeam.equals(board.getPiece(start).getTeamColor())) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: can't move other team's pieces"));
            return true;
        }
        return false;
    }

    private boolean checkGameOver(Session session, GameData gameData) throws Exception {
        if (gameData.game().GameOver) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game is over"));
            return true;
        }
        return false;
    }
}
