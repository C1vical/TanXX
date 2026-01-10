import static com.raylib.Raylib.*;

public class Tank extends Sprite {
    public Tank(float worldX, float worldY, int size, float angle, float speed, Texture texture, Color color) {
        super(worldX, worldY, size, angle, speed, texture, color);
        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;
    }

    public void update() {
        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT))  moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;
        

        if (moveX != 0 && moveY != 0) {
            moveX /= Math.sqrt(2);
            moveY /= Math.sqrt(2);
        }

        worldX += moveX * speed * GameScreen.dt;
        worldY -= moveY * speed * GameScreen.dt;

        if (worldX < -size / 2) worldX = -size / 2;
        if (worldX > GameScreen.worldW - size / 2) worldX = GameScreen.worldW - size / 2;
        if (worldY < -size / 2)  worldY = -size / 2;
        if (worldY > GameScreen.worldH - size / 2) worldY = GameScreen.worldH - size / 2;

        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

}
