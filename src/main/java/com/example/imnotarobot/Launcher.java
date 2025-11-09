package com.example.imnotarobot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Launcher extends Application {
    Image originalImage;
    BufferedImage modifiedImage;

    MenuItem saveImageItem;


    ToggleGroup shownImageGroup;
    RadioButton showOriginalImage;
    RadioButton showModifiedImage;
    ImageView imageViewer;
    Stage stage;
    Menu filterMenu;

    @Override
    public void start(Stage stage) {
        imageViewer = new ImageView();
        imageViewer.setPreserveRatio(true);

        // Here be menu bar
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

        // Filter menu WIP
        MenuItem grayscaleFilter = new MenuItem("Grayscale Filter");
        grayscaleFilter.setOnAction(e -> grayscaleFilter(modifiedImage));

        filterMenu.getItems().addAll(grayscaleFilter);
        filterMenu.setDisable(true);

        // About menu
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutWindow());
        aboutMenu.getItems().add(aboutItem);

        // Sidebar fill
        shownImageGroup = new ToggleGroup();

        showOriginalImage = new RadioButton("Original Image");
        showOriginalImage.setToggleGroup(shownImageGroup);
        showOriginalImage.setDisable(true);
        showOriginalImage.setOnAction(e -> showOriginalImage());

        showModifiedImage = new RadioButton("Modified Image");
        showModifiedImage.setToggleGroup(shownImageGroup);
        showModifiedImage.setDisable(true);
        showModifiedImage.setOnAction(e -> showModifiedImage());

        // Exit menu
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        exitMenu.getItems().add(exitItem);

        BorderPane root = new BorderPane();
        // Wrapper helps center the picture, otherwise it is always in the top left corner
        StackPane imageAreaWrapper = new StackPane(imageViewer);
        ScrollPane imageArea = new ScrollPane(imageAreaWrapper);
        VBox sideBar = new VBox();

        // Visual edit for sidebar
        sideBar.setAlignment(Pos.CENTER);
        sideBar.setPadding(new Insets(10));

        // Visual edit for image area
        imageArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageArea.setFitToWidth(true);
        imageArea.setFitToHeight(true);
        imageArea.setPannable(true);

        // Visual edit for root
        root.setMinSize(800, 600);

        // Fill the Screen!
        menuBar.getMenus().addAll(fileMenu, filterMenu, aboutMenu, exitMenu);
        // sideBar.getChildren().addAll(selectImageButton, editMatrixButton);
        sideBar.getChildren().addAll(showOriginalImage, showModifiedImage);

        // Set the hierarchy
        root.setTop(menuBar);
        root.setCenter(imageArea);
        root.setRight(sideBar);

        // Some scene BS
        Scene scene = new Scene(root, 800, 600);

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

        File file = fileChooser.showOpenDialog(null);
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

    // Here be filters
    private void grayscaleFilter(BufferedImage image) {
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int grayValue = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                Color grayColor = new Color(grayValue, grayValue, grayValue);

                grayscaleImage.setRGB(x, y, grayColor.getRGB());
            }
        }
        modifiedImage = grayscaleImage;
        showModifiedImage.setSelected(true);
        showModifiedImage();
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

    public static void main(String[] args) {
        launch(args);
    }
}
