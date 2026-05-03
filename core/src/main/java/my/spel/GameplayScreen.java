package my.spel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
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

    public static Difficulty difficulty = Difficulty.MEDIUM;

    Main parent;
    RandomGenerator random;

    public int scoreThisRound;
    private final float scrollSpeed;
    private float backgroundScrollAmount;
    private float skyScrollAmount;
    private final float gravityConstant;
    private float playerSpeedY;
    private final float jumpSpeed;
    boolean showFlame;
    boolean skipGameOver;

    FitViewport viewport;

    Texture backgroundTexture;
    Texture skyTexture;
    Texture playerTexture;
    Texture playerTextureStill;
    Texture playerTextureDead;
    Texture startingPlatformTexture;
    Texture promptTexture;
    Texture obstacleTexture;
    Texture flameTexture;

    SpriteBatch spriteBatch;

    Sprite playerSprite;
    Sprite startingPlatform;
    Sprite promptSprite;
    Sprite flameSprite;

    Circle playerHitBox;
    ShapeRenderer shapeRenderer;

    String theme;

    private final Sound angelSound;
    private final Sound whoopSound;

    List<Sprite> obstacles;
    float timeToCreateNewObstacle;

    private boolean playerIsDead;
    private boolean initialPause;

    public static float pauseTimer = 0;
    private float deathTimer;
    private boolean deathTimerIsReset;
    float flameFlipTimer;


    float skipDelayTimer;
    BitmapFont font;

    private static boolean displayFpsMeter = false;
    private float fpsMeterTimer;
    private float currentFps;

    public GameplayScreen(Main parent) {
        this.parent = parent;
        random = RandomGenerator.getDefault();

        theme = difficulty.equals(Difficulty.HARD) ? "theme_hard" : "theme_normal";
        applyTheme();

        scoreThisRound = 0;
        font = new BitmapFont(Gdx.files.internal("game_assets/uifont_border.fnt"));
        font.getData().setScale(0.1f);
        font.setColor(Color.WHITE);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(178, 100);
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(15, 15);
        playerSprite.setY(viewport.getWorldHeight() / 2);
        playerSprite.setX(viewport.getWorldWidth() / 2 - (viewport.getWorldWidth() / 4));

        promptSprite = new Sprite(promptTexture);
        promptSprite.setSize(66, 7.5f);
        promptSprite.setX(viewport.getWorldWidth() / 2 - promptSprite.getWidth() / 2);
        promptSprite.setY(viewport.getWorldHeight() / 7);

        startingPlatform = new Sprite(startingPlatformTexture);
        startingPlatform.setY(playerSprite.getY() - 3);
        startingPlatform.setX(playerSprite.getX() - (playerSprite.getWidth() / 8));
        startingPlatform.setSize(playerSprite.getWidth(), 3);

        angelSound = Gdx.audio.newSound(Gdx.files.internal("game_assets/" + theme + "/angel.mp3"));
        whoopSound = Gdx.audio.newSound(Gdx.files.internal("game_assets/" + theme + "/whoop.mp3"));
        jumpSpeed = 70f;
        gravityConstant = -210f;

        playerHitBox = new Circle();
        playerIsDead = false;
        initialPause = true;
        obstacles = new ArrayList<>();
        timeToCreateNewObstacle = 0f;
        scrollSpeed = 40f;
        backgroundScrollAmount = 0;
        skyScrollAmount = 0;

        createPlayerHitbox();
        shapeRenderer = new ShapeRenderer();
        deathTimer = 2f;
        deathTimerIsReset = false;
        flameSprite = new Sprite(flameTexture);
        flameFlipTimer = 0.05f;
        showFlame = false;
        skipGameOver = false;
        skipDelayTimer = 0.5f;

        fpsMeterTimer = 0;
        currentFps = 0;
    }

    public void createNewObstacle() {
        float pipeMargin = switch (difficulty) {
            case EASY -> 32;
            case MEDIUM -> 28;
            case HARD -> 25;
        };

        float obstacleWidth = 15;
        float obstacleHeight = 80;
        float obstacleScreenHeight = random.nextFloat(20, viewport.getWorldHeight() - 50f);

        Sprite obstacleSpriteBottom = new Sprite(obstacleTexture);
        obstacleSpriteBottom.setSize(obstacleWidth, obstacleHeight);
        obstacleSpriteBottom.setY(obstacleScreenHeight - obstacleHeight);
        obstacleSpriteBottom.setX(viewport.getWorldWidth() + 10);

        Sprite obstacleSpriteTop = new Sprite(obstacleTexture);
        obstacleSpriteTop.setSize(obstacleWidth, obstacleHeight);
        obstacleSpriteTop.setY((obstacleSpriteBottom.getHeight() + obstacleSpriteBottom.getY()) + pipeMargin);
        obstacleSpriteTop.setX(viewport.getWorldWidth() + 10);
        obstacleSpriteTop.flip(true, true);

        obstacles.add(obstacleSpriteTop);
        obstacles.add(obstacleSpriteBottom);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (!playerIsDead) {
            input();
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

    private void applyTheme() {
        String assetsRoot = "game_assets/";

        backgroundTexture = new Texture(assetsRoot + theme + "/background.png");
        skyTexture = new Texture(assetsRoot + theme + "/sky.png");
        playerTexture = new Texture(assetsRoot + theme + "/player.png");
        playerTextureStill = new Texture(assetsRoot + theme + "/player_still.png");
        playerTextureDead = new Texture(assetsRoot + theme + "/player_dead.png");
        startingPlatformTexture = new Texture(assetsRoot + theme + "/platform.png");
        promptTexture = new Texture(assetsRoot + theme + "/prompt.png");
        obstacleTexture = new Texture(assetsRoot + theme + "/obstacle.png");
        flameTexture = new Texture(assetsRoot + "theme_hard/flames.png");

    }

    private void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (initialPause) {
                initialPause = false;
            }
            playerSpeedY = jumpSpeed;
            parent.playSound(whoopSound);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            displayFpsMeter = !displayFpsMeter;
            fpsMeterTimer = 0;
        }
    }

    private void logic(float delta) {
        playerSprite.setRotation(MathUtils.clamp(playerSpeedY / 3, -90, 90));

        if (!playerIsDead) {
            createPlayerHitbox();
            if (enoughTimeHasPassedToCreateObstacle(delta)) {
                createNewObstacle();
            }
            moveBackground(delta);
            moveStartingPlatform(delta);
            moveObstacle(delta);
            if (playerHasCollidedWithObstacle()) {
                killPlayer();

            }
            movePlayerGravity(delta);
            checkPlayerCollision();
            if (playerSprite.getY() == -4) {
                killPlayer();
            }
        } else {
            skipDelayTimer -= delta;
            if (theme.equals("theme_normal")) {
                deathTimer -= delta;
                takePlayerToHeaven(delta);
            } else {
                deathTimer -= delta * 2;
                if (!deathTimerIsReset) {
                    takePlayerToHeaven(delta);
                    if (deathTimer <= 0) {
                        resetDeathTimer();
                    }
                } else {
                    movePlayerGravity(delta);
                }
                if (playerSprite.getY() <= 0) {
                    showFlame = true;
                }

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && skipDelayTimer <= 0) {
                deathTimer = 0;
                skipGameOver = true;
            }

            if (isGameOver(delta)) {
                parent.stopSound(angelSound);

                boolean isNewHighScore = playerHasHighScore();

                playerSprite.setRotation(theme.equals("theme_hard") ? 180 : 0);

                Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
                dispose();
                parent.goToGameOver(scoreThisRound, difficulty, isNewHighScore, scoreThisRound, skipGameOver);

            }
        }

    }

    private boolean playerHasHighScore() {
        int[] highScoreToCompareTo = switch (difficulty) {
            case EASY -> Main.easyHighScores;
            case MEDIUM -> Main.mediumHighScores;
            case HARD -> Main.hardHighScores;
        };

        for (int savedHighScore : highScoreToCompareTo) {
            if (scoreThisRound > savedHighScore) {
                return true;
            }
        }
        return false;
    }

    private void takePlayerToHeaven(float delta) {
        playerSpeedY = 15f;
        playerSprite.setRotation(0);
        playerSprite.translateY(playerSpeedY * delta);
    }


    private void checkPlayerCollision() {
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
        if (!playerIsDead) {
            playerSprite.setY(MathUtils.clamp(playerSprite.getY(), -4, viewport.getWorldHeight() - playerSprite.getHeight() + 3));
        }
    }

    private boolean enoughTimeHasPassedToCreateObstacle(float delta) {
        timeToCreateNewObstacle -= delta;
        if (timeToCreateNewObstacle < 0) {
            timeToCreateNewObstacle = 1.6f;
            return true;
        }
        return false;
    }

    private void killPlayer() {
        playerIsDead = true;
        parent.playSound(angelSound);
    }

    private void resetDeathTimer() {
        deathTimer = 4f;
        deathTimerIsReset = true;
    }

    private boolean isGameOver(float delta) {
        if (deathTimer < 0) {
            deathTimer = 0;
        }
        return deathTimer == 0;
    }

    private void movePlayerGravity(float delta) {
        playerSprite.translateY(playerSpeedY * delta);
        playerSpeedY += gravityConstant * delta;
    }

    private void moveObstacle(float delta) {
        boolean hasPassedObstacle = false;

        for (int i = obstacles.size() - 1; i >= 0; i--) {
            float obstacleXBefore = obstacles.get(i).getX();

            obstacles.get(i).translateX(-scrollSpeed * delta);

            float obstacleXAfter = obstacles.get(i).getX();
            if (!hasPassedObstacle && !playerIsDead) {
                if (playerSprite.getX() < obstacleXBefore && playerSprite.getX() > obstacleXAfter) {
                    scoreThisRound++;
                    hasPassedObstacle = true;
                }
            }

            if (obstacles.get(i).getX() < -30) {
                obstacles.remove(i);
            }
        }
    }

    private boolean playerHasCollidedWithObstacle() {
        for (Sprite obstacle : obstacles) {
            Rectangle obstacleHitBox = new Rectangle();
            obstacleHitBox.set(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());

            if (Intersector.overlaps(playerHitBox, obstacleHitBox)) {
                return true;
            }
        }
        return false;
    }

    private void createPlayerHitbox() {
        float playerHitboxSize = 3f;

        playerHitBox.set(
            playerSprite.getX() + (playerSprite.getWidth() / 2),
            playerSprite.getY() + (playerSprite.getHeight() / 2),
            playerSprite.getHeight() / playerHitboxSize
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

        skyScrollAmount += (scrollSpeed / 3) * delta;
        if (skyScrollAmount > viewport.getWorldWidth()) {
            skyScrollAmount = 0;
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


        spriteBatch.draw(skyTexture, -skyScrollAmount, 0, worldWidth, worldHeight);
        spriteBatch.draw(skyTexture, worldWidth - skyScrollAmount, 0, worldWidth, worldHeight);
        spriteBatch.draw(backgroundTexture, -backgroundScrollAmount, 0, worldWidth, worldHeight);
        spriteBatch.draw(backgroundTexture, worldWidth - backgroundScrollAmount, 0, worldWidth, worldHeight);

        for (Sprite obstacle : obstacles) {
            obstacle.draw(spriteBatch);
        }

        playerSprite.draw(spriteBatch);
        startingPlatform.draw(spriteBatch);

        if (showFlame) {
            flameFlipTimer -= delta;
            if (flameFlipTimer <= 0) {
                flameSprite.flip(true, false);
                flameFlipTimer = 0.05f;
            }
            flameSprite.setSize(15, 22);
            flameSprite.setY(0);
            flameSprite.setX(playerSprite.getX());
            flameSprite.draw(spriteBatch);
        }

        if (initialPause) {
            promptSprite.draw(spriteBatch);
        }

        createScoreUi(spriteBatch);

        if (displayFpsMeter) {
            createFpsMeter(spriteBatch, delta);
            fpsMeterTimer -= delta;
        }

        spriteBatch.end();


    }

    private void createScoreUi(SpriteBatch batch) {
        String score = "Score: " + scoreThisRound;
        font.setColor(Color.WHITE);
        font.draw(batch, score, 2, viewport.getWorldHeight() - 2);
    }

    private void createFpsMeter(SpriteBatch batch, float delta) {
        if (fpsMeterTimer <= 0) {
            if (delta == 0) {
                currentFps = -1;
            } else {
                currentFps = 1.0f / delta;
            }
            fpsMeterTimer = 0.5f;
        }
        String fps = String.format("FPS: %.0f", currentFps);
        font.setColor(Color.WHITE);
        font.draw(batch, fps, viewport.getWorldWidth() - 37, viewport.getWorldHeight() - 2);
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
