import static com.raylib.Raylib.*;

public class Bullet extends Sprite {
    // Lifetime in frames
    private float lifeTime = 2f;
    // Bullet alive status
    private boolean alive = true;

    public Bullet(float worldX, float worldY, int size, float angle, float speed, Texture texture, Color color) {
        super(worldX, worldY, size, angle, speed, texture, color);
        alive = false;
    }

    public void spawn(float worldX, float worldY, float angle) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.angle = angle;
        alive = true;
        lifeTime = 2f;
    }

    public void update() {
        // Move bullet in the direction of its angle
        worldX += Math.cos(angle) * speed * GameScreen.dt;
        worldY += Math.sin(angle) * speed * GameScreen.dt;

        // Reduce lifetime
        lifeTime -= GameScreen.dt;
        if (lifeTime <= 0f) alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}