package it.polimi.ingsw.view.GUI.controllers;
import it.polimi.ingsw.view.GUI.GuiApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;


public class ChatController implements Initializable {

    public TextArea textArea;
    public Label chatHistory;
    public Button closeButton;
    public AnchorPane chatRoom;

    public void updateMessage(StringBuilder chatMsg){
        String str = chatMsg.toString().replaceAll("\\033\\[38;5;208m", "").replaceAll("\\033\\[0m", "");
        Platform.runLater(() -> {
            chatHistory.setText(str);
        });
    }

    /**
     * Sends a chat message to the server.
     *
     * @param msg The message to send.
     */
    public void sendChatMessage(String msg){
        GuiApp.getGui().sendChatMessage(msg);
    }

    /**
     * Initializes the chat controller.
     *
     * @param url Ignored.
     * @param resourceBundle Ignored.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set the listener for the text field
        textArea.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendChatMessage(textArea.getText());
                //then reset the textField
                Platform.runLater(() -> {
                    textArea.setText("");
                });
            }
        });
    }

    /**
     * Handles the event of the close button being pressed.
     *
     * @param actionEvent Ignored.
     */
    public void closeButtonPressed(ActionEvent actionEvent) {
        GuiApp.changeScene(GuiApp.getMainPlayerViewRoot());
    }
}
