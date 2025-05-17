package com.gluonapplication;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeView {

    private Stage stage;
    private ParallelTransition playButtonAnimation;
    private ParallelTransition quitButtonAnimation;

    public WelcomeView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Load background image
        Image bgImage = new Image(getClass().getResource("/b.jpg").toExternalForm());
        BackgroundImage bg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false));

        BorderPane root = new BorderPane();
        root.setBackground(new Background(bg));

        // Top-right settings icon
        Image settingsIcon = new Image(getClass().getResource("/settings.png").toExternalForm());
        ImageView settingsView = new ImageView(settingsIcon);
        settingsView.setFitWidth(24);
        settingsView.setFitHeight(24);

        // Volume and brightness sliders in dropdown
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        CustomMenuItem volumeControl = new CustomMenuItem(new VBox(new Label("Volume"), volumeSlider));
        volumeControl.setHideOnClick(false);

        Slider brightnessSlider = new Slider(0, 100, 50);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        CustomMenuItem brightnessControl = new CustomMenuItem(new VBox(new Label("Brightness"), brightnessSlider));
        brightnessControl.setHideOnClick(false);

        ContextMenu settingsMenu = new ContextMenu(volumeControl, brightnessControl);

        Button settingsBtn = new Button();
        settingsBtn.setId("settings-btn");
        settingsBtn.setGraphic(settingsView);
        settingsBtn.setOnAction(e -> settingsMenu.show(settingsBtn, javafx.geometry.Side.BOTTOM, 0, 0));

        BorderPane.setAlignment(settingsBtn, Pos.TOP_RIGHT);
        root.setTop(settingsBtn);
        BorderPane.setMargin(settingsBtn, new Insets(10, 10, 0, 0));

        // Center layout: Title and buttons
        VBox centerLayout = new VBox(20);
        centerLayout.setAlignment(Pos.CENTER);

        Label title = new Label("Sesotho Learning App");
        title.getStyleClass().add("welcome-title");

        Button playBtn = new Button("Bapala");
        playBtn.setPrefWidth(200);
        playBtn.getStyleClass().add("play-button");

        Button quitBtn = new Button("Tlohella");
        quitBtn.setPrefWidth(200);
        quitBtn.getStyleClass().add("quit-button");

        centerLayout.getChildren().addAll(title, playBtn, quitBtn);
        root.setCenter(centerLayout);

        // Add combined bounce and alternating zoom animations to buttons
        playButtonAnimation = createBounceZoomAnimation(playBtn);
        quitButtonAnimation = createBounceZoomAnimation(quitBtn);

        playBtn.setOnAction(e -> {
            // Stop animations when transitioning to new view
            SoundManager.playClickSound();
            playButtonAnimation.stop();
            quitButtonAnimation.stop();
            BasicView basicView = new BasicView(stage);
            Scene scene = new Scene(basicView, 1200, 800);
            stage.setScene(scene);
        });

        quitBtn.setOnAction(e -> {
            SoundManager.playClickSound();
            playButtonAnimation.stop();
            quitButtonAnimation.stop();
            stage.close();
        });

        // Final scene setup
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }

    private ParallelTransition createBounceZoomAnimation(Button button) {
        // Bounce animation (up and down)
        TranslateTransition bounce = new TranslateTransition(Duration.millis(1500), button);
        bounce.setFromY(0);
        bounce.setToY(-15);
        bounce.setAutoReverse(true);
        bounce.setInterpolator(Interpolator.EASE_BOTH);

        // Alternating zoom animation (scale in and out)
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(750), button);
        zoomIn.setFromX(1.0);
        zoomIn.setFromY(1.0);
        zoomIn.setToX(1.1);
        zoomIn.setToY(1.1);
        zoomIn.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(750), button);
        zoomOut.setFromX(1.1);
        zoomOut.setFromY(1.1);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);
        zoomOut.setInterpolator(Interpolator.EASE_IN);

        SequentialTransition zoomSequence = new SequentialTransition(zoomIn, zoomOut);

        // Combine bounce with alternating zoom
        ParallelTransition combinedAnimation = new ParallelTransition(bounce, zoomSequence);
        combinedAnimation.setCycleCount(Animation.INDEFINITE);
        combinedAnimation.play();

        return combinedAnimation;
    }
}