package com.debugagent.wordle.webservices;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@RestController
public class WordleWS {
    private String currentWord;
    private int attemptCount;
    private List<String> guessedWords = new ArrayList<>();
    private static final List<String> DICTIONARY = new ArrayList<>();
    private static final int MAX_ATTEMPTS = 6;

    static {
        try (InputStream is = new ClassPathResource("words.txt").getInputStream()) {
            Scanner scanner = new Scanner(is).useDelimiter(System.lineSeparator());
            while (scanner.hasNext()) {
                DICTIONARY.add(scanner.next().toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading dictionary", e);
        }
    }

    public WordleWS() {
        resetGame();
    }

    @GetMapping("/guess")
    public Result guess(@RequestParam String word) {
        word = word.toUpperCase();
        if (word.length() != 5) {
            return new Result(null, "Please enter a 5-letter word.", MAX_ATTEMPTS - attemptCount, guessedWords);
        }

        if (!DICTIONARY.contains(word)) {
            return new Result(null, "Not a valid word!", MAX_ATTEMPTS - attemptCount, guessedWords);
        }

        guessedWords.add(word);
        attemptCount++;
        if (attemptCount >= MAX_ATTEMPTS) {
            String revealedWord = currentWord;
            resetGame();
            return new Result(null, "You lose! The correct word was: " + revealedWord + ". Try again!", 0, guessedWords);
        }

        CharacterResult[] results = new CharacterResult[currentWord.length()];
        boolean isWin = true;
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if (currentChar == currentWord.charAt(i)) {
                results[i] = CharacterResult.GREEN;
            } else {
                isWin = false;
                if (currentWord.indexOf(currentChar) > -1) {
                    results[i] = CharacterResult.YELLOW;
                } else {
                    results[i] = CharacterResult.BLACK;
                }
            }
        }

        if (isWin) {
            resetGame();
            return new Result(results, "Congratulations! You guessed the correct word.", 0, guessedWords);
        }

        return new Result(results, "Keep guessing! Attempts left: " + (MAX_ATTEMPTS - attemptCount), MAX_ATTEMPTS - attemptCount, guessedWords);
    }

    @GetMapping("/newgame")
    public ResponseEntity<Void> newGame() {
        resetGame();
        return ResponseEntity.ok().build();
    }

    private void resetGame() {
        currentWord = selectRandomWord();
        attemptCount = 0;
        guessedWords.clear();
    }

    private String selectRandomWord() {
        Random random = new Random();
        return DICTIONARY.get(random.nextInt(DICTIONARY.size()));
    }
}
