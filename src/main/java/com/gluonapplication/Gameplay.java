package com.gluonapplication;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.control.Slider;

public class Gameplay extends VBox {

    private String category;
    private Stage stage;
    private int currentQuestionIndex;
    private Label feedbackLabel;
    private Label scoreLabel;
    private List<Question> easyQuestions;
    private List<Question> mediumQuestions;
    private List<Question> hardQuestions;
    private List<Question> currentQuestions;
    private Score scoreTracker;
    private String currentDifficulty;
    private AudioClip clickSound;

    public Gameplay(String category, Stage stage) {
        this.category = category;
        this.stage = stage;
        this.currentQuestionIndex = 0;
        this.feedbackLabel = new Label();
        this.scoreLabel = new Label("Score: 0");
        this.easyQuestions = new ArrayList<>();
        this.mediumQuestions = new ArrayList<>();
        this.hardQuestions = new ArrayList<>();
        this.currentQuestions = new ArrayList<>();
        this.scoreTracker = new Score();
        this.getStylesheets().add(getClass().getResource("/gameplay.css").toExternalForm());
        this.getStyleClass().add("game-container");

        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-padding: 20px;");

        feedbackLabel.setStyle("-fx-font-size: 18px;");
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: blue;");

        initializeQuestions();
        showLevelSelection();
    }

    private void showLevelSelection() {
        VBox levelSelectionBox = new VBox(20);
        levelSelectionBox.setAlignment(Pos.CENTER);
        levelSelectionBox.getStyleClass().add("level-selection-box");

        // Create back button using Label
        Label backLabel = new Label("←");
        backLabel.setId("back-icon");
        backLabel.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            returnToCategories();
        });

        Button easyButton = createLevelButton("Easy");
        Button mediumButton = createLevelButton("Medium");
        Button hardButton = createLevelButton("Hard");

        levelSelectionBox.getChildren().addAll(backLabel, easyButton, mediumButton, hardButton);
        getChildren().setAll(levelSelectionBox);
    }

    private void returnToCategories() {
        Scene scene = new Scene(new BasicView(stage), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/gameplay.css").toExternalForm());
        stage.setScene(scene);
    }

    private Button createLevelButton(String difficulty) {
        Button button = new Button(difficulty);

        // Base style
        String baseStyle =
                "-fx-min-width: 180px; " +
                        "-fx-min-height: 50px; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Comic Sans MS', cursive, sans-serif; " +  // Kid-friendly font
                        "-fx-background-radius: 15px; " +  // Extra rounded corners
                        "-fx-border-radius: 15px; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-color: #FFFFFF; " +  // White border for pop-out effect
                        "-fx-padding: 8px 15px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);";  // Bigger shadow

        String difficultyStyle = "";
        switch (difficulty) {
            case "Easy":
                difficultyStyle =
                        "-fx-background-color: #FF9E3F; " +  // Bright orange
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #FF6B00;";  // Darker orange border
                break;
            case "Medium":
                difficultyStyle =
                        "-fx-background-color: #6ECFF6; " +  // Bright sky blue
                                "-fx-text-fill: #003366; " +  // Dark blue text
                                "-fx-border-color: #00A2E8;";  // Deeper blue border
                break;
            case "Hard":
                difficultyStyle =
                        "-fx-background-color: #FF5B8F; " +  // Pink
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #D6004B;";  // Dark pink border
                break;
        }

        button.setStyle(baseStyle + difficultyStyle);

        boolean forceUnlock = (category.equals("Lits'omo") || category.equals("Moetlo"))
                && difficulty.equals("Easy");

        if (forceUnlock || scoreTracker.isLevelUnlocked(category, difficulty)) {
            button.setOnAction(e -> {
                SoundManager.playClickSound();
                startGame(difficulty);
            });

            // Fun hover effects
            String finalDifficultyStyle = difficultyStyle;
            button.setOnMouseEntered(e -> button.setStyle(
                    baseStyle + finalDifficultyStyle +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5); " +
                            "-fx-scale-x: 1.05; " +  // Slight zoom
                            "-fx-scale-y: 1.05;"
            ));
            button.setOnMouseExited(e -> button.setStyle(baseStyle + finalDifficultyStyle));
        } else {
            button.setText(difficulty + " (🔒 Locked)");
            button.setDisable(true);
            button.setStyle(baseStyle +
                    "-fx-background-color: #CCCCCC; " +  // Light gray
                    "-fx-text-fill: #777777; " +
                    "-fx-border-color: #999999; " +
                    "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1); " +  // Inner shadow for "pressed" look
                    "-fx-opacity: 0.9;");
        }

        return button;
    }

    private void startGame(String difficulty) {
        this.currentDifficulty = difficulty;
        this.currentQuestionIndex = 0;
        scoreTracker.resetScore();
        scoreTracker.setCurrentLevel(category, difficulty);

        // Select appropriate questions
        switch (difficulty) {
            case "Easy":
                currentQuestions = selectRandomQuestions(easyQuestions, 5);
                break;
            case "Medium":
                currentQuestions = selectRandomQuestions(mediumQuestions, 5);
                break;
            case "Hard":
                currentQuestions = selectRandomQuestions(hardQuestions, 5);
                break;
        }

        showNextQuestion();
    }

    private List<Question> selectRandomQuestions(List<Question> questions, int count) {
        if (questions.size() <= count) {
            Collections.shuffle(questions);
            return new ArrayList<>(questions.subList(0, Math.min(questions.size(), count)));
        }

        List<Question> selected = new ArrayList<>();
        List<Question> copy = new ArrayList<>(questions);
        Collections.shuffle(copy);

        for (int i = 0; i < count && i < copy.size(); i++) {
            selected.add(copy.get(i));
        }

        return selected;
    }

    private MediaPlayer currentMediaPlayer; // Track current media player

    private void showNextQuestion() {
        // Clean up previous media resources
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.dispose();
            currentMediaPlayer = null;
        }

        getChildren().clear();

        // Header with back button and score
        HBox header = createHeader();

        if (currentQuestionIndex < currentQuestions.size()) {
            Question currentQuestion = currentQuestions.get(currentQuestionIndex);

            Label questionLabel = createQuestionLabel(currentQuestion);


            VBox mediaBox = createMediaContent(currentQuestion);


            Node optionsContainer = createOptionsContainer(currentQuestion);

            getChildren().addAll(header, questionLabel, mediaBox, optionsContainer, feedbackLabel);
        } else {
            showResults();
        }
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("game-header");

        Label backLabel = new Label("←");
        backLabel.setId("back-icon");
        backLabel.setOnMouseClicked(e -> returnToCategories());{
            SoundManager.playClickSound();
        };

        header.getChildren().addAll(backLabel, scoreLabel);
        return header;
    }

    private Label createQuestionLabel(Question question) {
        Label questionLabel = new Label(question.getQuestionText());
        questionLabel.getStyleClass().add("question-text");
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(600);
        return questionLabel;
    }

    private VBox createMediaContent(Question question) {
        VBox mediaBox = new VBox(10);
        mediaBox.setAlignment(Pos.CENTER);

        if (question.isVideoQuestion()) {
            setupVideoPlayer(question, mediaBox);
        } else if (question.isImageQuestion()) {
            setupQuestionImage(question, mediaBox);
        }

        return mediaBox;
    }

    private void setupVideoPlayer(Question question, VBox mediaBox) {
        try {
            Media media = new Media(getClass().getResource(question.getVideoPath()).toExternalForm());
            currentMediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(currentMediaPlayer);

            mediaView.setFitWidth(600);
            mediaView.setPreserveRatio(true);

            HBox controls = createVideoControls();
            mediaBox.getChildren().addAll(mediaView, controls);

            // Auto-play for first question
            if (currentQuestionIndex == 0) {
                currentMediaPlayer.play();
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Could not load video");
            errorLabel.setStyle("-fx-text-fill: red;");
            mediaBox.getChildren().add(errorLabel);
        }
    }

    private HBox createVideoControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button playButton = new Button("▶");
        playButton.setOnAction(e -> currentMediaPlayer.play());

        Button pauseButton = new Button("⏸");
        pauseButton.setOnAction(e -> currentMediaPlayer.pause());

        Slider volumeSlider = new Slider(0, 1, 0.7);
        volumeSlider.valueProperty().bindBidirectional(currentMediaPlayer.volumeProperty());

        controls.getChildren().addAll(playButton, pauseButton, new Label("Volume:"), volumeSlider);
        return controls;
    }

    private void setupQuestionImage(Question question, VBox mediaBox) {
        ImageView imageView = new ImageView(question.getImage());
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("question-image");
        mediaBox.getChildren().add(imageView);
    }

    private Node createOptionsContainer(Question question) {
        if (question.isImageQuestion() && !question.getOptionImages().isEmpty()) {
            return createImageOptions(question);
        } else {
            return createTextOptions(question);
        }
    }

    private HBox createImageOptions(Question question) {
        HBox optionsBox = new HBox(15);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.getStyleClass().add("image-options-container");

        List<Image> allOptions = new ArrayList<>(question.getOptionImages());
        if (question.getImage() != null) {
            allOptions.add(question.getImage());
        }
        Collections.shuffle(allOptions);

        for (Image optionImage : allOptions) {
            ImageView imageOption = new ImageView(optionImage);
            imageOption.setFitWidth(150);
            imageOption.setPreserveRatio(true);
            imageOption.getStyleClass().add("option-image");

            // Highlight correct answer when clicked
            imageOption.setOnMouseClicked(e -> {
                boolean isCorrect = optionImage.equals(question.getImage());
                handleAnswer(isCorrect);

                // Visual feedback
                if (isCorrect) {
                    imageOption.setStyle("-fx-effect: dropshadow(gaussian, green, 20, 0.7, 0, 0);");
                } else {
                    imageOption.setStyle("-fx-effect: dropshadow(gaussian, red, 20, 0.7, 0, 0);");
                }
            });

            optionsBox.getChildren().add(imageOption);
        }

        return optionsBox;
    }

    private HBox createTextOptions(Question question) {
        HBox optionsBox = new HBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.getStyleClass().add("text-options-container");

        List<String> options = new ArrayList<>(question.getWrongAnswers());
        options.add(question.getCorrectAnswer());
        Collections.shuffle(options);

        for (String option : options) {
            Button optionButton = new Button(option);
            optionButton.getStyleClass().add("option-button");
            optionButton.setWrapText(true);
            optionButton.setMaxWidth(300);
            optionButton.setOnAction(e -> handleAnswer(option.equals(question.getCorrectAnswer())));
            optionsBox.getChildren().add(optionButton);
        }

        return optionsBox;
    }


    private void handleAnswer(boolean isCorrect) {
        scoreTracker.incrementTotalQuestions();
        if (isCorrect) {
            scoreTracker.incrementCorrectAnswers();
            feedbackLabel.setText("U E TSOEPOTSE!");
            feedbackLabel.setStyle("-fx-text-fill: green,-fx-text-size:14;");
        } else {
            feedbackLabel.setText("U FOSITSE!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }

        scoreLabel.setText("Score: " + scoreTracker.getCorrectAnswers() + "/" + scoreTracker.getTotalQuestions());
        scoreLabel.setStyle("-fx-text-fill: white;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < currentQuestions.size()) {
                showNextQuestion();
            } else {
                scoreTracker.updateLevelProgress();
                showResults();
            }
        });
        pause.play();
    }

    private void showResults() {
        boolean passed = scoreTracker.getCorrectAnswers() >= 3;

        Label resultLabel = new Label();
        resultLabel.getStyleClass().add("result-label");

        if (passed) {
            resultLabel.setText("Rea o lebohisa! 🎉\nLimaraka: " + scoreTracker.getCorrectAnswers() + "/5");
            resultLabel.setStyle("-fx-text-fill: green;");
        } else {
            resultLabel.setText("leka hape!\nlimaraka: " + scoreTracker.getCorrectAnswers() + "/5 (Need 3 to pass)");
            resultLabel.setStyle("-fx-text-fill: red;");
        }

        Button retryButton = new Button("leka hape");
        retryButton.setOnAction(e -> {
            SoundManager.playClickSound();
            startGame(currentDifficulty);
        });
        retryButton.getStyleClass().add("retry");

        Button levelSelectButton = new Button("khetha mokhahlelo");
        levelSelectButton.setOnAction(e -> {
            SoundManager.playClickSound();
            showLevelSelection();
        });
        levelSelectButton.getStyleClass().add("level-select");

        VBox resultBox = new VBox(20, resultLabel, retryButton, levelSelectButton);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.getStylesheets().add(getClass().getResource("/gameplay.css").toExternalForm());

        getChildren().clear();
        getChildren().add(resultBox);
    }

    private void initializeQuestions() {
        if (category.equals("Lilotho")) {

            easyQuestions.add(new Question("Ka qhala phoofo ka ja mokotla", "moholu", createList(new String[]{"likhobe", "mokopu", "torofeile"}), null));
            easyQuestions.add(new Question("'Me nts'oare ke nye", "nko", createList(new String[]{"moriri", "letsoho", "serethe"}), null));
            easyQuestions.add(new Question("Monna eo ereng ha khots'e a roalle", "noka", createList(new String[]{"mollo", "moea", "pula"}), null));
            easyQuestions.add(new Question("Khare ea leifo", "ntja", createList(new String[]{"khomo", "pere", "katse"}), null));
            easyQuestions.add(new Question("O monate feela wa hlaba", "torofeile", createList(new String[]{"ts'ehlo", "perekisi", "lekhoaba"}), null));

            mediumQuestions.add(new Question("Maqheku a bina helehelele ka lehaheng", "likhobe", createList(new String[]{"mobu", "mollo", "lerole"}), null));
            mediumQuestions.add(new Question("Lithung-thung tsa tlapa le leholo", "linaleli", createList(new String[]{"metsi", "lipalesa", "selepe"}), null));
            mediumQuestions.add(new Question("Botala keba joang, bofubelu keba mali, monate ke wa tsoekere", "Lehapu", createList(new String[]{"apole", "erekisi", "joang"}), null));
            mediumQuestions.add(new Question("Nthethe o bina a lutse", "Sefate", createList(new String[]{"thaba", "lefika", "letamo"}), null));
            mediumQuestions.add(new Question("Majoana mabeli mabetsa hole", "mahlo", createList(new String[]{"metsi", "matsoho", "majoe"}), null));

            hardQuestions.add(new Question("Monna ea sekhoalita hloohong", "lesokoana", createList(new String[]{"katiba", "papa", "thaba"}), null));
            hardQuestions.add(new Question("Lipoli tsa makeleketla li fula li bothile", "sekele", createList(new String[]{"haraka", "kharafu", "sakha"}), null));
            hardQuestions.add(new Question("Lehalima lereleli le pota motse", "namane", createList(new String[]{"tsuonyana", "katse", "lekau"}), null));
            hardQuestions.add(new Question("Pate lia lekana", "leholimo le lefats'e", createList(new String[]{"leoatle", "moea", "maru"}), null));
            hardQuestions.add(new Question("Thankha-thankha ke tla tsoalla kae?", "mokopu hao nama", createList(new String[]{"moholi", "metsi", "pula"}), null));

        }

        if (category.equals("Lipapali")) {
            Image img1 = new Image("/1.jpg");
            Image img2 = new Image("/2.jpg");
            Image img3 = new Image("/3.jpg");
            Image img4 = new Image("/th.jpg");
            Image img5 = new Image("/5.jpg");
            Image img6 = new Image("/lesokoana.JPG");
            Image img7 = new Image("/libini.jpg");

            easyQuestions.add(new Question(
                    "Papali ee e bitsoang?",
                    "mohobelo",
                    createList(new String[]{"mohobelo", "mokallo", "mohlomi"}),
                    img1
            ));

            easyQuestions.add(new Question(
                    "Ke papali efe e etsoang ke banana ka ho bina ba ntse ba inama?",
                    "mokhibo",
                    createList(new String[]{"mokallo", "sekhoro", "mohobelo"}),
                    img2
            ));

            easyQuestions.add(new Question(
                    "Papali ena e kenyelletsa ho bina le ho opa ka maoto fatše?",
                    "litolobonyana",
                    createList(new String[]{"mokhibo", "mohlomi", "lebelo"}),
                    img3
            ));

            easyQuestions.add(new Question(
                    "Papali ea setso moo bana ba binang ba potoloha?",
                    "ho bopa",
                    createList(new String[]{"mokhibo", "mokallo", "sekhoro"}),
                    img4
            ));

            easyQuestions.add(new Question(
                    "Papali e tsejoang ka ho qoma ka maoto haufi le molamu?",
                    "liketo",
                    createList(new String[]{"mokhibo", "mokallo", "mohlomi"}),
                    img5
            ));
            // Medium Questions (Lipapali)
            mediumQuestions.add(new Question(
                    "Ke papali efe ee sebelisang ntho ee?",
                    "lesokoana",
                    createList(new String[]{"mokhibo", "mohlomi", "sekhoro"}),
                    img6
            ));

            mediumQuestions.add(new Question(
                    "Papali moo ho sebelisoang molamu ho tšoaea libapali tse ling?",
                    "ts'oasa ka molamu",
                    createList(new String[]{"mokhibo", "ts'oasa ka molamu", "mokallo"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Papali eo ho eona bana ba itšoarang ka matsoho ba bina?",
                    "selikalikoe sa ho bina",
                    createList(new String[]{"mokhibo", "ntlamo", "lebelo la molamu"}),
                    img7
            ));

            mediumQuestions.add(new Question(
                    "Papali eo bana ba qetellang ka ho ema ba le mong le ho bina?",
                    "ho ema mong",
                    createList(new String[]{"mokallo", "ho khasa", "tsikitlana"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Papali e etsoang ka ho qhomela holim’a mela kapa matlapa?",
                    "qhomela mela",
                    createList(new String[]{"mokhibo", "qhomela mela", "ho opa ka maoto"}),
                    null
            ));
            // Hard Questions (Lipapali)
            hardQuestions.add(new Question(
                    "Ke papali efe moo ho sebelisoang lithupa ho bapala ka bokhabane?",
                    "molamu oa setso",
                    createList(new String[]{"mokhibo", "lerumo", "mokallo"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Papali e kenyelletsang lithothokiso le mantsoe a ngotsoeng ka hloko?",
                    "lipapali tsa puo",
                    createList(new String[]{"lipale", "mokhibo", "tsikitlana"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Ke papali efe ea setso e batlang mamello, bohlale le ts'ebelisano?",
                    "qalo tsa bohlale",
                    createList(new String[]{"mokallo", "ho thiba ", "ho opa ka maoto"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Papali eo bana ba qetellang ka ho pheta mantsoe a tšoanang ka lebelo?",
                    "mantsoe a potlakileng",
                    createList(new String[]{"mohobelo", "mokhibo", "mokallo"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Papali eo ho eona ho tšoaroa molamu o mongata, ba bang ba leka ho o nka?",
                    "ho hula molamu",
                    createList(new String[]{"mokhibo", "ho kalla", "tsikitlana"}),
                    null
            ));

        }
        if(category.equals("Maele")) {
            easyQuestions.add(new Question(
                    "Maoto ha a lahloe",
                    "ho khella motho fatše ho sa lebellehe",
                    createList(new String[]{"ho ea hole", "ho matha", "ho palama"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Thupa e otlolloa e sa le metsi",
                    "ho ruta ngoana a sa le monyenyane",
                    createList(new String[]{"ho otla thupa", "ho noa metsi", "ho lema ka pula"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Pelo e bonolo e ja mpshe",
                    "motho ea bonolo o atleha habonolo",
                    createList(new String[]{"motho ea bohale o hlola", "ho rata nama", "ho hloka pelo"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Tau e tšehali ha e tsoale konyana",
                    "ngoana o tšoana le batsoali ba hae",
                    createList(new String[]{"tau e jele konyana", "ngwana o lehlohonolo", "lehoetla le hatsela"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Se etsang bana ba bang, le bana ba hao se ba etse",
                    "lokisa bana bohle ka tekano",
                    createList(new String[]{"se hlokomoloheng bana", "ho ruta bana ba bang", "ho tima mollo"}),
                    null
            ));
            mediumQuestions.add(new Question(
                    "Pelo e monate e etsa lerato le leholo",
                    "boikutlo bo botle bo hohela lerato",
                    createList(new String[]{"pelo e bohloko", "lerato le lahlehileng", "thabo e nyane"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Ntja e loma eo u e tšepileng",
                    "ho senngoa ke motho eo u mo tšepileng",
                    createList(new String[]{"ntja e lapileng", "motho ea mabifi", "tšepo e matla"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Metsi a hloka seboko ha a boele morung",
                    "monyetla ha o lahlehile ha o hlola o khutla",
                    createList(new String[]{"ho noa metsi", "ho lahleha tseleng", "ho nyamela ka metsi"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Se jele liphae ha se satsoe ke lefeela",
                    "motho ea qalileng o tlameha ho felehetsa mosebetsi",
                    createList(new String[]{"ho jeoa ke tlala", "phetho ea mosebetsi", "ho khaola sejo"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Ho shoa ha tau ke bophelo ba phokojwe",
                    "ho hloka matla ha e mong ho nolofalletsa e mong",
                    createList(new String[]{"phokojwe e lla", "tau e matlafatsa e mong", "phihlelo ea bophelo"}),
                    null
            ));
            hardQuestions.add(new Question(
                    "Tšomo ha e boleloe habeli",
                    "tsa bohlokoa li lokela ho hlomphuoa",
                    createList(new String[]{"tsa bohloko li phetoa", "pale e khuts'oane", "tsa bohloko lia qojoa"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Lehlanya lea ruta",
                    "le bohlale le ka tsoa ho mang kapa mang",
                    createList(new String[]{"lehlanya le tšosa", "motho ea phetseng hantle", "pale ea bohata"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Mollo ha o tuke ha ho le le mong",
                    "tšebelisano e etsa katleho",
                    createList(new String[]{"mollo o chesa mong", "ntoa e chesa", "lefifi lea bonesoa"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Motho o motle ka botho, eseng ka seaparo",
                    "botho ke eona e lokelang ho hlomphuoa, eseng ponahalo",
                    createList(new String[]{"seaparo se setle", "bongata ba lintho", "lilemo li bohlokoa"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Se batlang ho shoa se bolaea ntja",
                    "liketso tse kotsi li lebisa timetsong",
                    createList(new String[]{"ntja e nyala", "motho o fosahetse", "molato ha o fuoe ntja"}),
                    null
            ));
        }
        if(category.equals("Lits'omo")) {

            easyQuestions.add(new Question(
                    "mehleng ya khale ke mang ea qoqelang bana lits'mo?",
                    "nkhono",
                    createList(new String[]{"mme", "rakhali", "ntate"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Hone ho thoe bana ba ke nye eng hloohong?",
                    "Lehlokoa",
                    createList(new String[]{"Katiba", "Lerapo", "Kepisi"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "O ne a le kae Tselane ha a kopana le Limo?",
                    "Morung",
                    createList(new String[]{"Leralleng", "Hae", "Holong"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Ke mang ea qhekella Tselane?",
                    "Limo",
                    createList(new String[]{"Mats'atsi", "Tsele", "Ngoana"}),
                    null
            ));

            easyQuestions.add(new Question(
                    "Tselane o ile a fumana eng ka mor'a hore a tlohe hae?",
                    "Mosebetsi",
                    createList(new String[]{"Moputso", "Letho", "Tsoalo"}),
                    null
            ));
            mediumQuestions.add(new Question(
                    "Litsʼomo li phetoa haholo-holo neng?",
                    "Bosiu, ho pota mollo",
                    createList(new String[]{"Hoseng", "Matsatsi a mang", "Mesong"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Molemo wa lits'omo ke eng",
                    "Ho eletsa",
                    createList(new String[]{"Ho liela", "Tselane", "Hare"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Ha ngata moutloanyane o bolelang Lits'omong?",
                    "Bohlale",
                    createList(new String[]{"Molisa", "Morena", "Mohlahlobi"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Tau ke sesupo sa eng Lits'omong?",
                    "Morena",
                    createList(new String[]{"Molisana", "Monna", "Mosali"}),
                    null
            ));

            mediumQuestions.add(new Question(
                    "Phokojoe ke sesupo sa eng Lits'omong?",
                    "Moqhekanyetsi",
                    createList(new String[]{"Masene", "bohole", "Morena"}),
                    null
            ));
            hardQuestions.add(new Question(
                    "Tsʼomo ea moutloanyane le sekolopata e rutang?",
                    "Mamello le sepheo",
                    createList(new String[]{"Bohlale", "Matla kapa puso e sehloho", "Botsoalle"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Limo o ile a fumana eng ka mor'a hore a qhekelle?",
                    "Khokahano e ncha",
                    createList(new String[]{"Thabo e se nang boikarabelo", "Khokahano e ncha", "Mosebetsi oa ho sebelisana"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Ke mang ea ileng a qala ho tseba litaba tsa Limo?",
                    "Tselane",
                    createList(new String[]{"Phokojoe", "Tselane", "Hare"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Lits'omo tsa Sesotho li fana ka thuto efe?",
                    "Ho tšehetsa boikarabelo le ho khothaletsa mohlolo",
                    createList(new String[]{"Ho utsoa", "Matla", "Ho liela"}),
                    null
            ));

            hardQuestions.add(new Question(
                    "Ts'omong ea leshala le naoa leshala le ile itihela ka hara eng?",
                    "Noka",
                    createList(new String[]{"Moru", "Lengope", "Ts'imo"}),
                    null
            ));
        }
        if (category.equals("Moetlo")) {
            // ========== EASY QUESTIONS (1-5) ==========
            easyQuestions.add(new Question(
                    "Ke moetlo ofe o bonoang videoeng?",
                    "Moetlo oa ho bitsa letsatsi",
                    createList(new String[]{"Moetlo oa ho qala", "Moetlo oa ho lla", "Moetlo oa ho bina"}),
                    null,
                    "/videos/mangae.mp4"
            ));

            easyQuestions.add(new Question(
                    "Banna ba etsa eng videoeng?",
                    "Ho bina le ho opa maoto",
                    createList(new String[]{"Ho lla", "Ho qala", "Ho qoqa"}),
                    null,
                    "/videos/mokorotlo.mp4"
            ));

            easyQuestions.add(new Question(
                    "Mokete ona ke oa eng?",
                    "Mokete oa puo",
                    createList(new String[]{"Mokete oa lefu", "Mokete oa lenyalo", "Mokete oa ho hlola"}),
                    null,
                    "/videos/moetlo3.mp4"
            ));

            easyQuestions.add(new Question(
                    "Ke sehlopha sefe se bonoang videoeng?",
                    "Banna ba baholo",
                    createList(new String[]{"Bana", "Basali", "Lits'oantso"}),
                    null,
                    "/videos/moetlo4.mp4"
            ));

            easyQuestions.add(new Question(
                    "Ke lentsoe life tse utloahalang videoeng?",
                    "Lipina tsa setso",
                    createList(new String[]{"Mantsoe a thothokiso", "Lipuo tsa fora", "Mantsoe a bohloko"}),
                    null,
                    "/videos/moetlo5.mp4"
            ));

            // ========== MEDIUM QUESTIONS (6-10) ==========
            mediumQuestions.add(new Question(
                    "Mokete ofe o bonoang videoeng?",
                    "Mokete oa lefu",
                    createList(new String[]{"Mokete oa tsoalo", "Mokete oa lenyalo", "Mokete oa ho hlola"}),
                    null,
                    "/videos/moetlo6.mp4"
            ));

            mediumQuestions.add(new Question(
                    "Ke moetlo oa eng o bonoang moo?",
                    "Moetlo oa ho fana ka mpho",
                    createList(new String[]{"Moetlo oa ho nyala", "Moetlo oa ho qala", "Moetlo oa ho lla"}),
                    null,
                    "/videos/moetlo7.mp4"
            ));

            mediumQuestions.add(new Question(
                    "Ke eng se etsoang ka sekhahla videoeng?",
                    "Ho hlahisoa ha mokorotlo",
                    createList(new String[]{"Ho bina", "Ho qala", "Ho lla"}),
                    null,
                    "/videos/moetlo8.mp4"
            ));

            mediumQuestions.add(new Question(
                    "Ke moetlo oa kae o bonoang videoeng?",
                    "Moetlo oa Basotho",
                    createList(new String[]{"Moetlo oa MaZulu", "Moetlo oa MaXhosa", "Moetlo oa MaNdebele"}),
                    null,
                    "/videos/moetlo9.mp4"
            ));

            mediumQuestions.add(new Question(
                    "Ke lits'omo tsa eng tse bonoang videoeng?",
                    "Lits'omo tsa Basotho",
                    createList(new String[]{"Lits'omo tsa MaIndia", "Lits'omo tsa MaChina", "Lits'omo tsa MaJuda"}),
                    null,
                    "/videos/moetlo10.mp4"
            ));

            // ========== HARD QUESTIONS (11-15) ==========
            hardQuestions.add(new Question(
                    "Ke sehlopha life tse bonoang videoeng?",
                    "Marena le Bahale",
                    createList(new String[]{"Banna le Basali", "Bahlankana le Bahlankana", "Bana le Batsoali"}),
                    null,
                    "/videos/moetlo11.mp4"
            ));

            hardQuestions.add(new Question(
                    "Ke moetlo oa eng o bonoang moo?",
                    "Moetlo oa ho hlompha baholo",
                    createList(new String[]{"Moetlo oa ho nyala", "Moetlo oa ho qala", "Moetlo oa ho lla"}),
                    null,
                    "/videos/moetlo12.mp4"
            ));

            hardQuestions.add(new Question(
                    "Ke eng se hlokang ho etsoa pele ho moetlo ona?",
                    "Ho bitsa baholo",
                    createList(new String[]{"Ho bina", "Ho qala", "Ho lla"}),
                    null,
                    "/videos/moetlo13.mp4"
            ));

            hardQuestions.add(new Question(
                    "Ke moetlo oa kae o bonoang videoeng?",
                    "Moetlo oa Baphuthing",
                    createList(new String[]{"Moetlo oa Bataung", "Moetlo oa Bakoena", "Moetlo oa Bafokeng"}),
                    null,
                    "/videos/mangae.mp4"
            ));

            hardQuestions.add(new Question(
                    "Ke lits'omo tsa eng tse bonoang videoeng?",
                    "Lits'omo tsa bahale ba Basotho",
                    createList(new String[]{"Lits'omo tsa MaIndia", "Lits'omo tsa MaChina", "Lits'omo tsa MaJuda"}),
                    null,
                    "/videos/moetlo15.mp4"
            ));
        }

    }

    private List<String> createList(String[] elements) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
    }

    static class Question {
        private String questionText;
        private String correctAnswer;
        private List<String> wrongAnswers;
        private Image image;
        private String videoPath;
        private List<Image> optionImages;

        // Constructor for text/image questions
        public Question(String questionText, String correctAnswer,
                        List<String> wrongAnswers, Image image) {
            this(questionText, correctAnswer, wrongAnswers, image, null);
        }

        // Constructor for video questions
        public Question(String questionText, String correctAnswer,
                        List<String> wrongAnswers, Image image, String videoPath) {
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
            this.wrongAnswers = wrongAnswers;
            this.image = image;
            this.videoPath = videoPath;
            this.optionImages = new ArrayList<>();
        }

        // Getters
        public String getQuestionText() { return questionText; }
        public String getCorrectAnswer() { return correctAnswer; }
        public List<String> getWrongAnswers() { return wrongAnswers; }
        public Image getImage() { return image; }
        public String getVideoPath() { return videoPath; }
        public List<Image> getOptionImages() { return optionImages; }

        // Helper methods
        public boolean isVideoQuestion() {
            return videoPath != null && !videoPath.isEmpty();
        }

        public boolean isImageQuestion() {
            return image != null;
        }
    }
}