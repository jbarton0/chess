package service;

import model.GameData;
import service.Request.*;
import service.Result.*;
import java.util.ArrayList;

public class GameService {
    public CreateResult create(CreateRequest createRequest) {
        return new CreateResult(1234);
    }

    public void join(JoinRequest joinRequest) {

    }

    public ListResult listGames(ListRequest listRequest) {
        return new ListResult(new ArrayList<GameData>());
    }
}
