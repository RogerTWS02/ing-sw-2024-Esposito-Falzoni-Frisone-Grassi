package it.polimi.ingsw.network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int default_port = 666;
    private final ServerSocket serverSocket;

    // prende in ingresso indirizzo di rete e porta, oppure usa la porta di default
    // e genero il server
    public Server(InetAddress ip, int port) throws IOException {
        this.serverSocket = new ServerSocket(port, 66, ip);

    }
    public Server() throws IOException {
        this.serverSocket = new ServerSocket(default_port);
    }


    // funzione che permette al server di accettare connesioni dai client
    public void run(){
        System.out.println("Il server è avviato e attende che i client si connettano...");
        try{
            while(true){
                    //uso un clientHandler per evitare azioni bloccanti dal client
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandler.start();
            }
        }catch (Exception ignored){}
    }
}
