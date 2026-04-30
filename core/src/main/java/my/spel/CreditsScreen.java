package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class CreditsScreen implements Screen {


    private final Main parent;
    private final SpriteBatch spriteBatch;
    private final FitViewport viewport;
    private Stage stage;
    private Skin skin;
    Table table;
    Texture backgroundTexture;
    List<Sprite> playerSprites;
    float playerSpriteSpawnTimer;
    Texture playerTexture1 = new Texture("game_assets/theme_normal/player_dead.png");
    Texture playerTexture2 = new Texture("game_assets/theme_hard/player_dead.png");
    RandomGenerator random = RandomGenerator.getDefault();

    public CreditsScreen(Main parent) {
        this.parent = parent;
        playerSpriteSpawnTimer = 0;
        playerSprites = new ArrayList<>();
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);
        skin = new Skin(Gdx.files.internal(Main.skinPath));
    }

    @Override
    public void show() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        style.font.getData().setScale(0.7f);
        // Background image
        backgroundTexture = new Texture("game_assets/credits.png");
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
        logic(delta);
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, width, height);
        for (Sprite sprite : playerSprites) {
            sprite.draw(spriteBatch);
        }
        stage.addActor(table);
        spriteBatch.end();
        stage.draw();
    }

    private void logic(float delta) {
        playerSpriteSpawnTimer -= delta;
        if (playerSpriteSpawnTimer <= 0) {
            playerSpriteSpawnTimer = 5;
            spawnNewPlayer();
        }

        for (Sprite sprite : playerSprites) {
            sprite.translateY(33 * delta);
        }
    }

    private void spawnNewPlayer() {
        Texture texture = random.nextInt(0, 2) == 0 ? playerTexture1 : playerTexture2;

        Sprite newSprite = new Sprite(texture);
        newSprite.setSize(200, 200);
        newSprite.setY(-200);

        float worldWidth = viewport.getWorldWidth();
        boolean spawnLeft = random.nextBoolean();

        float minX, maxX;

        if (spawnLeft) {
            minX = 0;
            maxX = (worldWidth / 3) - newSprite.getWidth();
        } else {
            minX = worldWidth - (worldWidth / 3);
            maxX = worldWidth - newSprite.getWidth();
        }

        float x = random.nextFloat(minX, maxX);
        newSprite.setX(x);

        playerSprites.add(newSprite);
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
