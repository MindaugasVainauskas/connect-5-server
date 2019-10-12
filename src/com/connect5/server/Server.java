package com.connect5.server;

import com.connect5.game.Game;
import com.connect5.player.Player;

import java.io.*;
import java.net.ServerSocket;

public class Server {
    private ServerSocket serverSocket;
    private int playerCount;

    public Server(){
        System.out.println("5 In Line Game Server");
        playerCount = 0;

        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException ioException) {
            System.out.println("Exception Occurred: "+ ioException);
        }
    }

    public void startGameSession(){
        try {
            System.out.println("Waiting for Players");
            while (true) {
                Game game = new Game();
                playerCount++;
                Player playerOne = new Player(serverSocket.accept(), game, 'X', playerCount);

                playerCount++;
                Player playerTwo = new Player(serverSocket.accept(), game, 'O', playerCount);

                playerOne.start();
                playerTwo.start();

                //Set the opponent for appropriate player
                playerOne.setOpponentPlayer(playerTwo);
                playerTwo.setOpponentPlayer(playerOne);

                game.startTheGame(1);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally{
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

