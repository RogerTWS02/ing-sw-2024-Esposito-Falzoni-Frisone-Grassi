module GUI {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.polimi.ingsw.view.GUI to javafx.fxml;
    exports it.polimi.ingsw.view.GUI;
}