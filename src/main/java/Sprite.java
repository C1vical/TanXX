import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newRectangle;

abstract class Sprite {
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
    protected int size;

    // Sprite texture
    protected Texture texture;

    protected Color color;

    // Sprite health
    protected double health;
    protected double maxHealth;


    public Sprite(float worldX, float worldY, int size, float angle, float speed, Texture texture, Color color) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.size = size;
        this.angle = angle;
        this.speed = speed;
        this.texture = texture;
        this.color = color;
    }

    abstract public void spawn(float worldX, float worldY, float angle);
    abstract public void update();

    public void draw() {
        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;
        Rectangle source = newRectangle(0, 0, size, size);
        Rectangle dest = newRectangle(centerX, centerY, size, size);
        Vector2 origin = new Vector2().x(size / 2f).y(size / 2f);

        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);

        if (GameScreen.hitbox) {
            drawHitBox();
        }
    }

    abstract void drawHitBox();
    

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }


}
    
