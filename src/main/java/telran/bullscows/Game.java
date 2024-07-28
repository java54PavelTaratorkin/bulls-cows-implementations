package telran.bullscows;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

public class Game {
	private final long id;
	private final String serverSequence;
	private boolean isFinished;
	private final LocalDateTime startTime;
	private LocalDateTime endTime;
	private final List<MoveResult> moveResults;
	private static final Random random = new Random();
	private static final int CODE_LENGTH = 4;

	public Game(long id) {
		this.id = id;
		this.serverSequence = generateServerSequence();
		this.isFinished = false;
		this.startTime = LocalDateTime.now();
		this.moveResults = new ArrayList<>();
	}

	private String generateServerSequence() {
		return random.ints(0, 10).limit(CODE_LENGTH).mapToObj(String::valueOf).collect(Collectors.joining());
	}

	public long getId() {
		return id;
	}

	public String getServerSequence() {
		return serverSequence;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public List<MoveResult> getMoveResults() {
		return new ArrayList<>(moveResults);
	}

	public List<MoveResult> processMove(Move move) {
		String clientCode = move.clientSequence();
		checkForRepeatedCode(clientCode);
		MoveResult result = calculateBullsAndCows(clientCode);
		moveResults.add(result);

		if (result.bulls() == CODE_LENGTH) {
			setEndTime(LocalDateTime.now());
			isFinished = true;
		}

		return moveResults;
	}

	private void checkForRepeatedCode(String clientCode) {
		String repeatedInfo = moveResults.stream()
				.flatMap(
						result -> IntStream.range(0, CODE_LENGTH)
								.filter(i -> clientCode.charAt(i) == result.clientSequence().charAt(i))
								.mapToObj(i -> "Digit " + clientCode.charAt(i) + " has been used before in position "
										+ (i + 1) + " in sequence " + result.clientSequence() + ". "))
				.collect(Collectors.joining());

		if (!repeatedInfo.isEmpty()) {
			throw new IllegalArgumentException(repeatedInfo);
		}
	}

	private MoveResult calculateBullsAndCows(String clientCode) {
		int bulls = 0;
		int cows = 0;

		for (int i = 0; i < CODE_LENGTH; i++) {
			if (clientCode.charAt(i) == serverSequence.charAt(i)) {
				bulls++;
			} else if (serverSequence.contains(String.valueOf(clientCode.charAt(i)))) {
				cows++;
			}
		}

		return new MoveResult(clientCode, bulls, cows);
	}

	private void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
}