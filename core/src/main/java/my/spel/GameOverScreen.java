package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Arrays;

public class GameOverScreen implements Screen {

    Main parent;

    private Stage stage;
    private Skin skin;
    Sound gameOverSound;

    FitViewport viewport;
    SpriteBatch spriteBatch;
    Texture gameOverTexture;
    GlyphLayout layout;
    Table table;
    private float centerX;
    private float centerY;
    private int score;
    private int highScore;
    private boolean isNewHighScore;
    private GameplayScreen.Difficulty difficulty;

    Sprite playerSprite;
    BitmapFont font;

    private TextField textField;
    private String userInput;
    private Label nameLabel;

    int scoreThisRound;
    int playerHighScorePosition;
    boolean skipGameOver;


    public GameOverScreen(Main parent, int score, GameplayScreen.Difficulty difficulty, boolean isNewHighScore, int scoreThisRound, boolean skipGameOver) {
        this.skipGameOver = skipGameOver;
        playerHighScorePosition = -1;
        this.scoreThisRound = scoreThisRound;
        this.parent = parent;
        this.score = score;
        this.difficulty = difficulty;
        this.isNewHighScore = isNewHighScore;
        layout = new GlyphLayout();
        skin = new Skin(Gdx.files.internal(Main.skinPath));

        font = new BitmapFont(Gdx.files.internal("game_assets/uifont.fnt"));
        font.getData().setScale(0.9f);

        highScore = Main.prefs.getInteger("highscore", 0);

        String theme = difficulty == GameplayScreen.Difficulty.HARD ? "theme_hard" : "theme_normal";
        gameOverTexture = new Texture("game_assets/" + theme + "/game_over.png");
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("game_assets/game_over_sound.mp3"));
//        parent.playSound(gameOverSound);
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);
    }

    @Override
    public void show() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        playerSprite = parent.gameplayScreen.playerSprite;
        playerSprite.setScale(25);
        playerSprite.setOriginCenter();
        playerSprite.setY(viewport.getWorldHeight() - viewport.getWorldHeight() / 5);
        playerSprite.setX(viewport.getWorldWidth() / 2);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        style.font.getData().setScale(0.7f);

        TextButton menuButton = new TextButton("Title Screen", skin);

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitGameOverScreen();
                parent.goToMenu();
            }
        });

        table = new Table();
        table.setFillParent(true);
        table.bottom();

        if (isNewHighScore) {
            showUserInputField();
        }

        table.row();
        table.add(menuButton).padBottom(30).padTop(30).colspan(2);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || skipGameOver) {
            exitGameOverScreen();
            parent.newGame(false);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined); // match stage viewport
        spriteBatch.begin();
        spriteBatch.draw(gameOverTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        centerX = stage.getViewport().getWorldWidth() / 2f;
        centerY = stage.getViewport().getWorldHeight() / 2f;

        String messageText = isNewHighScore ? "You made it to the top 3!" : "Game over!";

        int highestScore = Main.prefs.getInteger("highscore_" + GameplayScreen.difficulty + "_1", 0);
        String highestScoreName = Main.prefs.getString("highscore_" + GameplayScreen.difficulty + "_1_name", "");
        if (scoreThisRound > highestScore) {
            highestScore = scoreThisRound;
            highestScoreName = "You";
            messageText = "You beat the high score!";
        }

        String highScoreText = highestScore > 0 ? "Best player: " + highestScoreName + " - " + highestScore : "";

        layout.setText(font, messageText);
        font.draw(spriteBatch, messageText,
            centerX - layout.width / 2,
            centerY + 100);

        String scoreText = "You scored: " + score;
        layout.setText(font, scoreText);
        font.draw(spriteBatch, scoreText,
            centerX - layout.width / 2,
            centerY);


        layout.setText(font, highScoreText);
        font.draw(spriteBatch, highScoreText,
            centerX - layout.width / 2,
            centerY - 100);

        String retryText = "Press space to try again";
        layout.setText(font, retryText);
        font.draw(spriteBatch, retryText,
            centerX - layout.width / 2,
            centerY - 200);
        playerSprite.draw(spriteBatch);

        spriteBatch.end();
        stage.draw();
    }

    private void exitGameOverScreen() {
        if (isNewHighScore) {
            handleHighScore();
        }
        dispose();
    }

    private void handleHighScore() {

        int[] highScoreToCompareTo = switch (difficulty) {
            case EASY -> Main.easyHighScores;
            case MEDIUM -> Main.mediumHighScores;
            case HARD -> Main.hardHighScores;
        };

        String[] highScoreNames = parseHighScoreNames();

        for (int i = 0; i < highScoreToCompareTo.length; i++) {
            if (scoreThisRound > highScoreToCompareTo[i]) {

                playerHighScorePosition = i + 1;

                for (int j = highScoreToCompareTo.length - 1; j > i; j--) {
                    highScoreToCompareTo[j] = highScoreToCompareTo[j - 1];
                    highScoreNames[j] = highScoreNames[j - 1];
                }

                highScoreToCompareTo[i] = scoreThisRound;

                String userName;
                if (textField.getText() == null || textField.getText().trim().isEmpty()) {
                    userName = "Stranger";
                } else {
                    userName = truncate(textField.getText());
                }

                highScoreNames[i] = userName;
                saveHighScoreToFile(highScoreNames, highScoreToCompareTo);
                break;
            }
        }
    }

    private String truncate(String text) {
        if (text == null) return null;
        return text.length() <= 15 ? text : text.substring(0, 10);
    }

    private String[] parseHighScoreNames() {

        String[] highScoreNames = new String[3];

        for (int i = 0; i < highScoreNames.length; i++) {
            highScoreNames[i] = Main.prefs.getString("highscore_" + difficulty + "_" + (i + 1) + "_name", "Stranger");
        }

        return highScoreNames;
    }

    /**
     * This method shows text input field on the screen when new high score is scored
     */
    public void showUserInputField() {
        nameLabel = new Label("Enter your name (Max 10 letters): ", skin);
        table.add(nameLabel);
        textField = new TextField("", skin);
        table.add(textField).width(200).height(50);
    }

    /**
     * This method saves the user input (player name) to file
     */
    public void saveHighScoreToFile(String[] names, int[] scores) {

        for (int i = 1; i <= 3; i++) {
            Main.prefs.putString("highscore_" + difficulty + "_" + i + "_name", names[i - 1]);
            Main.prefs.putInteger("highscore_" + difficulty + "_" + i, scores[i - 1]);
        }


        Main.prefs.flush();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
