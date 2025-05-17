package com.gluonapplication;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BasicView extends View {

    private Stage stage;

    public BasicView(Stage stage) {
        this.stage = stage;

        this.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        this.getStyleClass().add("basic-view");

        // Back button using Unicode arrow
        Label backLabel = new Label("←");
        backLabel.setId("back-icon");
        backLabel.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            goBack();
        });

        HBox topBar = new HBox(backLabel);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10, 0, 0, 10));
        topBar.getStyleClass().add("top-bar");

        Label welcomeLabel = new Label("Khetha Sehlopha");
        welcomeLabel.getStyleClass().add("welcome-label");

        // Category buttons with thumbnails

        HBox lipapaliBox = createCategoryButton("Lipapali", "/thumbnails/lipapali.png");
        HBox maeleBox = createCategoryButton("Maele", "/thumbnails/maele.png");
        HBox litsomBox = createCategoryButton("Lits'omo", "/thumbnails/litsomo.png");
        HBox moetloBox = createCategoryButton("Moetlo", "/thumbnails/moetlo.png");
        HBox lilothoBox = createCategoryButton("Lilotho", "/thumbnails/lilotho.png");

        VBox layout = new VBox(15, welcomeLabel, lilothoBox, lipapaliBox, maeleBox, litsomBox, moetloBox);
        layout.setAlignment(Pos.CENTER);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setCenter(layout);
        mainLayout.setPadding(new Insets(20));
        setCenter(mainLayout);
    }

    /*Creates a category button with a thumbnail.
     */
    private HBox createCategoryButton(String category, String thumbnailPath) {
        ImageView thumbnail = new ImageView(new Image(getClass().getResource(thumbnailPath).toExternalForm()));
        thumbnail.setFitWidth(50);
        thumbnail.setFitHeight(50);
        thumbnail.setPreserveRatio(true);

        Button button = new Button(category);
        button.setPrefWidth(200);
        button.getStyleClass().add("category-button");
        button.setOnAction(e -> {
            SoundManager.playClickSound();
            openGameplay(category);
        });

        HBox hbox = new HBox(10, thumbnail, button);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(5));
        hbox.getStyleClass().add("category-box");

        return hbox;
    }

    private void openGameplay(String category) {
        Gameplay gameplayView = new Gameplay(category, stage);
        Scene gameplayScene = new Scene(gameplayView, 1200, 800);
        gameplayScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(gameplayScene);
    }

    private void goBack() {
        WelcomeView welcomeView = new WelcomeView(stage);
        Scene welcomeScene = welcomeView.getScene();
        welcomeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(welcomeScene);
    }
}
