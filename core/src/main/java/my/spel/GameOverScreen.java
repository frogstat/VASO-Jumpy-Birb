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

    public GameOverScreen(Main parent, int score, GameplayScreen.Difficulty difficulty, boolean isNewHighScore) {
        this.parent = parent;
        this.score = score;
        this.difficulty = difficulty;
        this.isNewHighScore = isNewHighScore;
        layout = new GlyphLayout();
        skin = new Skin(Gdx.files.internal(Main.skinPath));

        font = new BitmapFont(Gdx.files.internal("game_assets/uifont.fnt"));
        font.getData().setScale(0.9f);

        highScore = Main.prefs.getInteger("highscore", 0);

        gameOverTexture = new Texture("game_assets/main_menu/background_menu.png");
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
                checkUserInput();
                dispose();
//                parent.stopSound(gameOverSound);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            checkUserInput();
            dispose();
            parent.newGame(false);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined); // match stage viewport
        spriteBatch.begin();
        spriteBatch.draw(gameOverTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        centerX = stage.getViewport().getWorldWidth() / 2f;
        centerY = stage.getViewport().getWorldHeight() / 2f;

        String scoreText = "You scored: " + score;
        layout.setText(font, scoreText);
        font.draw(spriteBatch, scoreText,
            centerX - layout.width / 2,
            centerY);

        String highScoreText = "High Score: " + Main.prefs.getInteger("highscore_" + GameplayScreen.difficulty.toString(), 0);
        layout.setText(font, highScoreText);
        font.draw(spriteBatch, highScoreText,
            centerX - layout.width / 2,
            centerY - 100);

        String retryText = "Press space to try again";
        layout.setText(font, retryText);
        font.draw(spriteBatch, retryText,
            centerX - layout.width / 2,
            centerY - 200);

        if (isNewHighScore) {
            getUserTextInput();
        }

        playerSprite.draw(spriteBatch);

        spriteBatch.end();
        stage.draw();
    }

    /**
     * This method shows text input field on the screen when new high score is scored
     */
    public void showUserInputField() {
        nameLabel = new Label("Enter your name and press ENTER to save: ", skin);
        table.add(nameLabel);
        textField = new TextField("", skin);
        table.add(textField).width(200).height(50);
    }

    /**
     * This method gets user text input when new high score is scored
     */
    public void getUserTextInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            userInput = textField.getText();
            saveNameToFile();
            System.out.println(userInput);
        }
    }

    /**
     * This method saves the user input (player name) to file
     */
    public void saveNameToFile() {
        String difficultyString = difficulty.toString();
        Main.prefs.putString("highscore_" + difficultyString + "_" + Main.currentPosition + "_name", userInput);
        Main.prefs.flush();
    }

    /**
     * This method checks if player wrote to text field.
     */
    public void checkUserInput() {
        if (userInput == null || userInput.isEmpty()) {
            userInput = "Stranger";
            saveNameToFile();
        }
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
