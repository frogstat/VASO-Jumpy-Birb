package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {

    private static final float VIRTUAL_WIDTH = 1920f;
    private static final float VIRTUAL_HEIGHT = 1080f;

    private Main parent;
    private Stage stage;
    private Skin skin;

    TextButton newGame;
    TextButton highScore;
    TextButton exit;

    Texture backgroundTexture;
    Texture titleTexture;
    Array<Texture> titleHighlightTextures = new Array<>();

    Image titleImage;

    Texture cloudOneTexture;
    Texture cloudTwoTexture;
    Sprite cloudOneSprite;
    Sprite cloudTwoSprite;
    SpriteBatch spriteBatch;

    HighlightAnimationActor highlightAnimationActor;
    Animation<Texture> highlightAnimation;

    public MenuScreen(Main parent) {
        this.parent = parent;
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        skin = new Skin(Gdx.files.internal(Main.skinPath));
    }

    @Override
    public void show() {

        backgroundTexture = new Texture("game_assets/main_menu/Background_menu.png");
        cloudOneTexture = new Texture("game_assets/main_menu/Cloud_1.png");
        cloudTwoTexture = new Texture("game_assets/main_menu/Cloud_2.png");
        titleTexture = new Texture("game_assets/main_menu/Title_1.png");
        titleImage = new Image(titleTexture);
        stage.addActor(titleImage);

        for (int i = 0; i < 26; i++) {
            titleHighlightTextures.add(new Texture(
                "game_assets/main_menu/Title_highlight_" + i + ".png"
            ));
        }

        highlightAnimation = new Animation<>(1 / 70f, titleHighlightTextures);

        // Cloud sprites
        spriteBatch = new SpriteBatch();
        cloudOneSprite = new Sprite(cloudOneTexture);
        cloudOneSprite.setSize(cloudOneTexture.getWidth() * 6f, cloudOneTexture.getHeight() * 6);

        cloudTwoSprite = new Sprite(cloudTwoTexture);
        cloudTwoSprite.setSize(cloudTwoTexture.getWidth() * 6f, cloudTwoTexture.getHeight() * 6f);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        newGame = new TextButton("Start", skin);
        highScore = new TextButton("High Score", skin);
        exit = new TextButton("Exit", skin);

        table.padTop(300);
        table.add(newGame).fillX().uniformX().padBottom(30);
        table.row();
        table.add(highScore).fillX().uniformX().padBottom(30);
        table.row();
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

        layout();// initial positioning
        cloudOneSprite.setY(800);
        cloudOneSprite.setX(200);
        cloudTwoSprite.setY(650);
        cloudTwoSprite.setX(1600);
    }

    private void layout() {
        float width = stage.getViewport().getWorldWidth();
        float height = stage.getViewport().getWorldHeight();


        titleImage.setSize(titleTexture.getWidth(), titleTexture.getHeight());
        titleImage.setPosition(
            (width - titleImage.getWidth()) / 2f,
            height - titleImage.getHeight() * 1.5f
        );


        if (highlightAnimationActor == null) {
            highlightAnimationActor = new HighlightAnimationActor(highlightAnimation, 4,
                titleImage.getX(), titleImage.getY());
            stage.addActor(highlightAnimationActor);
        } else {
            highlightAnimationActor.setPosition(titleImage.getX(), titleImage.getY());
        }
    }

    @Override
    public void render(float delta) {
        if(cloudOneSprite.getX() > VIRTUAL_WIDTH){
            cloudOneSprite.setX(0 - cloudOneSprite.getWidth());
        }
        if(cloudTwoSprite.getX() > VIRTUAL_WIDTH){
            cloudTwoSprite.setX(0 - cloudTwoSprite.getWidth());
        }


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture,0,0,VIRTUAL_WIDTH,VIRTUAL_HEIGHT);
        cloudOneSprite.draw(spriteBatch);
        cloudTwoSprite.draw(spriteBatch);
        spriteBatch.end();
        stage.act(delta);
        stage.draw();

        cloudOneSprite.translateX(40f * delta);
        cloudTwoSprite.translateX(40f * delta);



        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispose();
            parent.changeScreen(Main.ScreenTypes.DIFFICULTY_SELECTOR);
        }
    }

    public void cloudMovement() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        layout(); // recompute positions after resize
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
        backgroundTexture.dispose();
        titleTexture.dispose();
        cloudOneTexture.dispose();
        cloudTwoTexture.dispose();

        for (Texture t : titleHighlightTextures) {
            t.dispose();
        }
    }
}
