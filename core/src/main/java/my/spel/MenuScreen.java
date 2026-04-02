package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

    private Main parent;
    private Stage stage;
    private Skin skin;
    TextButton newGame;
    TextButton preferences;
    TextButton highScore;
    TextButton exit;

    Texture backgroundTexture;
    Texture titleTexture;
    Array<Texture> titleHighlightTextures = new Array<>();
    Image backgroundImage;
    Image titleImage;

    public MenuScreen(Main parent) {
        this.parent = parent;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal(Main.skinPath));
    }

    @Override
    public void show() {

        backgroundTexture = new Texture("game_assets/theme_main_menu/Sky menu.png");
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
        backgroundImage.setY(0f);
        backgroundImage.setX(0f);
        stage.addActor(backgroundImage);

        titleTexture = new Texture("game_assets/theme_main_menu/Title_1.png");
        titleImage = new Image(titleTexture);
        titleImage.setSize(titleTexture.getWidth(), titleTexture.getHeight());
        titleImage.setX((stage.getWidth() / 2f) - (titleTexture.getWidth() / 2f));
        titleImage.setY(stage.getHeight() - (titleTexture.getHeight() * 1.5f));
        stage.addActor(titleImage);

        // Get all images for the highlight effect
        for (int i = 0; i < 26; i++) {
            final String titleHighlightPath = "game_assets/theme_main_menu/Title_highlight_" + i + ".png";
            final Texture titleHighlightTexture = new Texture(titleHighlightPath);
            titleHighlightTextures.add(titleHighlightTexture);
        }

        Animation<Texture> highlightAnimation = new Animation<>(1/70f, titleHighlightTextures);
        AnimationActor animationActor = new AnimationActor(highlightAnimation, 4, titleImage.getX(), titleImage.getY());
        stage.addActor(animationActor);

        Table table = new Table();
        table.setFillParent(true);
        table.padTop(300);
        stage.addActor(table);

        newGame = new TextButton("Start", skin);
        highScore = new TextButton("High Score", skin);
        exit = new TextButton("Exit", skin);


        table.add(newGame).fillX().uniformX().padBottom(30);
        table.row().pad(10, 0, 30, 0);
        table.row();
        table.row().pad(10, 0, 30, 0);
        table.add(highScore).fillX().uniformX();
        table.row().pad(10, 0, 30, 0);
        table.add(exit).fillX().uniformX();

        Gdx.input.setInputProcessor(stage);

        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                parent.changeScreen(Main.ScreenTypes.DIFFICULTY_SELECTOR);
            }
        });

        highScore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                parent.showHighScore();
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispose();
            parent.changeScreen(Main.ScreenTypes.DIFFICULTY_SELECTOR);
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

    }
}
