package com.example.imnotarobot;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;


public class Launcher extends Application {

    private boolean isDark = true;
    private ImageView imageViewer = new ImageView();
    private Image originalImage;
    private BufferedImage modifiedImage;
    private Stage stage;

    private MenuItem saveImageItem;
    private Menu filterMenu;
    private RadioButton showOriginalImage;
    private RadioButton showModifiedImage;
    private ImageView guiStyleIcon;

    private final Image darkIcon = new Image(getClass().getResourceAsStream("darkmode.png"));
    private final Image lightIcon = new Image(getClass().getResourceAsStream("lightmode.png"));

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        setupImageView();
        guiStyleIcon = makeIconButton();

        MenuBar menuBar = buildMenuBar();
        VBox sideBar = buildSidebar();

        ScrollPane imageScrollArea = buildImageScrollPane();

        BorderPane root = new BorderPane();
        HBox menuWrap = new HBox(menuBar, makeDarkModeToggleButton());
        HBox.setHgrow(menuBar, Priority.ALWAYS);

        root.setTop(menuWrap);
        root.setCenter(imageScrollArea);
        root.setRight(sideBar);

        Scene scene = new Scene(root, 800, 600);
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setTitle("Clanker Painter 2.0");
        stage.setScene(scene);
        stage.show();
    }

    private void setupImageView() {
        imageViewer.setPreserveRatio(true);
        imageViewer.setSmooth(true);
    }

    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        file.getItems().addAll(makeLoadItem(), makeSaveItem());

        filterMenu = makeFilterMenu();
        filterMenu.setDisable(true);

        Menu about = new Menu("About", null, makeAboutItem());
        Menu exit = new Menu("Exit", null, makeExitItem());

        menuBar.getMenus().addAll(file, filterMenu, about, exit);
        return menuBar;
    }

    private VBox buildSidebar() {
        ToggleGroup viewGroup = new ToggleGroup();

        showOriginalImage = new RadioButton("Original Image");
        showOriginalImage.setToggleGroup(viewGroup);
        showOriginalImage.setDisable(true);
        showOriginalImage.setOnAction(e -> imageViewer.setImage(originalImage));

        showModifiedImage = new RadioButton("Modified Image");
        showModifiedImage.setToggleGroup(viewGroup);
        showModifiedImage.setDisable(true);
        showModifiedImage.setOnAction(e -> showModified());

        VBox box = new VBox(10, showOriginalImage, showModifiedImage);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        return box;
    }

    private ScrollPane buildImageScrollPane() {
        StackPane imageWrapper = new StackPane(imageViewer);
        imageWrapper.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(imageWrapper);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                e.consume();
                zoom(scrollPane, imageWrapper, e.getX(), e.getY(), e.getDeltaY());
            }
        });

        return scrollPane;
    }


    private ImageView makeIconButton() {
        ImageView iv = new ImageView(darkIcon);
        iv.setFitWidth(26);
        iv.setFitHeight(26);
        return iv;
    }

    private Button makeDarkModeToggleButton() {
        Button btn = new Button();
        btn.setGraphic(guiStyleIcon);
        btn.setOnAction(e -> toggleDarkMode());
        return btn;
    }

    private MenuItem makeLoadItem() {
        MenuItem item = new MenuItem("Load Image");
        item.setOnAction(e -> loadImage());
        return item;
    }

    private MenuItem makeSaveItem() {
        saveImageItem = new MenuItem("Save Image");
        saveImageItem.setDisable(true);
        saveImageItem.setOnAction(e -> saveImage());
        return saveImageItem;
    }

    private MenuItem makeAboutItem() {
        MenuItem item = new MenuItem("About");
        item.setOnAction(e -> showAboutWindow());
        return item;
    }

    private MenuItem makeExitItem() {
        MenuItem item = new MenuItem("Exit");
        item.setOnAction(e -> Platform.exit());
        return item;
    }

    private Menu makeFilterMenu() {
        Menu filters = new Menu("Filters");

        filters.getItems().addAll(
                makeFilter("Grayscale", () -> apply(Filters::grayscale)),
                makeFilter("Invert", () -> apply(Filters::invert)),
                makeFilter("Brightness", () -> apply(img -> Filters.brightness(img, 150))),
                makeFilter("Contrast", () -> apply(img -> Filters.contrast(img, 1.2f))),
                makeFilter("Threshold", () -> apply(img -> Filters.threshold(img, 12))),
                makeFilter("Blur", () -> apply(img -> Filters.boxBlur(img, 12))),
                makeFilter("Sharpen", () -> apply(Filters::sharpen)),
                makeFilter("Edge Detect", () -> apply(Filters::edgeDetect)),
                makeFilter("Pixelate", () -> apply(img -> Filters.pixelate(img, 20)))
        );
        return filters;
    }

    private MenuItem makeFilter(String name, Runnable action) {
        MenuItem item = new MenuItem(name);
        item.setOnAction(e -> action.run());
        return item;
    }

    private void loadImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        try {
            originalImage = new Image(file.toURI().toString());
            modifiedImage = ImageIO.read(file);

            imageViewer.setImage(originalImage);

            showOriginalImage.setDisable(false);
            showModifiedImage.setDisable(false);
            showOriginalImage.setSelected(true);

            saveImageItem.setDisable(false);
            filterMenu.setDisable(false);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Image");
        fc.setInitialFileName("image.png");

        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        try {
            String format = file.getName().toLowerCase().endsWith(".jpg") ? "jpg" : "png";
            ImageIO.write(modifiedImage, format, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void toggleDarkMode() {
        isDark = !isDark;
        guiStyleIcon.setImage(isDark ? darkIcon : lightIcon);
        Application.setUserAgentStylesheet(
                isDark ? new PrimerDark().getUserAgentStylesheet()
                        : new PrimerLight().getUserAgentStylesheet()
        );
    }

    private void apply(java.util.function.Function<BufferedImage, BufferedImage> fn) {
        modifiedImage = fn.apply(modifiedImage);
        showModifiedImage.setSelected(true);
        showModified();
    }

    private void showModified() {
        imageViewer.setImage(SwingFXUtils.toFXImage(modifiedImage, null));
    }

    private void showAboutWindow() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Clanker Painter 2.0");
        /*alert.setContentText("""
                You can edit images, apply filters...
                Developers:
                - Tomáš Vala
                - Lukáš Bíllý
                - Maxmilián Kolář
                """);*/
        alert.showAndWait();
    }


    private void zoom(ScrollPane scrollPane, StackPane wrapper, double mouseX, double mouseY, double deltaY) {
        double scaleFactor = (deltaY > 0) ? 1.1 : 0.9;

        double oldScale = wrapper.getScaleX();
        double newScale = oldScale * scaleFactor;

        newScale = Math.max(0.1, Math.min(newScale, 10));

        double mouseRelX = mouseX / scrollPane.getViewportBounds().getWidth();
        double mouseRelY = mouseY / scrollPane.getViewportBounds().getHeight();

        double contentWidth = wrapper.getBoundsInParent().getWidth();
        double contentHeight = wrapper.getBoundsInParent().getHeight();

        wrapper.setScaleX(newScale);
        wrapper.setScaleY(newScale);

        double newContentWidth = wrapper.getBoundsInParent().getWidth();
        double newContentHeight = wrapper.getBoundsInParent().getHeight();

        scrollPane.setHvalue((scrollPane.getHvalue() * (contentWidth - scrollPane.getViewportBounds().getWidth()) + mouseRelX * (newContentWidth - contentWidth))
                / (newContentWidth - scrollPane.getViewportBounds().getWidth()));
        scrollPane.setVvalue((scrollPane.getVvalue() * (contentHeight - scrollPane.getViewportBounds().getHeight()) + mouseRelY * (newContentHeight - contentHeight))
                / (newContentHeight - scrollPane.getViewportBounds().getHeight()));
    }

}
