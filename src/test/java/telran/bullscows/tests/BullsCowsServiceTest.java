package telran.bullscows.tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.*;

import telran.bullscows.*;

public class BullsCowsServiceTest {
    private BullsCowsService service;
    private static final Random random = new Random();
    private static final int CODE_LENGTH = 4;

    @BeforeEach
    public void setUp() {
        service = new BullsCowsMapImpl();
    }

    @Test
    public void testCreateNewGame() {
        long gameId = service.createNewGame();
        assertTrue(gameId > 0);
    }

    @Test
    public void testGetResultsValidMove() {
        long gameId = service.createNewGame();
        Move move = new Move(gameId, "1234");
        List<MoveResult> results = service.getResults(gameId, move);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    public void testGetResultsInvalidGameId() {
        Move move = new Move(999, "1234");
        assertThrows(IllegalArgumentException.class, () -> service.getResults(999, move));
    }

    @Test
    public void testGetResultsRepeatedMove() {
        long gameId = service.createNewGame();
        Move move = new Move(gameId, "1234");
        service.getResults(gameId, move);
        assertThrows(IllegalArgumentException.class, () -> service.getResults(gameId, move));
    }

    @Test
    public void testIsGameOver() {
        long gameId = service.createNewGame();
        Set<String> usedSequences = new HashSet<>();

        while (!service.isGameOver(gameId)) {
            String clientSequence = generateNonRepeatedSequence(usedSequences);            
            usedSequences.add(clientSequence);
            Move move = new Move(gameId, clientSequence);
            service.getResults(gameId, move);
        }

        assertTrue(service.isGameOver(gameId));
    }

	private String generateNonRepeatedSequence(Set<String> usedSequences) {
		String clientSequence;
		do {
		    clientSequence = generateRandomSequence();
		} while (usedSequences.contains(clientSequence));
		return clientSequence;
	}

    private String generateRandomSequence() {
        return random.ints(0, 10)
                     .distinct()
                     .limit(CODE_LENGTH)
                     .mapToObj(String::valueOf)
                     .collect(Collectors.joining());
    }

    @Test
    public void testProcessValidMove() {
        Game game = new Game(1);
        Move move = new Move(1, "1234");
        List<MoveResult> results = game.processMove(move);
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    public void testProcessInvalidMove() {
        Game game = new Game(1);
        Move move = new Move(1, "abcd");
        assertThrows(IllegalArgumentException.class, () -> game.processMove(move));
    }

    @Test
    public void testProcessMoveWithRepeatedCode() {
        Game game = new Game(1);
        Move move = new Move(1, "1234");
        game.processMove(move);
        assertThrows(IllegalArgumentException.class, () -> game.processMove(move));
    }

    @Test
    public void testCalculateBullsAndCows() {
        Game game = new Game(1);
        MoveResult result = game.processMove(new Move(1, "1234")).get(0);
        assertNotNull(result);
    }
}
