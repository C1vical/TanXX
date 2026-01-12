import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Bullet extends Sprite {
    // Lifetime in frames
    private float lifeTime;

    public Bullet(float worldX, float worldY, int size, float angle, float speed, Texture texture, Color color) {
        super(worldX, worldY, size, angle, speed, texture, color);
        lifeTime = 3f;
        alive = true;
    }

    public void update() {
        // Move bullet in the direction of its angle
        worldX += Math.cos(angle) * speed * GameScreen.dt;
        worldY += Math.sin(angle) * speed * GameScreen.dt;

        // Reduce lifetime
        lifeTime -= GameScreen.dt;
        if (lifeTime <= 0f) alive = false;
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2, YELLOW);
    }
}