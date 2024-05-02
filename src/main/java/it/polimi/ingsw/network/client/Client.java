package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client  {
    private final String ipServ;
    private final int port;
    private Socket socket;
    private int gameID;
    protected ObjectOutputStream out;
    protected ObjectInputStream inp;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public Client(String ip, int port) {
        this.ipServ = ip;
        this.port = port;
    }


    public void readFromSocketAsync(ObjectInputStream socketInput){
        Thread t = new Thread(() -> {
            try{
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        Message recievedMessage = (Message) socketInput.readObject();
                        //per debugging
                        System.out.println(recievedMessage.getObj().toString());
                    }catch (IOException | ClassNotFoundException e) {
                    }
                }
            }finally {
                closeSocket();
            }
        });
        t.start();
    }


    //funzione per mandare un messaggio al server riutilizzando out
    public synchronized void sendMessage(Message message){
        new Thread(() -> {
            try{
                logger.log(Level.INFO, "Sending message to server");
                out.reset();
                out.writeObject(message);
                out.flush();

            }catch(IOException e){
                logger.log(Level.SEVERE, "Error in sending message to server");
            };

        }).start();
    }

    public int getSocketPort() {
        return socket.getPort();
    }

    // funzione per leggere in ingresso i messaggi del Server e inizializzare out
    public void run() throws IOException {
        try{
            this.socket = new Socket(ipServ, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            inp = new ObjectInputStream(socket.getInputStream());
            logger.log(Level.INFO, "Client has connected to the server");

            readFromSocketAsync(inp);
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error in reading from socket");
            closeSocket();
        }

    }

    //funzione per chiudere la connessione
    public synchronized void closeSocket(){
        try{
            inp.close();
            out.close();
            socket.close();
        }catch(IOException ignored){}
        System.exit(0);
    }


}
