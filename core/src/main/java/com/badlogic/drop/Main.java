package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

public class Main implements ApplicationListener {
    public final static int MENU = 0;
    public final static int OPTIONS = 1;
    public final static int GAMEPLAY = 2;
    public final static int GAMEOVER = 3;

    @Override
    public void create() {


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {


    }


    private void input(float delta) {

    }


    private void logic(float delta) {

    }

    private void draw() {

    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }

    public void changeScreen(int screen){
        switch(screen){
            case MENU -> System.out.println(MENU);
            case OPTIONS ->System.out.println(OPTIONS);
            case GAMEPLAY ->System.out.println(GAMEPLAY);
            case GAMEOVER ->System.out.println(GAMEOVER);
        }
    }
}
