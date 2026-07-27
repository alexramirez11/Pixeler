import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;
import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

/**
 * This class represents the scene that holds everything for the lite brite together. This class extends the Scene class.
 */
public class PixelScene extends Scene {

    private static final String APP_NAME = "Pixeler";
    private static final Path APP_DATA_DIR = Paths.get(System.getenv("LOCALAPPDATA"), APP_NAME);
    private static final Path SAVED_IMAGES_DIR = APP_DATA_DIR.resolve("Saved_Images");
    private static final Path SAVED_IMAGES_DATA_DIR = APP_DATA_DIR.resolve("Saved_Images_Data");

    private final Color FONT_COLOR = Color.WHITE;

    private static BorderPane root;
    private Label startMes, buildMes, canvasName, saveMessage;
    private Button startButton, createButton, loadButton, change, toggleGrid, flip, discardButton;
    private VBox startBox, settingsBox, sideMenu;
    private HBox colorTools;
    private ComboBox<String> colorsBox;
    private TextField widthText, heightText;
    private ColorPicker picker;
    private PixelPane pixelPane;
    private ScrollPane scrollPane;
    private LinkedList<Button> loadList;
    private Image bucketOnImage = new Image("assets/bucket-on.png");
    private Image bucketOffImage = new Image("assets/bucket-off.png");
    private ImageView bucketView = new ImageView(bucketOffImage);
    private Image magnifyOnImage = new Image("assets/magnifying_on.png");
    private Image magnifyOffImage = new Image("assets/magnifying_off.png");
    private ImageView finderView = new ImageView(magnifyOffImage);
    private MenuButton saveOptions;
    
    /**
     * This constructor is used to create an instance of the pixel board.
     * @return A PixelScene object to run the program
     */
    public static Scene initPixelScene() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: black");
        return new PixelScene(root);
    }

    private PixelScene(BorderPane root) {
        super(root, (Screen.getPrimary().getBounds().getWidth() - 100), (Screen.getPrimary().getBounds().getHeight() - 100), Color.BLACK);

        initUserDirs();
        initStartMenu();
    }

    private void initUserDirs() {
        try {
            Files.createDirectories(SAVED_IMAGES_DIR);
            Files.createDirectories(SAVED_IMAGES_DATA_DIR);
        } catch (IOException e) {
            showErrorDialog("Unable to initialize storage", "Pixeler could not create its save directories\n\n", e);
        }
    }

    private void showErrorDialog(String title, String mes, Exception e) {
        Alert alert= new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(mes);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    /**
     * This method initializes the start menu when the application is executed.
     */
    private void initStartMenu() {
        startMes = new Label("Pixel Art");
        startMes.setAlignment(Pos.CENTER);
        startMes.setTextAlignment(TextAlignment.CENTER);
        startMes.setTextFill(FONT_COLOR);
        startMes.setMaxWidth(Double.MAX_VALUE);
        startMes.setFont(new Font(100));
        startButton = new Button("Start Project");
        startButton.setStyle("-fx-background-color: purple");
        startButton.setTextFill(FONT_COLOR);
        startButton.setAlignment(Pos.CENTER);
        startButton.setOnAction(this::processStart);
        startButton.setFont(new Font(60));

        startButton.maxWidthProperty().bind(root.widthProperty().multiply(0.6));

        startBox = new VBox(startMes, startButton);
        startBox.setAlignment(Pos.CENTER);
        startBox.setSpacing(100);
        VBox.setVgrow(startMes, Priority.ALWAYS);
        VBox.setVgrow(startButton, Priority.NEVER);
        root.setCenter(startBox);
        root.setRight(null);
    }

    /**
     * This method initializes the build menu for where the user configures their canvas' settings
     */
    private void initBuildMenu() {
        buildMes = new Label("Configure your canvas settings");
        buildMes.setAlignment(Pos.CENTER);
        buildMes.setTextAlignment(TextAlignment.CENTER);
        buildMes.setFont(new Font(50));
        buildMes.setMaxWidth(Double.MAX_VALUE);
        buildMes.setTextFill(FONT_COLOR);
        GridPane pane = new GridPane();
        Label wLabel = new Label("Columns: ");
        wLabel.setTextFill(FONT_COLOR);
        wLabel.setFont(new Font(30));
        Label hLabel = new Label("Rows: ");
        hLabel.setTextFill(FONT_COLOR);
        hLabel.setFont(new Font(30));
        widthText = new TextField();
        widthText.setStyle("-fx-background-color: darkorange");
        heightText = new TextField();
        heightText.setStyle("-fx-background-color: darkorange");
        Label colorLabel = new Label("Color: ");
        colorLabel.setTextFill(FONT_COLOR);
        colorLabel.setPrefSize(50, 50);
        colorsBox = new ComboBox<String>();
        colorsBox.getItems().addAll("black", "white", "orange", "red", "blue", "cyan", "yellow", "pink", "purple", "green", "lightgreen", "grey", "dimgrey");
        pane.add(wLabel, 0, 0);
        pane.add(hLabel, 0, 1);
        pane.add(widthText, 1, 0);
        pane.add(heightText, 1, 1);
        pane.add(colorLabel, 0, 3);
        pane.add(colorsBox, 1, 3);
        pane.setVgap(20);
        pane.setAlignment(Pos.CENTER);

        loadButton = new Button("Load Existing Canvas");
        loadButton.setStyle("-fx-background-color: orange");
        loadButton.setAlignment(Pos.CENTER);
        loadButton.setTextFill(FONT_COLOR);
        loadButton.setMaxWidth(800);
        loadButton.setPrefHeight(100);
        loadButton.setOnAction(this::loadSettings);
        loadButton.setFont(new Font(60));

        createButton = new Button("Create Canvas");
        createButton.setStyle("-fx-background-color: purple");
        createButton.setAlignment(Pos.CENTER);
        createButton.setTextFill(FONT_COLOR);
        createButton.setMaxWidth(800);
        createButton.setPrefHeight(100);
        createButton.setOnAction(this::processBuild);
        createButton.setFont(new Font(60));

        settingsBox = new VBox(buildMes, pane, createButton, loadButton);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setSpacing(30);
        VBox.setVgrow(buildMes, Priority.NEVER);
        VBox.setVgrow(pane, Priority.NEVER);
        VBox.setVgrow(buildMes, Priority.NEVER);
        VBox.setVgrow(buildMes, Priority.NEVER);
        root.setCenter(settingsBox);
        root.setRight(null);
    }

    /**
     * This method initializes the grid.
     */
    private void initGrid() {
        picker = new ColorPicker();
        int height = 0, width = 0;
        try {
            height = Integer.parseInt(heightText.getText());
            width = Integer.parseInt(widthText.getText());
        } catch (NumberFormatException exception) {
            height = 10;
            width = 10;
        }
        
        pixelPane = new PixelPane(height, width, colorsBox.getValue(), picker);
        initTools(pixelPane);
    }

    private void processChangeOnBackBoard(ActionEvent e) {
        if (colorsBox.getValue() == null) {
            colorsBox.setValue("black");
        }
        String col = colorsBox.getValue().toLowerCase().trim();
        pixelPane.setBackgroundColor(col);
        pixelPane.drawGrid();
    }

    private void processDiscard(ActionEvent e) {
        saveOptions.hide();
        if (pixelPane.hasChanged()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Exit");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Are you sure you want to exit without saving?");
            
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.NO) {
                return;
            }
        }
        Platform.runLater(() -> {initBuildMenu();});
    }

    /**
     * This method takes the user to the build menu after clicking the Start Project button.
     * @param e ActionEvent object to activate this method when a button is clicked
     */
    private void processStart(ActionEvent e) {
        initBuildMenu();
    }

    /**
     * This method takes the user to the actual lite brite screen after finishing their settings and hittin the Create Canvas button.
     * @param e ActionEvent object to activate this method when a button is clicked
     */
    private void processBuild(ActionEvent e) {
        initGrid();
    }

    /**
     * This method opens a TextInputDialog box to save the current canvas, after the user hits the save button.
     * @param e ActionEvent object to activate this method when a button is clicked
     */
    private void saveSettings(ActionEvent e) {
        TextInputDialog saveDialog = new TextInputDialog();
        saveDialog.setTitle("Save Image");
        saveDialog.setHeaderText("Save Image As: ");
        saveDialog.setContentText("Image Name: ");
        Optional<String> save = saveDialog.showAndWait();
        if (save.isPresent()) {
            processSave(save.get());
        }
    }

    private void saveNoExit(ActionEvent e) {
        processSave(pixelPane + "");
    }

    /**
     * This method opens a TextInputDialog box to load an exsisting canvas from the Saved_Images_Data folder that should be in this directory. 
     * This happens once the user clickes on the load existing canvas button.
     * @param e ActionEvent object to activate this method when a button is clicked
     */
    private void loadSettings(ActionEvent e) {
        Label loadLabel = new Label("Select the Canvas you Wish to Edit");
        loadLabel.setAlignment(Pos.CENTER);
        loadLabel.setTextAlignment(TextAlignment.CENTER);
        loadLabel.setFont(new Font(50));
        loadLabel.setTextFill(FONT_COLOR);
        VBox loadFiles = new VBox();
        File dir = SAVED_IMAGES_DATA_DIR.toFile();
        loadList = new LinkedList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    Path img = SAVED_IMAGES_DIR.resolve(f.getName().replace(".txt", ".png"));
                    Button selectable = new Button(f.getName().replace(".txt", ".png"));
                    try {
                        Image image = new Image(img.toUri().toString());
                        BackgroundImage bg_img = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                        Background bg = new Background(bg_img);
                        
                        selectable.setBackground(bg);
                    } catch (IllegalArgumentException exception) {
                        selectable.setStyle("-fx-background-color: black");
                    }
                    selectable.minWidthProperty().bind(root.widthProperty().multiply(0.6));
                    selectable.minHeightProperty().bind(root.heightProperty().multiply(0.8));
                    selectable.setTextFill(Color.WHITE);
                    selectable.setFont(new Font(50));
                    selectable.setBorder(new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    selectable.setOnAction(this::processLoadClick);
                    loadList.add(selectable);
                }
            } else {
                System.err.println("Directory containing each saved image's data could not be found.\nProgram shutting down...");
                System.exit(1);
            }
        } else {
            System.err.println("Directory containing each saved image's data could not be found.\nProgram shutting down...");
            System.exit(1);
        }
        loadFiles.getChildren().addAll(loadList);
        loadFiles.setStyle("-fx-background-color: black");
        loadFiles.setSpacing(20);
        loadFiles.setAlignment(Pos.CENTER);

        ScrollPane sp = new ScrollPane(loadFiles);
        sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);

        root.setCenter(sp);
        root.setRight(null);
    }

    private void processLoadClick(ActionEvent event) {
        String name = "";
        for (Button b : loadList) {
            if (event.getSource() == b) {
                name = b.getText().replace(".png", "");
                break;
            }
        }
        processLoad(name);
    }

    /**
     * This method loads the specifed existing canvas and rebuilds it into an instance of a LBPane so that the user can edit the image.
     * @param fileName String of the name of the file of the existing image
     */
    private void processLoad(String fileName) {
        File input = SAVED_IMAGES_DATA_DIR.resolve(fileName + ".txt").toFile();
        picker = new ColorPicker();
        try {
            Scanner scan = new Scanner(input);
            LinkedList<String> metaData = new LinkedList<String>();
            while (scan.hasNextLine()) {
                metaData.add(scan.nextLine());
            }
            scan.close();
            pixelPane = new PixelPane(metaData, picker, fileName);
            initTools(pixelPane);
        } catch (FileNotFoundException e) {
            System.err.println("FILE \"" + fileName + "\" DOES NOT EXIST. PLEASE ENSURE THE GIVEN FILE NAME IS CORRECT\nProgram shutting down...\n");
            System.exit(1);
        }
    }

    private void processBucketClick(MouseEvent e) {
        if (!pixelPane.getBucketMode()) {
            pixelPane.setBucket(true);
            pixelPane.setFinder(false);
            finderView.setImage(magnifyOffImage);
            bucketView.setImage(bucketOnImage);
        } else {
            pixelPane.setBucket(false);
            bucketView.setImage(bucketOffImage);
        }
    }

    private void processFinderClick(MouseEvent e) {
        if (!pixelPane.getFinderMode()) {
            pixelPane.setFinder(true);
            finderView.setImage(magnifyOnImage);
            pixelPane.setBucket(false);
            bucketView.setImage(bucketOffImage);
        } else {
            pixelPane.setFinder(false);
            finderView.setImage(magnifyOffImage);
        }
    }

    private void processGridToggle(ActionEvent e) {
        pixelPane.toggleGridLines();
    }

    private void processFlip(ActionEvent e) {
        pixelPane.flipHorizontal();
    }

    private void processExport(ActionEvent e) {
        Dialog<ExportSettings> dialog = new Dialog<>();
        dialog.setTitle("Export PNG");

        ButtonType exportButton = new ButtonType("Export", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(exportButton, ButtonType.CANCEL);
        ColorPicker removePicker = new ColorPicker(Color.WHITE);
        CheckBox removeCheck = new CheckBox("Make this color transparent");

        ComboBox<Integer> box = new ComboBox<>(); 
        box.getItems().addAll(1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 24);
        box.setValue(1);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Transparency Color"), 0, 0);
        grid.add(removePicker, 1, 0);
        grid.add(removeCheck, 1, 1);
        grid.add(new Label("Scale Value (nearest neighbor)"), 0, 2);
        grid.add(box, 1, 2);
        grid.add(new Label("This image will be saved in your Downloads directory"), 0, 3);
        
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == exportButton) {
                return new ExportSettings(removePicker.getValue(), removeCheck.isSelected(), box.getValue());
            }
            return null;
        });

        ExportSettings settings = dialog.showAndWait().orElse(null);
        if (settings == null) {
            return;
        }

        exportOnly(pixelPane + "", settings, true);
    }

    private void exportOnly(String name, ExportSettings settings, boolean toDownloads) {
        WritableImage image = pixelPane.exportImage(settings.isTransparent(), settings.getTransparentValue(), settings.scaleByValue());

        File output = SAVED_IMAGES_DIR.resolve(name + ".png").toFile();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
            if (toDownloads) {
                String userHome = System.getProperty("user.home");
                File downloadsDir = new File(userHome, "Downloads");
                File copy = new File(downloadsDir, name + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", copy);
            }
        } catch (IOException exception) {
            showErrorDialog("Export Failed", "Pixeler could not export your image to the Downloads folder.\n\n Your image was not saved. Please try again", exception);
        }
    }

    /**
     * This method saves the current PixelPane open. A png screenshot of the PixelPane is saved in the Saved_Images folder
     * and the metadata for it is saved in the Saved_Images_Data folder, both of which should be in this directory.
     * @param name String representation of the name of the PixelPane to save
     */
    private void processSave(String name) {
        File boardData = SAVED_IMAGES_DATA_DIR.resolve(name + ".txt").toFile();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
        pixelPane.setLastSavedTime(now.format(formatter));
        saveMessage.setText("Last saved on:\n" + pixelPane.getLastSavedTime());
        try {
            FileWriter writer = new FileWriter(boardData);
            Color[][] board = pixelPane.getGridColors();
            writer.write(pixelPane.getLastSavedTime() + "\n");
            writer.write(pixelPane.getGridLineColor() + "\n");
            writer.write(pixelPane.getRows() + "/" + pixelPane.getColumns() + "\n");
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    writer.write(i + "//" + j + "//" + colorToString(board[i][j]) + "\n");
                }
            }
            writer.close();
            pixelPane.setName(name);
            canvasName.setText("Canvas Name:\n" + pixelPane);
            exportOnly(name, new ExportSettings(FONT_COLOR, false, 1), false);
            pixelPane.setHasChanged(false);
        } catch (IOException e) {
            showErrorDialog("Save Failed", "Pixeler was unable to save your canvas.\n\nYour changes have not been saved. Please check that you have enough storage space and permission to save files, then try again.\n", e);
        }
    }

    private String colorToString(Color c) {
        return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }

    /**
     * This method initializes all of the tools that work on the given PixelPane object. It also initializes all of the
     * information about the pane and displays it.
     * @param pane The PixelPane to initialize tools for
     */
    private void initTools(PixelPane pane) {
        bucketView.setPreserveRatio(true);
        bucketView.setFitWidth(80);
        bucketView.setFitHeight(80);
        bucketView.setOnMouseClicked(this::processBucketClick);
        bucketView.setCursor(Cursor.HAND);

        finderView.setPreserveRatio(true);
        finderView.setFitWidth(80);
        finderView.setFitHeight(80);
        finderView.setOnMouseClicked(this::processFinderClick);
        finderView.setCursor(Cursor.HAND);
        pane.setFinderDeactivated(() -> {
            finderView.setImage(magnifyOffImage);
        });

        colorTools = new HBox(bucketView, finderView);
        colorTools.setAlignment(Pos.CENTER);

        canvasName = new Label("Canvas Name:\n" + pane);
        canvasName.setTextFill(FONT_COLOR);

        saveOptions = new MenuButton("Save Options");
        saveOptions.setStyle("-fx-background-color: purple");

        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As");
        MenuItem exportItem = new MenuItem("Export");

        saveOptions.getItems().addAll(saveItem, saveAsItem, exportItem);
        saveOptions.setTextFill(FONT_COLOR);
        saveItem.setOnAction(this::saveNoExit);
        saveAsItem.setOnAction(this::saveSettings);
        exportItem.setOnAction(this::processExport);

        discardButton = new Button("Exit");
        discardButton.setStyle("-fx-background-color: orange");
        discardButton.setTextFill(FONT_COLOR);
        discardButton.setOnAction(this::processDiscard);

        saveMessage = new Label("Last saved on:\n" + pixelPane.getLastSavedTime());
        saveMessage.setTextFill(Color.LIMEGREEN);

        change = new Button("Change\nGridlines");
        change.setMinSize(40, 20);
        change.setStyle("-fx-background-color: blue");
        change.setTextFill(Color.WHITE);
        change.setOnAction(this::processChangeOnBackBoard);
        change.setCursor(Cursor.HAND);
        toggleGrid = new Button("Toggle Gridlines");
        toggleGrid.setMinSize(40, 20);
        toggleGrid.setStyle("-fx-background-color: blue");
        toggleGrid.setTextFill(Color.WHITE);
        toggleGrid.setOnAction(this::processGridToggle);
        toggleGrid.setCursor(Cursor.HAND);

        flip = new Button("Flip");
        flip.setStyle("-fx-background-color: magenta");
        flip.setTextFill(FONT_COLOR);
        flip.setOnAction(this::processFlip);

        StackPane container = new StackPane(pane);
        scrollPane = new ScrollPane(container);

        scrollPane.setPannable(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);

        scrollPane.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, e -> {
            if (e.getTarget() instanceof Canvas) {
                return;
            }
        });

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent");

        sideMenu = new VBox(picker, saveOptions, discardButton, colorsBox, change, toggleGrid, flip, colorTools, canvasName, saveMessage);
        picker.prefWidthProperty().bind(sideMenu.widthProperty().multiply(1));
        picker.prefHeightProperty().bind(sideMenu.heightProperty().multiply(0.08));
        discardButton.prefWidthProperty().bind(sideMenu.widthProperty().multiply(0.3));
        discardButton.prefHeightProperty().bind(sideMenu.heightProperty().multiply(0.05));
        flip.prefWidthProperty().bind(sideMenu.widthProperty().multiply(0.3));
        flip.prefHeightProperty().bind(sideMenu.heightProperty().multiply(0.05));
        colorTools.prefWidthProperty().bind(sideMenu.widthProperty().multiply(1));
        colorTools.prefHeightProperty().bind(sideMenu.heightProperty().multiply(0.09));
        VBox.setVgrow(sideMenu, Priority.ALWAYS);
        sideMenu.setSpacing(10);
        sideMenu.setFillWidth(true);
        sideMenu.setAlignment(Pos.TOP_CENTER);

        saveOptions.prefWidthProperty().bind(sideMenu.widthProperty().multiply(0.8));
        saveOptions.prefHeightProperty().bind(root.heightProperty().multiply(0.05));
        
        sideMenu.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        root.setCenter(scrollPane);
        root.setRight(sideMenu);
    }
}
