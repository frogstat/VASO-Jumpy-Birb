package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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


    private RandomGenerator random = RandomGenerator.getDefault();
    private final Main parent;
    private final SpriteBatch spriteBatch;
    private final FitViewport viewport;
    private Stage stage;
    private Skin skin;
    private Table table;

    private Texture backgroundTexture;
    private List<Texture> spriteTextures;
    private List<Sprite> playerSprites;

    private float playerSpriteSpawnTimer;
    private float ballSpawnTimer;
    private final float gravityConstant;
    private List<Ball> balls;

    private float worldWidth;
    private float worldHeight;
    private boolean spawnSpriteLeft;
    private int spriteIndex;
    private float ballSpawnDelay;
    private boolean ballSpawnsLeft;

    private final Sound thunderSound;
    private float thunderSoundDelay;


    public CreditsScreen(Main parent) {
        this.parent = parent;
        playerSpriteSpawnTimer = 0;
        ballSpawnTimer = 6;
        playerSprites = new ArrayList<>();
        balls = new ArrayList<>();
        spriteTextures = createTextureList();
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        skin = new Skin(Gdx.files.internal(Main.skinPath));
        gravityConstant = 6600;
        spawnSpriteLeft = true;
        spriteIndex = 0;
        ballSpawnDelay = 8f;
        ballSpawnsLeft = true;

        thunderSound = Gdx.audio.newSound(Gdx.files.internal("game_assets/theme_hard/angel.mp3"));
        thunderSoundDelay = 0f;
    }

    private List<Texture> createTextureList() {
        List<Texture> textures = new ArrayList<>();
        textures.add(new Texture("game_assets/theme_hard/player_still.png"));
        textures.add(new Texture("game_assets/main_menu/tootsie.png"));
        textures.add(new Texture("game_assets/theme_normal/player_dead.png"));
        textures.add(new Texture("game_assets/theme_normal/player_still.png"));
        textures.add(new Texture("game_assets/theme_hard/player.png"));
        textures.add(new Texture("game_assets/theme_hard/player_dead.png"));
        return textures;
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

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        logic(delta);
        input();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        for (Sprite sprite : playerSprites) {
            sprite.draw(spriteBatch);
        }
        for (Ball ball : balls) {
            ball.getSprite().draw(spriteBatch);
        }

        spriteBatch.end();
        stage.draw();
    }

    private void input() {
        // Easter egg
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (thunderSoundDelay <= 0) {
                thunderSoundDelay = 2f;
                parent.playSound(thunderSound);
            }
            ballSpawnDelay = ballSpawnDelay == 0 ? 8 : 0;
            if (ballSpawnDelay == 0) {
                ballSpawnTimer = 0;
            }
        }
    }

    private void logic(float delta) {
        playerSpriteSpawnTimer -= delta;
        ballSpawnTimer -= delta;
        thunderSoundDelay -= delta;

        if (playerSpriteSpawnTimer <= 0) {
            playerSpriteSpawnTimer = 7;
            spawnNewSprite();
        }

        if (ballSpawnTimer <= 0) {
            ballSpawnTimer = ballSpawnDelay;
            spawnNewBall();
        }

        moveSprites(delta);
        removeSprites();
    }

    private void removeSprites() {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Sprite ballSprite = balls.get(i).getSprite();
            if (ballSprite.getX() > worldWidth + worldWidth / 5 || ballSprite.getX() < 0 - worldWidth / 5) {
                balls.remove(i);
            }
        }
        for (int i = playerSprites.size() - 1; i >= 0; i--) {
            Sprite playerSprite = playerSprites.get(i);
            if (playerSprite.getY() > worldHeight + playerSprite.getHeight()) {
                playerSprites.remove(i);
            }
        }

    }

    private void moveSprites(float delta) {
        for (Sprite sprite : playerSprites) {
            sprite.translateY(33 * delta);
            if (sprite.getTexture().toString().equals("game_assets/theme_hard/player.png")) {
                sprite.rotate(60 * delta);
            }
        }

        for (Ball ball : balls) {
            ball.moveX(delta);
            ball.applyGravity(delta, gravityConstant);
        }
    }

    private void spawnNewBall() {
        float y = worldHeight + worldHeight * 0.1f;
        float x = ballSpawnsLeft ? -worldWidth * 0.1f : worldWidth + worldWidth * 0.1f;
        balls.add(new Ball(x, y, ballSpawnsLeft));
        ballSpawnsLeft = !ballSpawnsLeft;
    }

    private void spawnNewSprite() {
        Texture texture = spriteTextures.get(spriteIndex);
        spriteIndex++;
        if (spriteIndex >= spriteTextures.size()) {
            spriteIndex = 0;
        }

        Sprite newSprite = new Sprite(texture);

        float aspectRatio = newSprite.getHeight() / newSprite.getWidth();
        newSprite.setSize(200, 200 * aspectRatio);

        newSprite.setY(-200);
        newSprite.setOriginCenter();

        float thirdWidth = worldWidth / 3f;
        float minX = spawnSpriteLeft ? 0f : worldWidth - thirdWidth;
        float maxX = spawnSpriteLeft ? thirdWidth - newSprite.getWidth() : worldWidth - newSprite.getWidth();

        spawnSpriteLeft = !spawnSpriteLeft;
        newSprite.setX(random.nextFloat(minX, maxX));

        playerSprites.add(newSprite);
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

    }
}
