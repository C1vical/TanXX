import static com.raylib.Raylib.*;

import java.util.ArrayList;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Color;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.Texture;
import com.raylib.Raylib.Vector2;

import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class GameScreen extends Screen {

    public static final int worldW = 1000;
    public static final int worldH = 1000;

    public static final int tileSize = 20;
    public static final int rows = worldH / tileSize;
    public static final int cols = worldW / tileSize;

    public static final Color worldGridColour = newColor(65, 65, 65, 255);
    public static final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public static final Color borderGridColour = newColor(34, 34, 34, 255);
    public static final Color borderGridLineColour = newColor(45, 45, 45, 255);

    public static final int borderSize = 1000;

    public static float zoomLevel = 1.0f;

    public Texture tank, barrel, bullet;

    // Tank
    public static Tank playerTank;
    public static final int tankSize = 75;
    public static float tankX = worldW / 2 - tankSize / 2;
    public static float tankY = worldH / 2- tankSize / 2;
    public static float tankCenterX;
    public static float tankCenterY;
    public static float angle;

    public static final int tankSpeed = 250; // pixels per second

    public static final int barrelW = tankSize;
    public static final int barrelH = tankSize / 2;

    public static Camera2D camera = new Camera2D();

    public static float dt;

    public static boolean mouseDown = false, hitbox = false;

    // Bullets
    public static ArrayList<Bullet> bullets = new ArrayList<>();
    public static float reloadSpeed = 0.01f;
    public static float reloadTimer = 0f;
    public static int bulletSize = tankSize / 2 - 10;
    public static int bulletSpeed = 200; // pixels per second
    

    public GameScreen() {
        InitWindow(Main.screenW, Main.screenH, "TanXX");
        SetTargetFPS(60);
        camera = new Camera2D();
        camera.offset(new Vector2().x(Main.screenW / 2f).y(Main.screenH / 2f));
        camera.rotation(0);

        // Load Textures
        loadTextures();

        BulletPool bulletPool = new BulletPool(256, bullet, bulletSize, bulletSpeed, RED);

        while (!WindowShouldClose()) {
            
            dt = GetFrameTime();

            BeginDrawing();
            
            ClearBackground(borderGridColour);
            BeginMode2D(camera);
        
            drawWorld();
            getInput();

            tankCenterX = tankX + tankSize / 2;
            tankCenterY = tankY + tankSize / 2;

            camera.target(new Vector2().x(tankCenterX).y(tankCenterY));

            getZoomLevel();
            camera.zoom(zoomLevel);
            
            Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), camera);
            angle = (float) Math.atan2(mouse.y() - tankCenterY, mouse.x() - tankCenterX);
            Rectangle source = newRectangle(0, 0, barrelW, barrelH);
            Rectangle dest = newRectangle(tankCenterX, tankCenterY, barrelW, barrelH);
            Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
            DrawTexturePro(barrel, source, dest, origin, angle * (180f / (float) Math.PI), GRAY);

            playerTank = new Tank(tankX, tankY, tankSize, angle, tankSpeed, tank, RED);
            playerTank.draw();

            // DrawTexturePro(tank, newRectangle(0, 0, tankSize, tankSize), newRectangle(tankCenterX, tankCenterY, tankSize, tankSize), new Vector2().x(tankSize / 2).y(tankSize / 2), angle * (180f / (float) Math.PI), RED);
            
            if (IsKeyPressed(KEY_B)) {
                hitbox = !hitbox;
            }
            
            if (reloadTimer > 0f) {
                reloadTimer -= dt;
            }
            
            if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && reloadTimer <= 0f) {
                float bulletX = tankCenterX + (float) Math.cos(angle) * (barrelW + bulletSize / 2) - bulletSize / 2;
                float bulletY = tankCenterY + (float) Math.sin(angle) * (barrelW + bulletSize / 2) - bulletSize / 2;
                bulletPool.shoot(bulletX, bulletY, angle);
                reloadTimer = reloadSpeed;
            }

            bulletPool.updateAndDraw();

            // for (Bullet bullet : bullets) {
            //     if (bullet.isAlive()) {
            //         bullet.update();
            //         bullet.draw();
            //     } else {
            //     bullets.remove(bullet);
            //         break;
            //     }
            // }

            EndMode2D();
            EndDrawing();
        }

        UnloadTexture(tank);
        UnloadTexture(barrel);
        UnloadTexture(bullet);
        CloseWindow();

    }

    public void drawWorld() {
        for (int x = - borderSize; x <= worldW + borderSize; x += tileSize) {
            DrawLine(x, -borderSize, x, worldH + borderSize, borderGridLineColour);
        }

        for (int y = - borderSize; y <= worldH + borderSize; y += tileSize) {
            DrawLine(-borderSize, y, worldW + borderSize, y, borderGridLineColour);
        }

        DrawRectangle(0, 0, worldW, worldH, worldGridColour);

        for (int x = 0; x <= worldW; x += tileSize) {
            DrawLine(x, 0, x, worldH, worldGridLineColour);
        }

        for (int y = 0; y <= worldH; y += tileSize) {
            DrawLine(0, y, worldW, y, worldGridLineColour);
        }
    }

    public void getZoomLevel() {
        float scroll = GetMouseWheelMove();
        if (scroll > 0) {
            zoomLevel += 0.1f;
        } else if (scroll < 0) {
            zoomLevel -= 0.1f;
        }

        if (zoomLevel < 0.5f) zoomLevel = 0.5f;
        if (zoomLevel > 2.0f) zoomLevel = 2.0f;
    }

    public void getInput() {
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

        tankX += moveX * tankSpeed * dt;
        tankY -= moveY * tankSpeed * dt;

        if (tankX < -tankSize / 2) tankX = -tankSize / 2;
        if (tankX > worldW - tankSize / 2) tankX = worldW - tankSize / 2;
        if (tankY < -tankSize / 2)  tankY = -tankSize / 2;
        if (tankY > worldH - tankSize / 2) tankY = worldH - tankSize / 2;
    }

    public void loadTextures() {
        // Load textures
        tank = resizeImage(LoadImage("resources/assets/game/tank.png"), tankSize, tankSize);
        barrel = resizeImage(LoadImage("resources/assets/game/barrel.png"), barrelW, barrelH);
        bullet = resizeImage(LoadImage("resources/assets/game/bullet.png"), bulletSize, bulletSize);
    }
}
