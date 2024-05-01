package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client  {
    private final String ipServ;
    private final int port;
    private Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream inp;

    public Client(String ip, int port) {
        this.ipServ = ip;
        this.port = port;
    }


    public Thread readFromSocketAsync(ObjectInputStream socketInput){
        Thread t = new Thread(() -> {
            try{
                while(true){
                    Message recievedMessage = (Message) socketInput.readObject();
                    //per debugging
                    System.out.println(recievedMessage.getObj().toString());
                }
            }catch(Exception ignored){}
        });
        t.start();
        return t;
    }


    //funzione per mandare un messaggio al server riutilizzando out
    public synchronized void sendMessage(Message message){
        new Thread(() -> {
            try{
                System.out.println("Messaggio mandato: "+message);
                out.reset();
                out.writeObject((Object)message);
                out.flush();

            }catch(IOException e){
                System.out.println("Ci sono problemi nel mandare i messaggi! "+ e.getMessage());
            };

        }).start();
    }

    // funzione per leggere in ingresso i messaggi del Server e inizializzare out
    public void run() throws IOException {
        this.socket = new Socket(ipServ, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        inp = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connessione stabilita");

        try{
            System.out.println("Il messaggio del server è:\n");
            readFromSocketAsync(inp);
        }catch (Exception e){
            System.out.println("Il client non funziona, motivo: "+e);
            closeSocket();
        }finally {
            closeSocket();
        }

    }

    //funzione per chiudere la connessione
    public synchronized void closeSocket(){
        try{
            socket.close();
        }catch(IOException ignored){}
        System.exit(0);
    }


}
