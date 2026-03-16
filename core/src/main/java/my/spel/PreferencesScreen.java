package my.spel;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesScreen implements Screen {

    private Main parent;
    private Stage stage;
    private Skin skin;

    private final String[] themes;
    private int themeIndex;
    private Label themeLabel;

    public static String theme = "theme_bird";
    public static float musicVolume = 0.5f;
    public static float audioVolume = 1f;

    public PreferencesScreen(Main parent) {
        this.parent = parent;
        themes = getThemes();
    }

    public String[] getThemes() {

        List<String> themeFolders = new ArrayList<>();
        FileHandle assetsDir = Gdx.files.internal(".");

        for (FileHandle child : assetsDir.list()) {
            if (child.isDirectory() && child.name().startsWith("theme_")) {
                themeFolders.add(child.name());
            }
        }

        String[] result = new String[themeFolders.size()];
        for (int i = 0; i < themeFolders.size(); i++) {
            String currentFolder = themeFolders.get(i);

            result[i] = currentFolder;
        }

        return result;

    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        themeIndex = Arrays.asList(themes).indexOf(theme);
        if (themeIndex < 0) themeIndex = 0;

        TextButton change = new TextButton("Change", skin);

        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equalsIgnoreCase(theme)) {
                themeIndex = i;
                break;
            }
        }


        themeLabel = new Label(getDisplayName(themes[themeIndex]), skin);

        Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicSlider.setValue(musicVolume);

        Slider audioSlider = new Slider(0f, 1f, 0.01f, false, skin);
        audioSlider.setValue(audioVolume);

        Label musicLabel = new Label("Music Volume", skin);
        Label audioLabel = new Label("Audio Volume", skin);

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolume = musicSlider.getValue();
                parent.music.setVolume(musicVolume);
            }
        });

        audioSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audioVolume = audioSlider.getValue();
            }
        });



        TextButton exitButton = new TextButton("Title Screen", skin);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                parent.goToMenu();
            }
        });


        change.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                themeIndex++;

                if (themeIndex >= themes.length) {
                    themeIndex = 0;
                }

                theme = themes[themeIndex];
                if(Main.previousScreen.equals(Main.ScreenTypes.GAMEPLAY)) {
                    parent.music.stop();
                    parent.music = Gdx.audio.newMusic(Gdx.files.internal(theme + "/music.mp3"));
                    parent.music.setVolume(musicVolume);
                    parent.music.play();
                }
                themeLabel.setText(getDisplayName(themes[themeIndex]));
                theme = theme.toLowerCase();
            }
        });

        Table themeSelector = new Table();
        themeSelector.add(themeLabel).width(150).center();
        themeSelector.add(change).pad(10);

        Table table = new Table();
        table.setFillParent(true);

        table.add(new Label("Theme", skin)).pad(10);
        table.row();
        table.add(themeSelector).pad(20);

        table.row();
        table.add(musicLabel).padTop(20);
        table.row();
        table.add(musicSlider).width(300).pad(10);

        table.row();
        table.add(audioLabel).padTop(20);
        table.row();
        table.add(audioSlider).width(300).pad(10);

        if (Main.previousScreen.equals(Main.ScreenTypes.GAMEPLAY)) {
            TextButton returnButton = new TextButton("Return", skin);

            returnButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dispose();
                    GameplayScreen.pauseTimer = 0.5f;
                    parent.continueGame();
                }
            });
            table.row();
            table.add(returnButton).padTop(30);
        }

        table.row();
        table.add(exitButton).padTop(30);

        stage.addActor(table);
    }

    public String getDisplayName(String name) {
        String displayLabel = name.split("theme_", 0)[1];
        displayLabel = displayLabel.substring(0, 1).toUpperCase() + displayLabel.substring(1);
        return displayLabel;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Main.previousScreen.equals(Main.ScreenTypes.GAMEPLAY)) {
            dispose();
            GameplayScreen.pauseTimer = 0.5f;
            parent.continueGame();
        }
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {

        stage.dispose();
        skin.dispose();
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
}
