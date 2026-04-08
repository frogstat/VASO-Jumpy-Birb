package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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
    private int score;
    private int highScore;

    BitmapFont font;

    public GameOverScreen(Main parent, int score) {
        this.parent = parent;
        this.score = score;
        layout = new GlyphLayout();
        skin = new Skin(Gdx.files.internal(Main.skinPath));

        font = new BitmapFont(Gdx.files.internal("game_assets/uifont.fnt"));
        font.getData().setScale(0.6f);

        highScore = Main.prefs.getInteger("highscore", 0);

        gameOverTexture = new Texture("game_assets/game_over.png");
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("game_assets/game_over_sound.mp3"));
        parent.playSound(gameOverSound);
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);


        TextButton menuButton = new TextButton("Title Screen", skin);

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                parent.stopSound(gameOverSound);
                parent.goToMenu();
            }
        });

        table = new Table();
        table.setFillParent(true);
        table.bottom();
        table.row();
        table.add(menuButton).padBottom(30);
        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined); // match stage viewport
        spriteBatch.begin();
        spriteBatch.draw(gameOverTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        float centerX = stage.getViewport().getWorldWidth() / 2f;
        float centerY = stage.getViewport().getWorldHeight() / 2f;

        String scoreText = "You scored: " + score;
        layout.setText(font, scoreText);
        font.draw(spriteBatch, scoreText,
            centerX - layout.width / 2,
            centerY - 80);

        String highScoreText = "High Score: " + Main.prefs.getInteger("highscore_" + GameplayScreen.difficulty.toString(), 0);
        layout.setText(font, highScoreText);
        font.draw(spriteBatch, highScoreText,
            centerX - layout.width / 2,
            centerY - 120);

        String retryText = "Press space to try again";
        layout.setText(font, retryText);
        font.draw(spriteBatch, retryText,
            centerX - layout.width / 2,
            centerY - 160);

        spriteBatch.end();
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispose();
            parent.newGame();
            parent.stopSound(gameOverSound);
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
