package my.spel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Ball {

    private Sprite sprite;
    private float speedY;
    private float speedX;
    private boolean goesRight;


    public Ball(float x, float y, boolean goesRight) {
        this.goesRight = goesRight;
        Texture texture = new Texture("game_assets/main_menu/ball.png");
        sprite = new Sprite(texture);
        float aspectRatio = sprite.getHeight() / sprite.getWidth();
        sprite.setSize(150, 150 * aspectRatio);
        speedX = 600;
        speedY = 0;
        sprite.setPosition(x, y);
        sprite.setOriginCenter();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void moveX(float delta) {
        float directionModifier = goesRight ? 1 : -1;
        sprite.translateX(speedX * directionModifier * delta);
    }

    public void applyGravity(float delta, float gravityConstant) {
        speedY -= gravityConstant * delta;
        sprite.translateY(speedY * delta);


        if (sprite.getY() < 0) {
            sprite.setY(0);
        }

        if (sprite.getY() == 0) {
            speedY *= -1;
            speedY *= 0.8f;
        }
    }


}
