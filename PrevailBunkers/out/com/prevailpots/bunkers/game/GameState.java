package com.prevailpots.bunkers.game;

public enum GameState
{
    LOBBY("LOBBY", 0), 
    GAME("GAME", 1), 
    ENDING("ENDING", 2);
    
    private GameState(final String s, final int n) {
    }
}
