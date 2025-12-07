package bte.burakstexteditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.*;

public class SimpleNotepad extends Application{
    private TextArea textArea = new TextArea();
    private File currentFile = null;
    private boolean isModified = false;
    private Stage primaryStage;

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("BuraksTextEditor");
        this.primaryStage = stage;
        primaryStage.setTitle("BuraksTextEditor");
        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");

        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem selectAllItem = new MenuItem("Select All");
        cutItem.setOnAction( e -> textArea.cut());
        copyItem.setOnAction( e -> textArea.copy());
        pasteItem.setOnAction( e -> textArea.paste());
        selectAllItem.setOnAction( e -> textArea.selectAll());

        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, selectAllItem);

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exitApp = new MenuItem("Exit");

        newFile.setOnAction(e -> newFile(stage));
        openFile.setOnAction(e -> openFile(stage));
        saveFile.setOnAction(e -> saveFile(stage));
        exitApp.setOnAction(e -> {
            if (!confirmUnsavedChanged(stage)){
                stage.close();
            }
        });

        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exitApp);
        menuBar.getMenus().addAll(fileMenu, editMenu);
        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(textArea);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            isModified = true;
        });

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void newFile(Stage stage){
        if (!confirmUnsavedChanged(stage)) return ;
        textArea.clear();
        primaryStage.setTitle("BuraksTextEditor - " + currentFile.getName());
        currentFile = null;
    }

    private void openFile(Stage stage){
        if (!confirmUnsavedChanged(stage)) return ;
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                textArea.setText(reader.lines().reduce("", (a,b) -> a + "\n" + b));
                currentFile = file;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        primaryStage.setTitle("BuraksTextEditor - " + currentFile.getName());
    }
    private void saveFile(Stage stage){
        if (currentFile == null){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            currentFile = fileChooser.showSaveDialog(stage);
        }
        if (currentFile != null){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))){
                writer.write(textArea.getText());
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        primaryStage.setTitle("BuraksTextEditor - " + currentFile.getName());
    }
    private boolean confirmUnsavedChanged(Stage stage){
        if (!isModified) return true;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Are you sure you want to continue?");

        ButtonType saveBtn = new ButtonType("Save");
        ButtonType dontSaveBtn = new ButtonType("Don't Save");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveBtn, dontSaveBtn, cancelBtn);
        var result = alert.showAndWait().orElse(cancelBtn);

        if (result == saveBtn){
            saveFile(stage);
            return true;
        } else if (result == dontSaveBtn){
            return true;
        } else{
            return false;
        }
    }
}

