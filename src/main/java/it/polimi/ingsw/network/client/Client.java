package it.polimi.ingsw.network.client;

import com.sun.source.tree.TryTree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client  {
    private final String  ip;
    private final int port;
    private final Socket socket;

    protected ObjectOutputStream out;
    protected ObjectInputStream inp;

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socket = new Socket(ip, port);
    }


    public Thread readFromSocketAsync(ObjectInputStream socketInput){
        Thread t = new Thread(() -> {
            try{
                while(true){
                    String recievedMessage = (String) socketInput.readObject();

                }

            }catch(Exception ignored){}

        });
        t.start();
        return t;
    }

    public synchronized void sendMessage(String message){
        new Thread(() -> {
            try{
                out.reset();
                out.writeObject((Object)message);
                out.flush();

            }catch(IOException e){
                System.out.println("Ci sono problemi nel mandare i messaggi! "+ e.getMessage());
            };

        }).start();
    }

    public void run(){
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            inp = new ObjectInputStream(socket.getInputStream());
            readFromSocketAsync(inp);
        }catch (Exception ignored){

        }finally {
            closeSocket();
        }

    }

    public synchronized void closeSocket(){
        try{
            socket.close();
        }catch(IOException ignored){}
        System.exit(0);
    }


}
