import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newRectangle;

public class Sprite {
    // Coordinates in the world 
    protected float worldX;
    protected float worldY;

    // Coordinates of center
    protected float centerX;
    protected float centerY;

    // Angle the sprite is facing
    protected float angle;

    // Sprite speed
    protected float speed;

    // Sprite dimensions
    protected float width;
    protected float height;

    // Sprite texture
    protected Texture texture;

    // Sprite colour
    protected Color color;

    // Sprite health
    protected float health;
    protected float maxHealth;

    // Stats
    protected float damage;

    // Alive status
    protected boolean alive;


    public Sprite(float worldX, float worldY, float width, float height, float angle, float speed, Texture texture, Color color) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.speed = speed;
        this.texture = texture;
        this.color = color;
    }

    public void draw() {
        centerX = worldX + width / 2f;
        centerY = worldY + height / 2f;
        Rectangle source = newRectangle(0, 0, width, height);
        Rectangle dest = newRectangle(centerX, centerY, width, height);
        Vector2 origin = new Vector2().x(width / 2f).y(height / 2f);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public boolean isAlive() {
        return alive;
    }
}
    
