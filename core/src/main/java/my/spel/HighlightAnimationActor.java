package my.spel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HighlightAnimationActor extends Actor {
    private final Animation<Texture> animation;
    private float elapsedTime;
    private final float pauseTimeSeconds;
    private float timeWhenAnimationPaused;
    private boolean animationPaused;
    private final float x;
    private final float y;

    public HighlightAnimationActor(Animation<Texture> animation, float pauseTimeSeconds, float x, float y) {
        this.animation = animation;
        this.pauseTimeSeconds = pauseTimeSeconds;
        animationPaused = false;
        timeWhenAnimationPaused = 0;
        elapsedTime = 0;
        this.x = x;
        this.y = y;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        elapsedTime += delta;

        if (animationPaused && elapsedTime - timeWhenAnimationPaused > pauseTimeSeconds) {
            elapsedTime = 0;
            animationPaused = false;
        }
    }

    // Draw at every frame
    @Override
    public void draw(Batch batch, float parentAlpha) {

        // if animation not paused, show animation
        if (!animationPaused) {
            final Texture texture = animation.getKeyFrame(elapsedTime);
            batch.draw(texture, x, y, texture.getWidth(), texture.getHeight());
            if (animation.isAnimationFinished(elapsedTime)) {
                timeWhenAnimationPaused = elapsedTime;
                animationPaused = true;
            }
        }
    }
}
