package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HighScoreScreen implements Screen {

    private final Main parent;
    private final BitmapFont font;
    private final SpriteBatch spriteBatch;
    private final FitViewport viewport;
    private Stage stage;
    private Skin skin;
    Table table;
    Texture backgroundTexture;

    public HighScoreScreen(Main parent) {
        this.parent = parent;

        font = new BitmapFont(Gdx.files.internal("game_assets/uifont.fnt"));
        font.getData().setScale(0.6f);
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1280, 720);
        skin = new Skin(Gdx.files.internal(Main.skinPath));
    }

    @Override
    public void show() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // Background image
        backgroundTexture = new Texture("game_assets/main_menu/Background_menu.png");

        TextButton menuButton = new TextButton("Title Screen", skin);

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.previousScreen = Main.ScreenTypes.HIGH_SCORE;
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
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined); // match stage viewport
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());

        stage.addActor(table);

        GlyphLayout layout = new GlyphLayout();

        String highScoreHard = "Highest Score Hard: " + Main.prefs.getInteger("highscore_HARD", 0);
        String highScoreMedium = "Highest Score Medium: " + Main.prefs.getInteger("highscore_MEDIUM", 0);
        String highScoreEasy = "Highest Score Easy: " + Main.prefs.getInteger("highscore_EASY", 0);
        layout.setText(font, highScoreHard);
        font.draw(spriteBatch, highScoreHard, (width - layout.width) / 2, (height / 2));

        layout.setText(font, highScoreMedium);
        font.draw(spriteBatch, highScoreMedium, (width - layout.width) / 2, (height / 2) + 50);

        layout.setText(font, highScoreEasy);
        font.draw(spriteBatch, highScoreEasy, (width - layout.width) / 2, (height / 2) + 100);

        spriteBatch.end();
        stage.draw();
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

    }
}
