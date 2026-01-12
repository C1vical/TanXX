import com.raylib.Raylib.*;

public class Shape extends Sprite {
    private float rotationSpeed;
    private double orbitAngle;
    private float orbitAngleSpeed;
    private float orbitRadius;
    private float orbitX;
    private float orbitY;

    private boolean alive = true;

    public Shape(float orbitX, float orbitY, int size, float angle, float speed, Texture texture, Color color) {
        super(0, 0, size, angle, speed, texture, color);
        
        this.orbitX = orbitX;
        this.orbitY = orbitY;

        // Random speed between 0.0001 and 0.0002, random sign
        orbitAngleSpeed = (float) (Math.random() * 0.000 + 0.001f) * (Math.random() < 0.5 ? 1 : -1);
        rotationSpeed = (float) (Math.random() * 0.001 + 0.001f) * (Math.random() < 0.5 ? 1 : -1);

        // Random orbit radius between 30 and 100
        orbitRadius = 30 + (float)(Math.random() * 70);
        
        worldX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        worldY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        alive = false;
    }

    public void spawn(float orbitX, float orbitY, float angle) {
        this.orbitX = orbitX;
        this.orbitY = orbitY;
        orbitAngle = angle;
        worldX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius) - size / 2;
        worldY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius) - size / 2;
        alive = true;
    }

    public void update() {
        // // Update orbit angle
        orbitAngle += orbitAngleSpeed;
        // Update rotation angle
        angle += rotationSpeed;
        // // Calculate new world position based on circular orbit
        worldX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius) - size / 2;
        worldY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius) - size / 2;
    }

    public void drawHitBox() {
        
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
