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

    FitViewport viewport;
    SpriteBatch spriteBatch;
    Texture gameOverTexture;
    Table table;
    private int score;
    private int highScore;

    BitmapFont font;

    public GameOverScreen(Main parent, int score) {
        this.parent = parent;
        this.score = score;

        font = new BitmapFont(Gdx.files.internal("uifont.fnt"));
        font.getData().setScale(2f);

        highScore = Main.prefs.getInteger("highscore", 0);

        gameOverTexture = new Texture("game_over.png");
        parent.playSound(Gdx.audio.newSound(Gdx.files.internal("game_over_sound.mp3")));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        TextButton menuButton = new TextButton("Title Screen", skin);

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                parent.goToMenu();
            }
        });

        table = new Table();
        table.setFillParent(true);
        table.bottom();
        table.row();
        table.add(menuButton).padBottom(30);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined); // match stage viewport
        spriteBatch.begin();
        spriteBatch.draw(gameOverTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        stage.addActor(table);
        font.draw(spriteBatch, "You scored: " + score, 740, stage.getViewport().getWorldHeight() - 620);
        //font.draw(spriteBatch, "High Score: " + highScore, 755, stage.getViewport().getWorldHeight() - 720);
        font.draw(spriteBatch, "High Score: " + GameplayScreen.highScoreThisSession, 755, stage.getViewport().getWorldHeight() - 720);
        font.draw(spriteBatch, "Press space to try again", 600, stage.getViewport().getWorldHeight() - 820);

        spriteBatch.end();
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispose();
            parent.newGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
