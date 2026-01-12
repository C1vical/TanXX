import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newRectangle;

public class Shape extends Sprite {

    public enum Type {
        SQUARE,
        TRIANGLE,
        PENTAGON
    }

    private Type shapeType;

    private float rotationSpeed;
    private double orbitAngle;
    private float orbitAngleSpeed;
    private float orbitRadius;
    private float orbitX;
    private float orbitY;

    private boolean alive = true;

    public Shape(float orbitX, float orbitY, float width, float height, float angle, float speed, Texture texture, Color color, Type shapeType) {
        super(0, 0, width, height, angle, speed, texture, color);
        
        this.orbitX = orbitX;
        this.orbitY = orbitY;

        // Random speed between 0.08 and 0.1, random sign (for cw and ccw rotation)
        orbitAngleSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        rotationSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);

        // Random orbit radius between 30 and 100
        orbitRadius = 30 + (float)(Math.random() * 70);
        
        worldX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        worldY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        alive = false;

        this.shapeType = shapeType;
    }

    public void update() {
        // // Update orbit angle
        orbitAngle += orbitAngleSpeed * GameScreen.dt;
        // Update rotation angle
        angle += rotationSpeed * GameScreen.dt;;
        // // Calculate new world position based on circular orbit
        worldX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius) - width / 2f;
        worldY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius) - height / 2f;
    }

//    public void draw() {
//        switch (shapeType) {
//            case SQUARE -> {
//                Draw
//                DrawPolyLines(new Vector2().x(centerX).y(centerY), 4, (float) (width * Math.sqrt(2) / 2), angle * (180f / (float) Math.PI) + 45, GREEN);
//            }
//            case TRIANGLE -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 3, (float) (width / Math.sqrt(3)), angle * 180f / (float) Math.PI, GREEN);
//            case PENTAGON -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 5, height / 2f, angle * 180f / (float) Math.PI, GREEN);
//        }
//    }

    public void drawHitBox()  {
        switch (shapeType) {
            case SQUARE -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 4, (float) (width * Math.sqrt(2) / 2), angle * (180f / (float) Math.PI) + 45, GREEN);
//            case TRIANGLE -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 3, (float) (width / Math.sqrt(3)), angle * 180f / (float) Math.PI, GREEN);
//            case PENTAGON -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 5, height / 2f, angle * 180f / (float) Math.PI, GREEN);
//            case TRIANGLE -> DrawPolyLines(new Vector2().x(centerX - 10f * (float) Math.cos(angle)).y(centerY - 10f * (float) Math.sin(angle)), 3, (float) (width / Math.sqrt(3)), angle * 180f / (float) Math.PI, GREEN);
//            case PENTAGON -> DrawPolyLines(new Vector2().x(centerX - 7f * (float) Math.cos(angle)).y(centerY - 7f * (float) Math.sin(angle)), 5, height / 2f, angle * 180f / (float) Math.PI, GREEN);
//            case SQUARE ->DrawPolyLines(new Vector2().x(centerX).y(centerY), 4, (float) (width * Math.sqrt(2) / 2), angle * (180f / (float) Math.PI) + 45, GREEN);
            case TRIANGLE -> DrawCircleLinesV(new Vector2().x(centerX).y(centerY), GameScreen.squareWidth / 2f, GREEN);
            case PENTAGON -> DrawCircleLinesV(new Vector2().x(centerX).y(centerY), GameScreen.squareWidth / 2f, GREEN);
        }
    }


    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
