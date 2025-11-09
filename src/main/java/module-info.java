module com.example.imnotarobot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.imnotarobot to javafx.fxml;
    exports com.example.imnotarobot;
}