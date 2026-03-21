package my.spel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    public enum ScreenTypes {
        MAIN_MENU,
        PREFERENCES,
        GAMEPLAY,
        GAME_OVER
    }

    public Music music;

    MenuScreen menuScreen;
    GameplayScreen gameplayScreen;
//    GameOverScreen gameOverScreen;
    PreferencesScreen preferencesScreen;

    public static ScreenTypes previousScreen;

    @Override
    public void create() {
        music = Gdx.audio.newMusic(Gdx.files.internal("menu_music.mp3"));
        music.setVolume(PreferencesScreen.musicVolume);
        playMusic();
        changeScreen(ScreenTypes.MAIN_MENU);
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
//            case GAME_OVER -> {
//                if (gameOverScreen == null) {
//                    gameOverScreen = new GameOverScreen(this);
//                }
//                setScreen(gameOverScreen);
//            }
            case PREFERENCES -> {
                if (preferencesScreen == null) {
                    preferencesScreen = new PreferencesScreen(this);
                }
                setScreen(preferencesScreen);
            }
        }
    }

    public void newGame() {
        gameplayScreen = new GameplayScreen(this);
        music.stop();
        music = Gdx.audio.newMusic(Gdx.files.internal(gameplayScreen.theme + "/music.mp3"));
        playMusic();
        changeScreen(ScreenTypes.GAMEPLAY);
    }

    public void continueGame() {
        changeScreen(ScreenTypes.GAMEPLAY);
    }

    public void showPreferencesScreen() {
        preferencesScreen = new PreferencesScreen(this);
        changeScreen(ScreenTypes.PREFERENCES);
    }

    public void goToMenu() {
        if (previousScreen.equals(ScreenTypes.GAMEPLAY)) {
            stopMusic();
            music = Gdx.audio.newMusic(Gdx.files.internal("menu_music.mp3"));
            playMusic();
        }
        menuScreen = new MenuScreen(this);
        changeScreen(ScreenTypes.MAIN_MENU);
    }

    public void goToGameOver(int score){
//        gameOverScreen = new GameOverScreen(this);
//        changeScreen(ScreenTypes.GAME_OVER);
        setScreen(new GameOverScreen(this, score));
    }

    public void playMusic() {
        music.setLooping(true);
        music.play();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSound(Sound sound){
        sound.play(PreferencesScreen.audioVolume);
    }


}
