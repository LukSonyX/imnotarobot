package com.example.imnotarobot;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
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

public class Launcher extends Application {
    boolean isDark;
    Image originalImage;
    BufferedImage modifiedImage;
    MenuItem saveImageItem;
    ScrollPane imageArea;
    StackPane imageAreaWrap;
    ToggleGroup shownImageGroup;
    RadioButton showOriginalImage;
    RadioButton showModifiedImage;
    ImageView imageViewer;
    Stage stage;
    Menu filterMenu;
    Image darkModeIcon = new Image(getClass().getResourceAsStream("darkmode.png"));
    Image lightModeIcon = new Image(getClass().getResourceAsStream("lightmode.png"));
    ImageView guiStyleIcon;


    @Override
    public void start(Stage stage) {
        isDark = true;

        imageViewer = new ImageView();
        imageViewer.setPreserveRatio(true);
        imageViewer.setSmooth(true);


        guiStyleIcon = new ImageView(darkModeIcon);
        guiStyleIcon.setFitWidth(26);
        guiStyleIcon.setFitHeight(26);
        guiStyleIcon.setPreserveRatio(true);

        // Adding group make ScrollPane work with scale
        Group yetAnotherWrapper = new Group(imageViewer);

        MenuBar menuBar = new MenuBar();

        Button guiStyle = new Button();
        guiStyle.setGraphic(guiStyleIcon);
        guiStyle.setOnAction(e -> {
            toggleDarkMode();
        });

        HBox menuWrap = new HBox();

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

        // Filter menu WIP
        Filters filters = new Filters();

        MenuItem grayscaleFilter = new MenuItem("Grayscale Filter");
        grayscaleFilter.setOnAction(e -> {
            modifiedImage = filters.grayscaleFilter(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem invertColors = new MenuItem("Invert colors");
        invertColors.setOnAction(e -> {
            modifiedImage = filters.invertColors(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        MenuItem pixelSort = new MenuItem("sort pixels");
        pixelSort.setOnAction(e -> {
            modifiedImage = filters.pixelSort(modifiedImage);
            showModifiedImage.setSelected(true);
            showModifiedImage();
        });

        filterMenu.getItems().addAll(grayscaleFilter, invertColors, pixelSort);
        filterMenu.setDisable(true);

        // About menu
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> {
            showAboutWindow();
        });
        aboutMenu.getItems().add(aboutItem);

        // Exit menu
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        exitMenu.getItems().add(exitItem);

        // Sidebar fill
        shownImageGroup = new ToggleGroup();

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

        // Visual edit for root
        root.setMinSize(800, 600);

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
        sideBar.setPadding(new Insets(10));

        // Fill the Screen!
        menuBar.getMenus().addAll(fileMenu, filterMenu, aboutMenu, exitMenu);
        menuWrap.getChildren().addAll(menuBar, guiStyle);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        sideBar.getChildren().addAll(showOriginalImage, showModifiedImage);

        // Set the hierarchy
        root.setTop(menuWrap);
        root.setCenter(imageArea);
        root.setRight(sideBar);

        // Some scene BS
        Scene scene = new Scene(root, 800, 600);

        // Dark Mode... Light Mode may be implemented in future
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setTitle("clanker painter 2.0");
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

        //File file = fileChooser.showOpenDialog(null);
        File file = new File("/home/LukSonyX/Desktop/mnam.png");
        if (file != null) {
            try {
                showOriginalImage.setDisable(false);
                showOriginalImage.setSelected(true);
                showModifiedImage.setDisable(false);
                saveImageItem.setDisable(false);
                filterMenu.setDisable(false);

                modifiedImage = ImageIO.read(file);
                originalImage = new Image(file.toURI().toString());
                imageViewer.setImage(originalImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private Color pick_color() {

        return Color.blue;
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
        double scale_factor = (direction > 0) ? 1.1 : 0.9;

        imageViewer.setScaleX(imageViewer.getScaleX()*scale_factor);
        imageViewer.setScaleY(imageViewer.getScaleY()*scale_factor);
        imageViewer.setScaleZ(imageViewer.getScaleZ()*scale_factor);
    }
/*
    publ ic static void main(String[] args) {
        launch(args);
    }
*/
}
