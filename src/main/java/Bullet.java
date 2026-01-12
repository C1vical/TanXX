import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Bullet extends Sprite {
    // Lifetime in frames
    private float lifeTime;

    public Bullet(float worldX, float worldY, int width, int height, float angle, float speed, Texture texture, Color color) {
        super(worldX, worldY, width, height, angle, speed, texture, color);
        lifeTime = 3f;
        alive = true;
    }

    public void update() {
        // Move bullet in the direction of its angle
        worldX += (float) (Math.cos(angle) * speed * GameScreen.dt);
        worldY += (float) (Math.sin(angle) * speed * GameScreen.dt);

        centerX = worldX + width / 2f;
        centerY = worldY + height / 2f;

        // Reduce lifetime
        lifeTime -= GameScreen.dt;
        if (lifeTime <= 0f) alive = false;
    }

    public void draw() {
        DrawCircleV(new Vector2().x(centerX).y(centerY), width / 2 + 5, newColor(55, 55, 55, 255));
        DrawCircleV(new Vector2().x(centerX).y(centerY), width / 2, color);
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), width / 2f, GREEN);
    }
}