package com.connect5.game;

import com.connect5.player.Player;

public class Game {
    private char[][] gameBoard;
    private boolean gameInProgress;
    private String playerOneName;
    private String playerTwoName;
    private int currentPlayerTurn;

    public Game(){
        gameBoard = new char[6][9];
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void setCurrentPlayerTurn(int currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public void startTheGame(int playerId){
        setCurrentPlayerTurn(playerId);
    }

    //return selected column
    public char[] returnSelectedColumn(int columnNo){
        char[] column = new char[6];

        for (int rowId = 0; rowId < 6; rowId++) {
            column[rowId] = this.gameBoard[rowId][columnNo];
        }

        return column;
    }

    //Set token to player selection
    public void setPlayerToken(int rowId, int columnSelected, char currentPlayerToken) {
        this.gameBoard[rowId][columnSelected] = currentPlayerToken;
    }

    //Return game board state as a string to send to client
    public String getGameBoardState() {
        System.out.println("Game Board State");
        String gameBoardState = "";

        for (int rowId = this.gameBoard.length - 1; rowId >= 0; rowId--) {
            for (int colId = 0 ; colId < this.gameBoard[rowId].length; colId++) {
                System.out.print("["+this.gameBoard[rowId][colId]+"]");
                gameBoardState += "["+this.gameBoard[rowId][colId]+"]";
                if (colId == this.gameBoard[rowId].length - 1){
                    System.out.println();
                    gameBoardState += "\n";
                }
            }
        }
        return gameBoardState;
    }
}
