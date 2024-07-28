package telran.bullscows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BullsCowsMapImpl implements BullsCowsService {
    public final Map<Long, Game> games = new HashMap<>();
    private long nextGameId = 1;

    @Override
    public long createNewGame() {
        Game game = new Game(nextGameId);
        games.put(nextGameId, game);
        return nextGameId++;
    }

    @Override
    public List<MoveResult> getResults(long gameId, Move move) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game ID not found");
        }
        return game.processMove(move);
    }

    @Override
    public boolean isGameOver(long gameId) {
        Game game = games.get(gameId);
        return game != null && game.isFinished();
    }
}