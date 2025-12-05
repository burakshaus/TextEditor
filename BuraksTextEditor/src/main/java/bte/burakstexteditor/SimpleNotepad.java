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

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("BuraksTextEditor");

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exitApp = new MenuItem("Exit");

        newFile.setOnAction(e -> newFile());
        openFile.setOnAction(e -> openFile(stage));
        saveFile.setOnAction(e -> saveFile(stage));
        exitApp.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exitApp);
        menuBar.getMenus().add(fileMenu);

        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(textArea);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void newFile(){
        textArea.clear();
        currentFile = null;
    }

    private void openFile(Stage stage){
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
    }
}

