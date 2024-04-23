package it.polimi.ingsw.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int default_port = 666;
    private final ServerSocket serverSocket;
    protected final Object lock = new Object();

    public Server(InetAddress ip, int port) throws IOException {
        this.serverSocket = new ServerSocket(port, 66, ip);

    }

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(default_port);
    }


    public void run(){
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();

            }catch (Exception ignored){}
        }
    }
}
