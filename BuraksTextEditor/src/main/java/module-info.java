module bte.burakstexteditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
//    requires eu.hansolo.tilesfx;

    opens bte.burakstexteditor to javafx.fxml;
    exports bte.burakstexteditor;
}