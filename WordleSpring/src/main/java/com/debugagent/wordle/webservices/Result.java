package com.debugagent.wordle.webservices;

import java.util.List;

public record Result(CharacterResult[] results, String errorMessage, int remainingAttempts, List<String> guessedWords) {
}
