package bte.burakstexteditor;

import com.sun.javafx.css.StyleCacheEntry;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class SimpleNotepad extends Application{
    private TextArea textArea = new TextArea();
    private File currentFile = null;
    private boolean isModified = false;
    private Stage primaryStage;

    private Label lineColLabel = new Label("Line Column");
    private Label charCountLabel = new Label("Char Count");

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("BuraksTextEditor");
        this.primaryStage = stage;
        primaryStage.setTitle("BuraksTextEditor");
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "false");
        }
        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        CheckMenuItem darkModeItem = new CheckMenuItem("Dark Mode");
        viewMenu.getItems().add(darkModeItem);
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem selectAllItem = new MenuItem("Select All");
        cutItem.setOnAction( e -> textArea.cut());
        copyItem.setOnAction( e -> textArea.copy());
        pasteItem.setOnAction( e -> textArea.paste());
        selectAllItem.setOnAction( e -> textArea.selectAll());
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        undoItem.setOnAction(e -> textArea.undo());
        redoItem.setOnAction(e -> textArea.redo());
        undoItem.setAccelerator(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN)
        );
        redoItem.setAccelerator(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN)
        );

        cutItem.setAccelerator(
          new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN)
        );

        copyItem.setAccelerator(
                new KeyCodeCombination( KeyCode.C,KeyCombination.CONTROL_DOWN)
        );

        pasteItem.setAccelerator(
                new KeyCodeCombination(KeyCode.V,KeyCombination.CONTROL_DOWN)
        );

        selectAllItem.setAccelerator(
                new KeyCodeCombination(KeyCode.A,KeyCombination.CONTROL_DOWN)
        );


        editMenu.getItems().addAll(undoItem, redoItem, new SeparatorMenuItem(), cutItem, copyItem, pasteItem, selectAllItem);

        HBox statusBar = new HBox();
        statusBar.setSpacing(20);
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #e2e2e2");
        statusBar.getChildren().addAll(lineColLabel,charCountLabel);

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exitApp = new MenuItem("Exit");

        newFile.setAccelerator(
                new KeyCodeCombination(KeyCode.N,KeyCombination.CONTROL_DOWN)
        );

        openFile.setAccelerator(
                new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN)
        );

        saveFile.setAccelerator(
                new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN)
        );



        newFile.setOnAction(e -> newFile(stage));
        openFile.setOnAction(e -> openFile(stage));
        saveFile.setOnAction(e -> saveFile(stage));
        exitApp.setOnAction(e -> {
            if (!confirmUnsavedChanged(stage)){
                stage.close();
            }
        });



        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exitApp);
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);
        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(textArea);
        layout.setBottom(statusBar);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            isModified = true;
            updateStatusBar();
            updateWindowTitle();
        });
        textArea.caretPositionProperty().addListener((obs, oldPos, newPos) -> {
            updateStatusBar();
        });

        exitApp.setAccelerator(
                new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)
        );


        Scene scene = new Scene(layout, 600, 400);
        darkModeItem.setOnAction(e -> {
            if (darkModeItem.isSelected()) {
                scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().clear(); // clears all (simplest)
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    private void newFile(Stage stage){
        if (!confirmUnsavedChanged(stage)) return;

        textArea.clear();
        currentFile = null;
        isModified = false;
        updateWindowTitle();
    }

    private void openFile(Stage stage){
        if (!confirmUnsavedChanged(stage)) return ;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files (*.txt)",  "*.txt"),
                new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md"),
                new FileChooser.ExtensionFilter("Log Files (*.log)", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                textArea.setText(reader.lines().reduce("", (a,b) -> a + "\n" + b));
                currentFile = file;
                primaryStage.setTitle("BuraksTextEditor - " + currentFile.getName());
                isModified = false;
                updateWindowTitle();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private void saveFile(Stage stage){
        if (currentFile == null){
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files (*.txt)",  "*.txt"),
                    new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md"),
                    new FileChooser.ExtensionFilter("Log Files (*.log)", "*.log"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
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
        isModified = false;
        updateWindowTitle();
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
    private void updateStatusBar() {
        String text = textArea.getText();
        int caretPos = textArea.getCaretPosition();

        // Safety: caretPos can be > text.length() for a few ms during deletion
        if (caretPos > text.length()) {
            caretPos = text.length();
        }
        if (caretPos < 0) caretPos = 0;

        // Character count
        int charCount = text.length();
        charCountLabel.setText("Char Count: " + charCount);

        // If empty, reset safely
        if (text.isEmpty()) {
            lineColLabel.setText("Line: 1, Col: 1");
            return;
        }

        // LINE NUMBER (safe)
        int line = text.substring(0, caretPos).split("\n", -1).length;

        // COLUMN NUMBER (safe)
        int lastNewLine = text.lastIndexOf('\n', caretPos - 1);
        int column = caretPos - (lastNewLine + 1);
        if (column < 0) column = 0;

        lineColLabel.setText("Line: " + line + ", Col: " + (column + 1));
    }

    private void updateWindowTitle(){
        String filename = (currentFile == null)
                ? "Untitled"
                : currentFile.getName();

        String modifiedMark = isModified ? " *":"";
        primaryStage.setTitle("BuraksTextEditor - " + filename + modifiedMark);
    }



}

