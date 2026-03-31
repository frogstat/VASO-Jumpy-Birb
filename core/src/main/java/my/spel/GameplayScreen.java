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

    Main parent;

    public static int highScoreThisSession = 0;
    public int scoreThisRound;
    private final float scrollSpeed;
    private float backgroundScrollAmount;
    private float skyScrollAmount;
    //public static Difficulty difficulty = Difficulty.valueOf(Main.prefs.getString("difficulty", Difficulty.MEDIUM.name()));
    public static Difficulty difficulty = Difficulty.MEDIUM;
    private final float gravityConstant;
    private float playerSpeedY;
    private float jumpSpeed;

    FitViewport viewport;

    Texture backgroundTexture;
    Texture skyTexture;
    Texture playerTexture;
    Texture playerTextureStill;
    Texture playerTextureDead;
    Texture startingPlatformTexture;
    Texture promptTexture;

    SpriteBatch spriteBatch;
    SpriteBatch uiBatch;

    Sprite playerSprite;
    Sprite startingPlatform;
    Sprite promptSprite;

    Circle playerHitBox;
    ShapeRenderer shapeRenderer;

    String theme;

    private Sound angelSound;

    List<Sprite> obstacles;
    float timeToCreateNewObstacle;

    private boolean playerIsDead;
    private boolean initialPause;

    public static float pauseTimer = 0;
    private float deathTimer;

    BitmapFont font;

    public GameplayScreen(Main parent) {
        this.parent = parent;
        theme = PreferencesScreen.theme;
        checkTheme(true);

        scoreThisRound = 0;
        uiBatch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("uifont.fnt"));
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(178, 100);
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(15, 15);
        playerSprite.setY(viewport.getWorldHeight() / 2);
        playerSprite.setX(viewport.getWorldWidth() / 2 - (viewport.getWorldWidth() / 4));

        promptSprite = new Sprite(promptTexture);
        promptSprite.setSize(66, 7.5f);
        promptSprite.setX(viewport.getWorldWidth()/2 - promptSprite.getWidth()/2);
        promptSprite.setY(viewport.getWorldHeight() / 7);

        startingPlatform = new Sprite(startingPlatformTexture);
        startingPlatform.setY(playerSprite.getY() - 3);
        startingPlatform.setX(playerSprite.getX() - (playerSprite.getWidth() / 8));
        startingPlatform.setSize(playerSprite.getWidth(), 3);

        angelSound = Gdx.audio.newSound(Gdx.files.internal(theme + "/angel.mp3"));
        jumpSpeed = 70f;
        gravityConstant = -210f;

        playerHitBox = new Circle();
        playerIsDead = false;
        initialPause = true;
        obstacles = new ArrayList<>();
        timeToCreateNewObstacle = 0f;
        scrollSpeed = 30f;
        backgroundScrollAmount = 0;
        skyScrollAmount = 0;

        createPlayerHitbox();
        shapeRenderer = new ShapeRenderer();
        deathTimer = 2f;
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
        obstacleSpriteTop.flip(false, true);

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
            skyTexture = new Texture(theme + "/sky.png");
            playerTexture = new Texture(theme + "/player.png");
            playerTextureStill = new Texture(theme + "/player_still.png");
            playerTextureDead = new Texture(theme + "/player_dead.png");
            startingPlatformTexture = new Texture(theme + "/platform.png");
            promptTexture = new Texture(theme + "/prompt.png");
        }
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            initialPause = false;
            playerSpeedY = jumpSpeed;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
            parent.showPreferencesScreen();
        }
    }

    private void logic(float delta) {
        playerSprite.setRotation(MathUtils.clamp(playerSpeedY/3, -90, 90));

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
            takePlayerToHeaven(delta);

            if (isGameOver(delta)) {
                parent.stopSound(angelSound);
                int savedHighScore = Main.prefs.getInteger("highscore", 0);
                if (scoreThisRound > savedHighScore) {
                    Main.prefs.putInteger("highscore", scoreThisRound);
                    Main.prefs.flush();
                }

                if (scoreThisRound > highScoreThisSession) {
                    highScoreThisSession = scoreThisRound;
                }

                Main.previousScreen = Main.ScreenTypes.GAMEPLAY;
                dispose();
                parent.stopMusic();
                parent.goToGameOver(scoreThisRound);
            }
        }

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
            timeToCreateNewObstacle = 1.5f;
            return true;
        }
        return false;
    }

    private void killPlayer() {
        playerIsDead = true;
        parent.playSound(angelSound);
    }

    private boolean isGameOver(float delta) {
        deathTimer -= delta;

        if(deathTimer < 0){
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
//        playerHitBox.set(
//            playerSprite.getX() + playerSprite.getWidth() / 4f,
//            playerSprite.getY() + playerSprite.getHeight() / 4f,
//            playerSprite.getWidth() / 2f,
//            playerSprite.getHeight() / 2f
//        );

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

        if (initialPause){
            promptSprite.draw(spriteBatch);
        }

        spriteBatch.end();


        createScoreUi();
    }

    private void createScoreUi() {
        uiBatch.begin();
        String score = "Score: " + scoreThisRound;
        float x = 15;
        float y = Gdx.graphics.getHeight() - 15;
        float offset = 4f; // Outline thickness

        // Draw black text in all 4 directions
        font.setColor(Color.BLACK);
        font.draw(uiBatch, score, x + offset, y);
        font.draw(uiBatch, score, x - offset, y);
        font.draw(uiBatch, score, x, y + offset);
        font.draw(uiBatch, score, x, y - offset);

        // Draw white text on top
        font.setColor(Color.WHITE);
        font.draw(uiBatch, score, x, y);

        uiBatch.end();
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
