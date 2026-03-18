package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.*;
import java.util.random.RandomGenerator;

public class GameplayScreen implements Screen {

    public enum Difficulty {
        EASY, MEDIUM, HARD;
    }

    Main parent;

    public static int highScore = 0;
    public int scoreThisRound;
    private final float scrollSpeed;
    private float backgroundScrollAmount;
    public static Difficulty difficulty = Difficulty.MEDIUM;

    private final float gravityConstant;
    private float playerSpeedY;

    FitViewport viewport;

    Texture backgroundTexture;
    Texture playerTexture;
    Texture playerTextureStill;
    Texture playerTextureDead;
    Texture playerHitBoxTexture;

    SpriteBatch spriteBatch;

    Sprite playerSprite;
    Sprite startingPlatform;

    Rectangle playerHitBox;

    String theme;

    List<Sprite> obstacles;
    float timeToCreateNewObstacle;

    private boolean playerIsDead;
    private boolean initialPause;

    public static float pauseTimer = 0;

    public GameplayScreen(Main parent) {
        this.parent = parent;
        theme = PreferencesScreen.theme;
        checkTheme(true);
        gravityConstant = -2f;

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(178, 100);
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(15, 15);
        playerSprite.setY(viewport.getWorldHeight() / 2);
        playerSprite.setX(viewport.getWorldWidth() / 2 - (viewport.getWorldWidth() / 4));

        startingPlatform = new Sprite(new Texture("theme_bird/platform.png"));
        startingPlatform.setY(playerSprite.getY() - 3);
        startingPlatform.setX(playerSprite.getX() - (playerSprite.getWidth() / 8));
        startingPlatform.setSize(playerSprite.getWidth(), 3);

        playerHitBox = new Rectangle();
        playerHitBoxTexture = new Texture("player_hitbox.png");
        playerIsDead = false;
        initialPause = true;
        obstacles = new ArrayList<>();
        timeToCreateNewObstacle = 0f;
        scrollSpeed = 30f;
        backgroundScrollAmount = 0;

        createPlayerHitbox();
    }

    public void createNewObstacle() {
        float pipeMargin = switch (difficulty) {
            case EASY -> 35;
            case MEDIUM -> 30;
            case HARD -> 25;
        };

        RandomGenerator random = RandomGenerator.getDefault();
        float obstacleWidth = 15;
        float obstacleHeight = 80;
        float obstacleScreenHeight = random.nextFloat(20, viewport.getWorldHeight() - 50f);

        Sprite obstacleSpriteBottom = new Sprite(new Texture("obstacle.png"));
        obstacleSpriteBottom.setSize(obstacleWidth, obstacleHeight);
        obstacleSpriteBottom.setY(obstacleScreenHeight - obstacleHeight);
        obstacleSpriteBottom.setX(viewport.getWorldWidth() + 10);

        Sprite obstacleSpriteTop = new Sprite(new Texture("obstacle.png"));
        obstacleSpriteTop.setSize(obstacleWidth, obstacleHeight);
        obstacleSpriteTop.setY((obstacleSpriteBottom.getHeight() + obstacleSpriteBottom.getY()) + pipeMargin);
        obstacleSpriteTop.setX(viewport.getWorldWidth() + 10);
        obstacleSpriteTop.flip(false,true);

        System.out.println("Created obstacle");
        obstacles.add(obstacleSpriteTop);
        obstacles.add(obstacleSpriteBottom);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        checkTheme(false);

        if (!playerIsDead) {
            input(delta);
        }

        if (pauseTimer == 0 && !initialPause) {
            logic(delta);
        }

        draw(delta);

        if (pauseTimer > 0) {
            pauseTimer -= delta;
        }
        if (pauseTimer < 0) {
            pauseTimer = 0;
        }
    }

    private void checkTheme(boolean firstTime) {
        if (firstTime || !theme.equals(PreferencesScreen.theme)) {
            theme = PreferencesScreen.theme;
            backgroundTexture = new Texture(theme + "/background.png");
            playerTexture = new Texture(theme + "/player.png");
            playerTextureStill = new Texture(theme + "/player_still.png");
            playerTextureDead = new Texture(theme + "/player_dead.png");


        }
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            initialPause = false;
            playerSpeedY = 0.5f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
            parent.showPreferencesScreen();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            int themeIndex = 0;
            Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
            parent.preferencesScreen = new PreferencesScreen(parent);
            String[] themes = parent.preferencesScreen.getThemes();
            for (int i = 0; i < themes.length; i++) {
                if (themes[i].equalsIgnoreCase(theme)) {
                    themeIndex = i;
                    break;
                }
            }

            themeIndex++;

            if (themeIndex >= themes.length) {
                themeIndex = 0;
            }

            PreferencesScreen.theme = themes[themeIndex].toLowerCase();
            if (Main.previousScreen.equals(Main.ScreenTypes.GAMEPLAY)) {
                parent.music.stop();
                parent.music = Gdx.audio.newMusic(Gdx.files.internal(PreferencesScreen.theme + "/music.mp3"));
                parent.music.setVolume(PreferencesScreen.musicVolume);
                parent.music.play();
            }
            System.out.println("Switching to " + PreferencesScreen.theme);
        }
    }

    private void logic(float delta) {
        playerSprite.setRotation(MathUtils.clamp(playerSpeedY * 20, -90, 90));

        moveBackground(delta);
        moveStartingPlatform(delta);
        createPlayerHitbox();

        if (enoughTimeHasPassedToCreateObstacle(delta)) {
            createNewObstacle();
        }
        if (!playerIsDead) {
            if (playerHasCollidedWithObstacle()) {
                killPlayer();
            }
        }

        moveObstacle(delta);
        movePlayerGravity(delta);
        checkPlayerCollision();

        if (playerSprite.getY() == 0) {
            killPlayer();
        }

        if (isGameOver()) {
            Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
            dispose();
            parent.stopMusic();
            parent.goToGameOver();
        }
    }

    private void checkPlayerCollision() {
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
        if (!playerIsDead) {
            playerSprite.setY(MathUtils.clamp(playerSprite.getY(), 0, viewport.getWorldHeight() - playerSprite.getHeight()));
        }
    }

    private boolean enoughTimeHasPassedToCreateObstacle(float delta) {
        timeToCreateNewObstacle -= delta;
        if (timeToCreateNewObstacle < 0) {
            timeToCreateNewObstacle = 3;
            return true;
        }
        return false;
    }

    private void killPlayer() {
        playerIsDead = true;
        parent.playSound(Gdx.audio.newSound(Gdx.files.internal(theme + "/death_sound.mp3")));
    }

    private boolean isGameOver() {
        return playerSprite.getY() < -200;
    }

    private void movePlayerGravity(float delta) {
        playerSprite.translateY(playerSpeedY);
        playerSpeedY += gravityConstant * delta;
    }

    private void moveObstacle(float delta) {
        for (int i = obstacles.size() - 1; i >= 0; i--) {

            obstacles.get(i).translateX(-20 * delta);

            if (obstacles.get(i).getX() < -30) {
                System.out.println("Removed out of bounds obstacle");
                obstacles.remove(i);
            }
        }
    }

    private boolean playerHasCollidedWithObstacle() {
        for (Sprite obstacle : obstacles) {
            Rectangle obstacleHitBox = new Rectangle();
            obstacleHitBox.set(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());

            if (playerHitBox.overlaps(obstacleHitBox)) {
                return true;
            }
        }
        return false;
    }

    private void createPlayerHitbox() {
        playerHitBox.set(
            playerSprite.getX() + playerSprite.getWidth() / 4f,
            playerSprite.getY() + playerSprite.getHeight() / 4f,
            playerSprite.getWidth() / 2f,
            playerSprite.getHeight() / 2f
        );
    }

    private void moveStartingPlatform(float delta) {
        startingPlatform.translateX(-scrollSpeed * delta);
    }

    private void moveBackground(float delta) {
        backgroundScrollAmount += (scrollSpeed / 2) * delta;
        if (backgroundScrollAmount > viewport.getWorldWidth()) {
            backgroundScrollAmount = 0;
        }
    }

    private void draw(float delta) {
        if (playerSpeedY == 0) {
            playerSprite.setTexture(playerTextureStill);
        } else if (playerIsDead) {
            playerSprite.setTexture(playerTextureDead);
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

        spriteBatch.draw(backgroundTexture, -backgroundScrollAmount, 0, worldWidth, worldHeight);
        spriteBatch.draw(backgroundTexture, worldWidth - backgroundScrollAmount, 0, worldWidth, worldHeight);

        for (Sprite obstacle : obstacles) {
            obstacle.draw(spriteBatch);
        }


        playerSprite.draw(spriteBatch);
        startingPlatform.draw(spriteBatch);


        if (theme.equals("theme_simple")) {
            spriteBatch.draw(playerHitBoxTexture, playerHitBox.getX(), playerHitBox.getY(), playerHitBox.getWidth(), playerHitBox.getHeight());
        }



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
