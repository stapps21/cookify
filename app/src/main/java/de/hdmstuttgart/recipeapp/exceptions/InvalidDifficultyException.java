package de.hdmstuttgart.recipeapp.exceptions;

public class InvalidDifficultyException extends RuntimeException {
    public InvalidDifficultyException(String difficultyName) {
        super("Invalid difficulty selected! (Difficulty name: \"" + difficultyName + "\")");
    }
}
