package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageListener;
import it.polimi.ingsw.network.server.RMIServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client  {

    private MessageListener messageListener;
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

                        //inoltro il messaggio al client estraendo dal tipo di interfaccia
                        messageListener.onMessageReceived(recievedMessage);
                        //per debugging
                        //System.out.println(recievedMessage.getObj().toString());
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
    public void run(boolean useSocket){
        if(useSocket){
            try{
                this.socket = new Socket(ipServ, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                inp = new ObjectInputStream(socket.getInputStream());
                logger.log(Level.INFO, "Client has connected to the server");

                readFromSocketAsync(inp);
            }catch (IOException e){
                logger.log(Level.SEVERE, "Error in reading from socket");
                closeSocket();
            }
        }else{
            try{
                Registry registry = LocateRegistry.getRegistry(ipServ, port);
                RMIServerInterface stub = (RMIServerInterface) LocateRegistry.getRegistry(ipServ, port).lookup("Codex_server");
                logger.log(Level.INFO, "Client has connected to the server using RMI");
                playGame(stub);
            }catch (RemoteException | NotBoundException e) {
                logger.log(Level.SEVERE, "Error in connecting to server using RMI");
                closeSocket();
            }
        }

    }

    public void playGame(RMIServerInterface stub) {

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

    public int getGameID() {
        return gameID;
    }
}
