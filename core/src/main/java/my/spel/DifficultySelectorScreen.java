package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DifficultySelectorScreen implements Screen {

    private Main parent;

    private Stage stage;
    private Skin skin;

    TextButton easy;
    TextButton medium;
    TextButton hard;

    public DifficultySelectorScreen(Main parent) {
        this.parent = parent;
        skin = new Skin(Gdx.files.internal(Main.skinPath));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Table table = new Table();
        table.setFillParent(true);
        table.padTop(300);
        stage.addActor(table);

        easy = new TextButton("Easy", skin);
        medium = new TextButton("Medium", skin);
        hard = new TextButton("Hard", skin);

        table.add(easy);
        table.pad(0, 30, 0, 30);
        table.add(medium);
        table.pad(0, 30, 0, 30);
        table.add(hard);

        Gdx.input.setInputProcessor(stage);

        easy.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                startGame(GameplayScreen.Difficulty.EASY);
            }
        });

        medium.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                startGame(GameplayScreen.Difficulty.MEDIUM);
            }
        });

        hard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                startGame(GameplayScreen.Difficulty.HARD);
            }
        });

    }

    public void startGame(GameplayScreen.Difficulty difficulty){
        parent.stopMusic();
        GameplayScreen.difficulty = difficulty;
        parent.newGame();
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
    }
}
