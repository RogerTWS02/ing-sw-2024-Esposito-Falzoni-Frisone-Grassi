module group.sweng.ingsw2024espositofalzonifrisonegrassi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens group.sweng.ingsw2024espositofalzonifrisonegrassi to javafx.fxml;
    exports group.sweng.ingsw2024espositofalzonifrisonegrassi;
}