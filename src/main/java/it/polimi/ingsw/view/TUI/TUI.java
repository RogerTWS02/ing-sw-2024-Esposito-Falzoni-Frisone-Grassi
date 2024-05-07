package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageListener;

import java.util.Scanner;

import static it.polimi.ingsw.network.message.MessageType.*;

public class TUI implements MessageListener {
    public static Client cli;

    @Override
    public Message onMessageReceived(Message message) {
        return message;
    }
    public static void main(String[] args) {
        // Inizializza lo scanner per leggere da console
        Scanner scanner = new Scanner(System.in);
        String[] command;
        String message;

        while(true){
            //stampo il layout

            //chiedo all'utente di inserire un comando
            System.out.println("Inserisci un comando (/help per lista comandi):");

            //Leggi il messaggio inserito dall'utente
            command = scanner.nextLine().split(" ");
            switch(command[0]){
                case "/help":
                    message = "Lista dei comandi: \n" +
                              "/infoCard posX posY - restituisce info su una carta nel PlayerBoard\n"+
                              "/placeCard posX posY - prova a posizionare una carta nel Playerboard\n"+
                              "/drawCardFromDeck Golden/Resource - pesca dal deck in base al tipo\n"+
                              "/drawCardFromViewable - pesca dalle carte visibili\n"+
                              "/TEMPLATE PARAM_1, PARAM_2\n";

                    cli.sendMessage(
                            new Message(
                                    TEST_MESSAGE,
                                    cli.getSocketPort(),
                                    -1,
                                    message)
                    );

                //chiedo l'UUID della carta al server e genero i dati dal JSON
                // messaggio del tipo: /infoCard posX posY
                case "/infoCard":
                    if(command.length < 3) {
                        System.out.println("Comando non segue la sintassi corretta (prova /help)");
                        break;
                    }
                    int posX = 0, posY = 0;
                    try {
                        posX = Integer.parseInt(command[1]);
                        posY = Integer.parseInt(command[2]);

                    }catch(NumberFormatException e){
                        System.out.println(e);
                        break;
                    }

                    //mando la richiesta di info
                    cli.sendMessage(
                            new Message(
                                    REQUEST_INFO_CARD,
                                    cli.getSocketPort(),
                                    cli.getGameID(),
                                    new Object[]{posX, posY})
                    );
            }


            if(command.equals("exit")){
                // Chiudo lo scanner
                scanner.close();
                break;
            }
        }
    }
}
