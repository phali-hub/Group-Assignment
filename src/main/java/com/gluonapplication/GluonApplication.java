package com.gluonapplication;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.net.URL;

public class GluonApplication extends Application {

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) {
        WelcomeView welcomeView = new WelcomeView(primaryStage);
        Scene welcomeScene = welcomeView.getScene();

        initializeBackgroundMusic();

        primaryStage.setTitle("Sesotho Learning App");
        primaryStage.setScene(welcomeScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializeBackgroundMusic() {
        try {
            URL musicFile = getClass().getResource("/backsong/kids-music-118820.mp3");

            if (musicFile != null) {
                Media media = new Media(musicFile.toExternalForm());
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

                mediaPlayer.setVolume(0.1);


                mediaPlayer.play();
            } else {
                System.err.println("Background music file not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}