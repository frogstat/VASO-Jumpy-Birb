package my.spel;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public enum ScreenTypes{
        MAIN_MENU,
        PREFERENCES,
        GAMEPLAY,
        GAME_OVER
    }

    MenuScreen menuScreen;
    GameplayScreen gameplayScreen;
    GameOverScreen gameOverScreen;
    PreferencesScreen preferencesScreen;

    public static ScreenTypes previousScreen;

    @Override
    public void create() {
        changeScreen(ScreenTypes.MAIN_MENU);
    }

    public void changeScreen(ScreenTypes screenType){
        switch (screenType){
            case MAIN_MENU -> {
                if(menuScreen == null){
                    menuScreen = new MenuScreen(this);
                }
                setScreen(menuScreen);
            }
            case GAMEPLAY -> {
                if(gameplayScreen == null){
                    gameplayScreen = new GameplayScreen(this);
                }
                setScreen(gameplayScreen);
            }
            case GAME_OVER -> {
                if(gameOverScreen == null){
                    gameOverScreen = new GameOverScreen(this);
                }
                setScreen(gameOverScreen);
            }
            case PREFERENCES -> {
                if(preferencesScreen == null){
                    preferencesScreen = new PreferencesScreen(this);
                }
                setScreen(preferencesScreen);
            }
        }
    }

    public void newGame(){
        gameplayScreen = new GameplayScreen(this);
        changeScreen(ScreenTypes.GAMEPLAY);
    }

    public void continueGame(){
        changeScreen(ScreenTypes.GAMEPLAY);
    }

    public void showPreferencesScreen() {
        changeScreen(ScreenTypes.PREFERENCES);
    }







}
