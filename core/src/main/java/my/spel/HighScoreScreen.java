package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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

public class HighScoreScreen implements Screen {

    private Main parent;
    private BitmapFont font;
    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private Stage stage;
    private Skin skin;
    Table table;

    public HighScoreScreen(Main parent) {
        this.parent = parent;

        font = new BitmapFont(Gdx.files.internal("uifont.fnt"));
        font.getData().setScale(2f);
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        TextButton menuButton = new TextButton("Title Screen", skin);
//        TextButton resetButton = new TextButton("Reset high score", skin);

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.previousScreen = Main.ScreenTypes.HIGH_SCORE;
                dispose();
                parent.goToMenu();
            }
        });

//        resetButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Main.prefs.putInteger("highscore", 0);
//                GameplayScreen.highScoreThisSession = 0;
//            }
//        });

        table = new Table();
        table.setFillParent(true);
        table.bottom();
        table.row();
//        table.add(resetButton).padBottom(30);
//        table.row();
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
        stage.addActor(table);

        GlyphLayout layout = new GlyphLayout();

        String text1 = "Highest score this session: " + GameplayScreen.highScoreThisSession;
        layout.setText(font, text1);
        font.draw(spriteBatch, text1, (width - layout.width) / 2, height / 2);

//        String text2 = "Highest Score total: " + Main.prefs.getInteger("highscore", 0);
//        layout.setText(font, text2);
//        font.draw(spriteBatch, text2, (width - layout.width) / 2, (height / 2) - 100);

        spriteBatch.end();
        stage.draw();
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
