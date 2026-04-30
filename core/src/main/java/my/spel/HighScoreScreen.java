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
        font.getData().setScale(0.5f);
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);
        skin = new Skin(Gdx.files.internal(Main.skinPath));
    }

    @Override
    public void show() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // Background image
        backgroundTexture = new Texture("game_assets/main_menu/background_menu.png");

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        style.font.getData().setScale(0.7f);

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
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, width, height);

        stage.addActor(table);

        GlyphLayout layout = new GlyphLayout();

        String placeholder = "Stranger";
        String hardText = "Hard Top 3";
        String mediumText = "Medium Top 3";
        String easyText = "Easy Top 3";

        String highScoreEasy1 = formatHighScore("EASY", 1, "1st", placeholder);
        String highScoreEasy2 = formatHighScore("EASY", 2, "2nd", placeholder);
        String highScoreEasy3 = formatHighScore("EASY", 3, "3rd", placeholder);

        String highScoreMedium1 = formatHighScore("MEDIUM", 1, "1st", placeholder);
        String highScoreMedium2 = formatHighScore("MEDIUM", 2, "2nd", placeholder);
        String highScoreMedium3 = formatHighScore("MEDIUM", 3, "3rd", placeholder);

        String highScoreHard1 = formatHighScore("HARD", 1, "1st", placeholder);
        String highScoreHard2 = formatHighScore("HARD", 2, "2nd", placeholder);
        String highScoreHard3 = formatHighScore("HARD", 3, "3rd", placeholder);


        layout.setText(font, easyText);
        font.draw(spriteBatch, easyText, width / 20, (height / 2) + 270);
        layout.setText(font, highScoreEasy1);
        font.draw(spriteBatch, highScoreEasy1, width / 20, (height / 2) + 180);

        layout.setText(font, highScoreEasy2);
        font.draw(spriteBatch, highScoreEasy2, width / 20, (height / 2) + 90);

        layout.setText(font, highScoreEasy3);
        font.draw(spriteBatch, highScoreEasy3, width / 20, (height / 2));

        layout.setText(font, mediumText);
        font.draw(spriteBatch, mediumText, (width / 2) - 200, (height / 2) + 270);
        layout.setText(font, highScoreMedium1);
        font.draw(spriteBatch, highScoreMedium1, (width / 2) - 200, (height / 2) + 180);

        layout.setText(font, highScoreMedium2);
        font.draw(spriteBatch, highScoreMedium2, (width / 2) - 200, (height / 2) + 90);

        layout.setText(font, highScoreMedium3);
        font.draw(spriteBatch, highScoreMedium3, (width / 2) - 200, (height / 2));

        layout.setText(font, hardText);
        font.draw(spriteBatch, hardText, width - width / 4, (height / 2) + 270);

        layout.setText(font, highScoreHard1);
        font.draw(spriteBatch, highScoreHard1, width - width / 4, (height / 2) + 180);

        layout.setText(font, highScoreHard2);
        font.draw(spriteBatch, highScoreHard2, width - width / 4, (height / 2) + 90);

        layout.setText(font, highScoreHard3);
        font.draw(spriteBatch, highScoreHard3, width - width / 4, (height / 2));



        spriteBatch.end();
        stage.draw();
    }

    private String formatHighScore(String difficulty, int rank, String placeLabel, String placeholder) {
        int score = Main.prefs.getInteger("highscore_" + difficulty + "_" + rank, 0);

        if (score == 0) {
            return placeLabel + ": Empty!";
        }

        String name = Main.prefs.getString("highscore_" + difficulty + "_" + rank + "_name", placeholder);
        return placeLabel + ": " + score + " (by " + name + ")";
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
