package com.example.imnotarobot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ApplicationController {
    @FXML
    private ImageView imageViewer;

    private BufferedImage image;


    @FXML
    private void onOpenImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vyber obrázek");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrázky", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                image = ImageIO.read(file);
                Image fxImage = new Image(file.toURI().toString());
                imageViewer.setImage(fxImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
