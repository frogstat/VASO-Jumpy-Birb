package my.spel;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * {@link ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    public static String skinPath = "game_assets/skin/glassy-ui.json";

    public enum ScreenTypes {
        MAIN_MENU,
        DIFFICULTY_SELECTOR,
        GAMEPLAY,
        GAME_OVER,
        HIGH_SCORE
    }

    public Music music;

    MenuScreen menuScreen;
    GameplayScreen gameplayScreen;
    DifficultySelectorScreen difficultySelectorScreen;


    public static ScreenTypes previousScreen;

    public static Preferences prefs;

    public static int[] easyHighScores = new int[3];
    public static int[] mediumHighScores = new int[3];
    public static int[] hardHighScores = new int[3];

    @Override
    public void create() {
        prefs = Gdx.app.getPreferences("VASO_jumpyBird/gamedata.xml");
        parseHighScores();
        music = Gdx.audio.newMusic(Gdx.files.internal("game_assets/menu_music.mp3"));
        playMusic();
        changeScreen(ScreenTypes.MAIN_MENU);
    }

    private static void parseHighScores() {
        for(GameplayScreen.Difficulty difficulty : GameplayScreen.Difficulty.values()){
            int[] highScoreCurrentDifficulty = switch (difficulty){
                case EASY -> easyHighScores;
                case MEDIUM -> mediumHighScores;
                case HARD -> hardHighScores;
            };

            for (int i = 0; i < highScoreCurrentDifficulty.length; i++) {
                highScoreCurrentDifficulty[i] = prefs.getInteger("highscore_" + difficulty.toString() + "_" + (i + 1),0);
            }

        }
    }

    public void changeScreen(ScreenTypes screenType) {
        switch (screenType) {
            case MAIN_MENU -> {
                if (menuScreen == null) {
                    menuScreen = new MenuScreen(this);
                }
                setScreen(menuScreen);
            }
            case GAMEPLAY -> {
                if (gameplayScreen == null) {
                    gameplayScreen = new GameplayScreen(this);
                }
                setScreen(gameplayScreen);
            }
            case DIFFICULTY_SELECTOR -> {
                difficultySelectorScreen = new DifficultySelectorScreen(this);
                setScreen(difficultySelectorScreen);
            }
        }
    }

    public void newGame(boolean newMusic) {
        gameplayScreen = new GameplayScreen(this);
        if (newMusic){
            music.stop();
            music = Gdx.audio.newMusic(Gdx.files.internal("game_assets/" + gameplayScreen.theme + "/music.mp3"));
            playMusic();
        }
        changeScreen(ScreenTypes.GAMEPLAY);
    }

    public void continueGame() {
        changeScreen(ScreenTypes.GAMEPLAY);
    }

    public void goToMenu() {
        if (previousScreen.equals(ScreenTypes.GAMEPLAY)) {
            stopMusic();
            music = Gdx.audio.newMusic(Gdx.files.internal("game_assets/menu_music.mp3"));
            playMusic();
        }
        menuScreen = new MenuScreen(this);
        changeScreen(ScreenTypes.MAIN_MENU);
    }

    public void goToGameOver(int score, GameplayScreen.Difficulty difficulty, boolean isNewHighScore, int scoreThisRound, boolean skipGameOver) {
        setScreen(new GameOverScreen(this, score, difficulty, isNewHighScore, scoreThisRound, skipGameOver));
    }

    public void goToCredits(){
        setScreen(new CreditsScreen(this));
    }

    public void playMusic() {
        music.setLooping(true);
        music.play();
    }

    public void stopMusic() {
        music.stop();
    }

    public void showHighScore() {
        setScreen(new HighScoreScreen(this));
    }


    public void playSound(Sound sound) {
        sound.play();
    }

    public void stopSound(Sound sound) {
        sound.stop();
    }


}
