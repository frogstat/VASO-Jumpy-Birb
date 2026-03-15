package my.spel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameplayScreen implements Screen {

    Main parent;

    public static int highScore = 0;
    public int scoreThisRound;

    private final float gravityConstant;
    private float playerSpeedY;

    FitViewport viewport;

    Texture backgroundTexture;
    Texture playerTexture;
    Texture playerTextureStill;

    SpriteBatch spriteBatch;

    Sprite playerSprite;

    Rectangle playerHitBox;

    String theme;

    public static float pauseTimer = 0;

    public GameplayScreen(Main parent) {
        this.parent = parent;
        theme = PreferencesScreen.theme;
        checkTheme(true);
        gravityConstant = -4f;

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(150, 100);
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(15, 15);
        playerSprite.setY(viewport.getWorldHeight() / 2);
        playerSprite.setX(viewport.getWorldWidth() / 2 - (viewport.getWorldWidth() / 4));
        playerHitBox = new Rectangle();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        checkTheme(false);
        input(delta);
        logic(delta);
        draw();
    }

    private void checkTheme(boolean firstTime) {
        if (firstTime || !theme.equals(PreferencesScreen.theme)) {
            theme = PreferencesScreen.theme;
            backgroundTexture = new Texture(theme + "/background.png");
            playerTexture = new Texture(theme + "/player.png");
            playerTextureStill = new Texture(theme + "/player_still.png");

            parent.music.stop();
            parent.music = Gdx.audio.newMusic(Gdx.files.internal(theme + "/music.mp3"));
            parent.music.play();
        }
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            playerSpeedY = 1f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
            parent.showPreferencesScreen();
        }
    }

    private void logic(float delta) {
        if(pauseTimer == 0) {
            playerHitBox.set(
                playerSprite.getX() + playerSprite.getWidth() / 4f,
                playerSprite.getY() + playerSprite.getHeight() / 4f,
                playerSprite.getWidth() / 2f,
                playerSprite.getHeight() / 2f
            );

            playerSprite.translateY(playerSpeedY);
            playerSpeedY += gravityConstant * delta;

            playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
            playerSprite.setY(MathUtils.clamp(playerSprite.getY(), 0, viewport.getWorldHeight() - playerSprite.getHeight()));

            if (playerSprite.getY() == 0) {
                playerSpeedY = 0;
            }

        }


        if(pauseTimer > 0){
            pauseTimer -= delta;
        }
        if (pauseTimer < 0){
            pauseTimer = 0;
        }
    }

    private void draw() {
        if (playerSpeedY == 0) {
            playerSprite.setTexture(playerTextureStill);
        } else {
            playerSprite.setTexture(playerTexture);
        }

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        playerSprite.setOrigin(playerSprite.getWidth() / 2f, playerSprite.getHeight() / 2f);
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        playerSprite.setRotation(MathUtils.clamp(playerSpeedY * 20, -90, 90));

        playerSprite.draw(spriteBatch);
        spriteBatch.end();
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
