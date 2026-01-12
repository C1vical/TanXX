import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Tank extends Sprite {
    // Recoil
    float recoilX = 0f;
    float recoilY = 0f;

    float decay = 5f;

    public Tank(float worldX, float worldY, int width, int height, float angle, float speed, Texture texture, Color color) {
        super(worldX, worldY, width, height, angle, speed, texture, color);
        centerX = worldX + width / 2f;
        centerY = worldY + height / 2f;
    }

    public void update() {
        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT))  moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;
        

        if (moveX != 0 && moveY != 0) {
            moveX /= (float) Math.sqrt(2);
            moveY /= (float) Math.sqrt(2);
        }

        worldX += moveX * speed * GameScreen.dt;
        worldY -= moveY * speed * GameScreen.dt;

        worldX += recoilX * GameScreen.dt;
        worldY -= recoilY * GameScreen.dt;

        recoilX -= recoilX * decay * GameScreen.dt;
        recoilY -= recoilY * decay * GameScreen.dt;

        if (worldX < -width / 2f) worldX = -width / 2f;
        if (worldX > GameScreen.worldW - width / 2f) worldX = GameScreen.worldW - width / 2f;
        if (worldY < -height / 2f)  worldY = -height / 2f;
        if (worldY > GameScreen.worldH - height / 2f) worldY = GameScreen.worldH - height / 2f;

        centerX = worldX + width / 2f;
        centerY = worldY + height / 2f;
    }

    public void draw() {
        centerX = worldX + width / 2f;
        centerY = worldY + height / 2f;
        DrawCircleV(new Vector2().x(centerX).y(centerY), width / 2 + 5, newColor(55, 55, 55, 255));
        DrawCircleV(new Vector2().x(centerX).y(centerY), width / 2, color);
    }

    public void applyRecoil() {
        recoilX = -GameScreen.recoil * (float) Math.cos(angle);
        recoilY = GameScreen.recoil * (float) Math.sin(angle);
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), width / 2f, GREEN);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
