package com.connect5.player;


import com.connect5.game.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Private inner class for handling client connection threads
public class Player extends Thread {
    private Socket clientSocket;
    private Game game;
    private int playerId;
    private String playerName;
    private char playerSymbol;
    Player opponentPlayer;
    private String turn;
    private String message;
    private String request;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Player(Socket clientSocket, Game game, char symbol, int playerId) {
        this.clientSocket = clientSocket;
        this.setGame(game);
        this.setPlayerId(playerId);
        this.setPlayerSymbol(symbol);

        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try{

            do {
                try {
                    //Send initial Message
                    message = "Welcome player! Please make your selection";
                    sendMessage(message);

                    request = (String)objectInputStream.readObject();

                    switch (request) {
                        case "play":
                            startPlayingTheGame();
                            break;
                        case "showGameBoardState":
                            returnGameBoardState();
                            break;
                        case "bye":
                            endClientConnection();
                            break;
                    }
                }catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                }
            } while (!message.equalsIgnoreCase("exit"));

        } catch (IOException ioException){
            ioException.printStackTrace();
        } finally{
            try{
                objectInputStream.close();
                objectOutputStream.close();
                clientSocket.close();
            } catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public void setOpponentPlayer(Player opponentPlayer) {
        this.opponentPlayer = opponentPlayer;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public char getPlayerSymbol() {
        return playerSymbol;
    }

    public void setPlayerSymbol(char playerSymbol) {
        this.playerSymbol = playerSymbol;
    }

    private void startPlayingTheGame() {

        try {
            sendMessage("Please enter your name");
            this.setPlayerName((String)objectInputStream.readObject());
            System.out.println("player " + this.getPlayerName() + " joined the game");

            sendMessage("Hello " + this.getPlayerName());
            //Set player names for the game
            if (this.getPlayerId() == 1) {
                this.getGame().setPlayerOneName(this.getPlayerName());
            }else{
                this.getGame().setPlayerTwoName(this.getPlayerName());
            }

            sendMessage("Your Token is: " + this.getPlayerSymbol());
            String opponentName = "";
            if (this.getPlayerId() == 1) {
                opponentName = this.getGame().getPlayerTwoName();
            }else{
                opponentName = this.getGame().getPlayerOneName();
            }
            sendMessage("Your Opponent is: "+ opponentName + ", If name is empty, opponent haven't selected name yet.");

            //Make note the game has started
            this.getGame().setGameInProgress(true);
            String gameMessage = "";
            do {
                gameMessage = (String)objectInputStream.readObject();

                switch (gameMessage) {
                    case "makeAMove":
                        moveRequested();
                        break;
                    case "showGameBoardState":
                        returnGameBoardState();
                        break;
                    case "exitGame":
                        endClientConnection();
                        break;
                }
            } while (!gameMessage.equalsIgnoreCase("exitGame"));

        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
    }

    private void moveRequested() {
        boolean canMakeAMove = false;
        int columnSelected = 0;
        //Check whose turn it is
        int currentPlayerTurn = this.getGame().getCurrentPlayerTurn();

        //If current player client ID is same as the turn id it is that client's turn
        if (this.getPlayerId() == currentPlayerTurn) {
            sendMessage("okToMove");
            canMakeAMove = true;
        }

        if (canMakeAMove) {
            //Get the column number
            try {
                columnSelected = Integer.parseInt((String)objectInputStream.readObject());

                char currentPlayerToken;
                if (currentPlayerTurn == 1) {
                    currentPlayerToken = 'X';
                }else{
                    currentPlayerToken = 'O';
                }

                char[] currentColumn = this.getGame().returnSelectedColumn((columnSelected - 1));

                for (int rowId = 0; rowId < currentColumn.length; rowId++) {
                    System.out.println("Current content: "+ currentColumn[rowId]);
                    if (currentColumn[rowId] != 'X' && currentColumn[rowId] != 'O') {
                        System.out.println("Found empty spot at "+ rowId);
                        this.getGame().setPlayerToken(rowId, (columnSelected - 1), currentPlayerToken);
                        sendMessage("moveOk");
                        //Set other players turn now
                        if (currentPlayerTurn == 1) {
                            this.getGame().setCurrentPlayerTurn(2);
                        }else{
                            this.getGame().setCurrentPlayerTurn(1);
                        }
                        break;
                    }else if(rowId == 5 && (currentColumn[rowId] == 'X' || currentColumn[rowId] == 'O')){
                        System.out.println("All spots in this column are taken");
                        sendMessage("redoMove");
                        break;
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }else{
            sendMessage("notYourTurn");
        }
    }

    private void returnGameBoardState() {
        message = this.getGame().getGameBoardState();
        sendMessage(message);
    }


    private void endClientConnection() {
        message = "exit";
        sendMessage(message);

        System.out.println("player "+this.playerName+" left the game");
    }

    private void sendMessage(String message) {
        try{
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}