package tech.wenisch.discord.experiencebot.exceptions;

public class NoSessionStartTimeException extends Exception { 
    public NoSessionStartTimeException(String errorMessage) {
        super(errorMessage);
    }
}