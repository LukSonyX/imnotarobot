package com.example.imnotarobot;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ApplicationController extends Application {
    double scale_factor = 1;
    boolean isDark;
    Image originalImage;
    BufferedImage modifiedImage;
    BufferedImage backupImage;
    Button revertButton;
    MenuItem saveImageItem;
    ScrollPane imageArea;
    StackPane imageAreaWrap;
    ToggleGroup shownImageGroup;
    RadioButton showOriginalImage;
    RadioButton showModifiedImage;
    ImageView imageViewer;
    Stage stage;
    int somerandom = 1;
    Menu filterMenu;
    Image darkModeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("darkmode.png")));
    Image lightModeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("lightmode.png")));
    ImageView guiStyleIcon;

    @Override
    public void start(Stage stage) {
        isDark = true;

        imageViewer = new ImageView();
        imageViewer.fitWidthProperty().bind(stage.widthProperty());
        imageViewer.fitHeightProperty().bind(stage.heightProperty());
        imageViewer.setPreserveRatio(true);
        imageViewer.setSmooth(true);


        guiStyleIcon = new ImageView(darkModeIcon);
        guiStyleIcon.setFitWidth(26);
        guiStyleIcon.setFitHeight(26);
        guiStyleIcon.setPreserveRatio(true);

        // Adding group make ScrollPane work with scale
        Group yetAnotherWrapper = new Group(imageViewer);

        Button guiStyle = new Button();
        guiStyle.setGraphic(guiStyleIcon);
        guiStyle.setOnAction(e -> toggleDarkMode());

        HBox menuWrap = new HBox();
        MenuBar menuBar = new MenuBar();

        // Menus
        Menu fileMenu = new Menu("File");
        filterMenu = new Menu("Filters");
        Menu aboutMenu = new Menu("About");
        Menu exitMenu = new Menu("Exit");

        // File menu items
        MenuItem loadImageItem = new MenuItem("Load Image");
        loadImageItem.setOnAction(e -> onOpenImage(imageViewer));

        saveImageItem = new MenuItem("Save Image");
        saveImageItem.setOnAction(e -> onSaveImage());
        saveImageItem.setDisable(true);

        fileMenu.getItems().addAll(loadImageItem, saveImageItem);

        MenuItem grayscaleFilter = new MenuItem("Grayscale Filter");
        grayscaleFilter.setOnAction(e -> {
            modifiedImage = Filters.grayscale(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem invertColors = new MenuItem("Invert colors");
        invertColors.setOnAction(e -> {
            modifiedImage = Filters.invert(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem contrastFilter = new MenuItem("Contrast");
        contrastFilter.setOnAction(e -> {
            modifiedImage = Filters.contrast(modifiedImage, 1.5f);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem boxBlurFilter = new MenuItem("Box Blur");
        boxBlurFilter.setOnAction(e -> {
            Double sliderValue = showSliderPopup("Brightness value", 0, 100);
            if (sliderValue == null) return;

            modifiedImage = Filters.boxBlur(modifiedImage, sliderValue.intValue());
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem thresholdFilter = new MenuItem("Threshold Filter");
        thresholdFilter.setOnAction(e -> {
            Double sliderValue = showSliderPopup("Threshold value", 0, 255);
            if (sliderValue == null) return;

            modifiedImage = Filters.threshold(modifiedImage, sliderValue.intValue());
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem edgerDetector = new MenuItem("Edge Detector");
        edgerDetector.setOnAction(e -> {
            modifiedImage = Filters.edgeDetect(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem sharpenFilter = new MenuItem("Sharpen");
        edgerDetector.setOnAction(e -> {
            modifiedImage = Filters.sharpen(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem pixelateFilter = new MenuItem("Pixelate");
        pixelateFilter.setOnAction(e -> {
            modifiedImage = Filters.pixelate(modifiedImage, 3);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem brightnessFilter = new MenuItem("Brightness Filter");
        brightnessFilter.setOnAction(e -> {
            Double sliderValue = showSliderPopup("Brightness value", -100, 100);
            if (sliderValue == null) return;

            modifiedImage = Filters.brightness(modifiedImage, sliderValue.intValue());
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        filterMenu.getItems().addAll(contrastFilter, grayscaleFilter, invertColors, brightnessFilter, thresholdFilter, boxBlurFilter, pixelateFilter, sharpenFilter, edgerDetector);
        filterMenu.setDisable(true);

        // About menu
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutWindow());
        aboutMenu.getItems().add(aboutItem);

        // Exit menu
        MenuItem exitItem = new MenuItem("Click again to proceed.");
        exitItem.setOnAction(e -> Platform.exit());
        exitMenu.getItems().add(exitItem);

        // Sidebar fill
        shownImageGroup = new ToggleGroup();

        Button generateVoronoiButton = new Button("Voronoi Image");
        generateVoronoiButton.setOnAction(e -> {
            try {
                modifiedImage = VoronoiGenerator.generateVoronoi(12345L + somerandom);
                originalImage = SwingFXUtils.toFXImage(modifiedImage, null);

                somerandom++;
                showOriginalImage();
                backupImage = modifiedImage;
                showOriginalImage.setDisable(false);
                showOriginalImage.setSelected(true);
                showModifiedImage.setDisable(false);
                saveImageItem.setDisable(false);
                filterMenu.setDisable(false);
                revertButton.setDisable(false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button generateDSquareButton = new Button("Diamond Square");
        generateDSquareButton.setOnAction(e -> {
            modifiedImage = DiamondSquareGenerator.generateDiamondSquare(somerandom);
            originalImage = SwingFXUtils.toFXImage(modifiedImage, null);
            somerandom++;
            showOriginalImage();
            backupImage = modifiedImage;
            showOriginalImage.setDisable(false);
            showOriginalImage.setSelected(true);
            showModifiedImage.setDisable(false);
            saveImageItem.setDisable(false);
            filterMenu.setDisable(false);
            revertButton.setDisable(false);
        });

        revertButton = new Button("Revert Image");
        revertButton.setOnAction(e -> {
           modifiedImage = backupImage;
           showModifiedImage();
        });
        revertButton.setDisable(true);

        // Modified / Original
        showOriginalImage = new RadioButton("Original Image");
        showOriginalImage.setToggleGroup(shownImageGroup);
        showOriginalImage.setDisable(true);
        showOriginalImage.setOnAction(e -> showOriginalImage());

        showModifiedImage = new RadioButton("Modified Image");
        showModifiedImage.setToggleGroup(shownImageGroup);
        showModifiedImage.setDisable(true);
        showModifiedImage.setOnAction(e -> showModifiedImage());

        // Wrapper for Image Viewer to keep image at the center
        imageAreaWrap = new StackPane(yetAnotherWrapper);
        imageAreaWrap.setAlignment(Pos.CENTER);

        // Screen Components Manifest
        BorderPane root = new BorderPane();
        imageArea = new ScrollPane(imageAreaWrap);
        VBox sideBar = new VBox();

        // Visual edit for imageArea
        imageArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageArea.setFitToWidth(true);
        imageArea.setFitToHeight(true);
        imageArea.setPannable(true);

        imageArea.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                e.consume();
                zoom(e.getDeltaY());
            }
        });

        // Visual edit for sideBar
        sideBar.setAlignment(Pos.CENTER);
        sideBar.setSpacing(5);

        // Fill the Screen!
        menuBar.getMenus().addAll(fileMenu, filterMenu, aboutMenu, exitMenu);
        menuWrap.getChildren().addAll(menuBar, guiStyle);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        sideBar.getChildren().addAll(showOriginalImage, showModifiedImage, revertButton, generateVoronoiButton, generateDSquareButton);

        // Set the hierarchy
        root.setTop(menuWrap);
        root.setCenter(imageArea);
        root.setRight(sideBar);

        Scene scene = new Scene(root, 800, 600);

        // Dark Mode
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setTitle("clanker painter 2.0");
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        stage.setScene(scene);
        stage.show();
    }
    // Does what it says...
    private void onOpenImage(ImageView imageViewer) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vyber obrázek");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrázky", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(null);
        //File file = new File("/home/zvonilka/Pictures/wallpapers/pixel.png");
        if (file != null) {
            try {
                modifiedImage = ImageIO.read(file);
                originalImage = new Image(file.toURI().toString());
                imageViewer.setImage(originalImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            backupImage = modifiedImage;
            showOriginalImage.setDisable(false);
            showOriginalImage.setSelected(true);
            showModifiedImage.setDisable(false);
            saveImageItem.setDisable(false);
            filterMenu.setDisable(false);
            revertButton.setDisable(false);
        }
    }

    private void onSaveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Uložit obrázek");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrázek", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        fileChooser.setInitialFileName("image.png");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            String fileName = file.getName().toLowerCase();
            String format = fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ? "jpg" : "png";
            try {
                ImageIO.write(modifiedImage, format, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleDarkMode() {
        if (isDark) {
            isDark = false;
            guiStyleIcon.setImage(lightModeIcon);
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
        else {
            isDark = true;
            guiStyleIcon.setImage(darkModeIcon);
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        }

    }

    private void showAboutWindow() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("About Us");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Label label = new Label("This is a program called Clanker Painter 2.0" +
                "\nYou can edit images, apply filters..." +
                "\nDevelopers: Tomáš Vala, Lukáš Bíllý, Maxmilián Kolář");
        label.setWrapText(true);
        dialog.getDialogPane().setContent(label);

        dialog.showAndWait();
    }

    // Toggle between modified/original image
    private void showModifiedImage() {
        Image image = SwingFXUtils.toFXImage(modifiedImage, null);
        imageViewer.setImage(image);
    }

    private void showOriginalImage() {
        imageViewer.setImage(originalImage);
    }

    private void zoom(double direction) {
        if (direction == 0) return;

        double scaleFactorChange = (direction > 0) ? 1.1 : 0.9;
        scale_factor *= scaleFactorChange;

        double minScale = 0.1;
        double maxScale = 5.0;

        if (scale_factor < minScale) {
            scale_factor = minScale;
        } else if (scale_factor > maxScale) {
            scale_factor = maxScale;
        }

        imageViewer.setScaleX(scale_factor);
        imageViewer.setScaleY(scale_factor);
    }

    public Double showSliderPopup(String title, double min, double max) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Adjust value");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        VBox container = new VBox(10, slider);
        container.setAlignment(Pos.CENTER);
        container.setPrefWidth(300);

        dialog.getDialogPane().setContent(container);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return slider.getValue();
            }
            else if (buttonType == ButtonType.CLOSE) {
                return null;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
