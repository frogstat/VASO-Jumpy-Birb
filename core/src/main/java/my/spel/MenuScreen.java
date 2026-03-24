package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

    private Main parent;
    private Stage stage;
    private Skin skin;
    TextButton newGame;
    TextButton preferences;
    TextButton highScore;
    TextButton exit;

    public MenuScreen(Main parent) {
        this.parent = parent;
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {

        Table table = new Table();
        table.setFillParent(true);
        table.padTop(300);
        stage.addActor(table);

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        newGame = new TextButton("Start", skin);
        preferences = new TextButton("Options", skin);
        highScore = new TextButton("High Score", skin);
        exit = new TextButton("Exit", skin);


        table.add(newGame).fillX().uniformX().padBottom(30);
        table.row().pad(10, 0, 30, 0);
        table.add(preferences).fillX().uniformX();
        table.row().pad(10, 0, 30, 0);
        table.row();
        table.row().pad(10, 0, 30, 0);;
        table.add(highScore).fillX().uniformX();
        table.row().pad(10, 0, 30, 0);;
        table.add(exit).fillX().uniformX();

        Gdx.input.setInputProcessor(stage);


        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                parent.stopMusic();
                parent.newGame();
            }
        });

        preferences.addListener(new ChangeListener() {
            @Override

            public void changed(ChangeEvent event, Actor actor) {
                Main.previousScreen = Main.ScreenTypes.MAIN_MENU;
                dispose();
                parent.showPreferencesScreen();
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
