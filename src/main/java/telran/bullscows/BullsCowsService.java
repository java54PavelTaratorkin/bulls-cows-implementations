package telran.bullscows;

import java.util.List;

public interface BullsCowsService {
    long createNewGame();
    List<MoveResult> getResults(long gameId, Move move);
    boolean isGameOver(long gameId);
}