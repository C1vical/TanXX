import static com.raylib.Raylib.*;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Color;
import com.raylib.Raylib.Image;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.Texture;
import com.raylib.Raylib.Vector2;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class GameScreen extends Screen {

    // World dimensions
    public static final int worldW = 2000;
    public static final int worldH = 2000;
    public static final int borderSize = 5000;

    // Grid dimensions
    public static final int tileSize = 20;
    public static final int rows = worldH / tileSize;
    public static final int cols = worldW / tileSize;

    // World colours
    public static final Color worldGridColour = newColor(65, 65, 65, 255);
    public static final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public static final Color borderGridColour = newColor(34, 34, 34, 255);
    public static final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Textures
    public Texture tank, barrel, bullet, square, triangle, pentagon;

    // Colours
    public static final Color tankColor = newColor(32, 199, 129, 255);
    public static final Color barrelColor = newColor(100, 99, 107, 255);
    public static final Color bulletColor = newColor(32, 199, 129, 255);
    public static final Color squareColor = newColor(214, 208, 30, 255);
    public static final Color triangleColor = newColor(214, 51, 30, 255);
    public static final Color pentagonColor = newColor(82, 58, 222, 255);

    // Tank
    public static Tank playerTank;
    public static final int tankSize = 100;
    public static float angle;
    public static final int tankSpeed = 250; // pixels per second

    // Barrel
    public static final int barrelW = tankSize;
    public static final int barrelH = tankSize / 2;

    // Bullets
    public static BulletPool bulletPool;
    public static float reloadSpeed = 0.8f; // default 0.8f
    public static float reloadTimer = 0f;
    public static int bulletSize = barrelH; // default barrelH
    public static int bulletSpeed = 275; // pixels per second

    // Shapes
    public static ShapePool shapePool;
    public static int shapeSize = 80;
    public static float shapeSpeed = 0.003f; // pixels per second
    
    // Camera
    public static Camera2D camera = new Camera2D();

    // Camera zoom level
    public static float zoomLevel = 1.0f;

    // Frame time
    public static float dt;

    // Booleans
    public static boolean hitbox = false;

    // Lerp values
    public static final float movementLerp = 0.25f;
    public static final float zoomLerp = 0.1f;

    public GameScreen() {

        SetConfigFlags(FLAG_WINDOW_RESIZABLE);
        InitWindow(1280, 720, "TanXX");
        SetWindowMinSize(1280, 720);
        SetTargetFPS(60);

        Image icon = LoadImage("resources/assets/menu/XX.png");
        SetWindowIcon(icon);
        UnloadImage(icon);

        // Load Textures
        tank = resizeImage(LoadImage("resources/assets/game/tank.png"), tankSize, tankSize);
        barrel = resizeImage(LoadImage("resources/assets/game/barrel.png"), barrelW, barrelH);
        bullet = resizeImage(LoadImage("resources/assets/game/bullet.png"), bulletSize, bulletSize);
        square = resizeImage(LoadImage("resources/assets/shapes/square.png"), shapeSize, shapeSize);
        triangle = resizeImage(LoadImage("resources/assets/shapes/triangle.png"), shapeSize, shapeSize);
        pentagon = resizeImage(LoadImage("resources/assets/shapes/pentagon.png"), shapeSize, shapeSize);
        Texture[] shapeTextures = {square, triangle, pentagon};
        Color[] shapeColours = {squareColor, triangleColor, pentagonColor};

        playerTank = new Tank(worldW / 2 - tankSize / 2, worldH / 2 - tankSize / 2, tankSize, angle, tankSpeed, tank, tankColor);

        // Set camera
        camera = new Camera2D();
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        camera.rotation(0);
        camera.zoom(zoomLevel);
        camera.target(new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()));

        bulletPool = new BulletPool(256, bullet, bulletSize, bulletSpeed, bulletColor);
        shapePool = new ShapePool(256, shapeTextures, shapeSize, shapeSpeed, shapeColours);

        boolean firstFrame = true;

        while (!WindowShouldClose()) {
            dt = GetFrameTime();

            BeginDrawing();
            
            ClearBackground(borderGridColour);
            BeginMode2D(camera);
            
            drawWorld();
            
            camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));

            // Smooth lerp
            Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY());
            camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
            camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

            
            
            getZoomLevel();
            float desiredZoom = zoomLevel;
            camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);
    
            Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), camera);
            angle = (float) Math.atan2(mouse.y() - playerTank.getCenterY(), mouse.x() - playerTank.getCenterX());
            
            if (IsKeyPressed(KEY_B)) {
                hitbox = !hitbox;
            }
            
            if (reloadTimer > 0f) {
                reloadTimer -= dt;
            }
            
            if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && reloadTimer <= 0f) {
                float bulletX = playerTank.getCenterX() + (float) Math.cos(angle) * (barrelW + bulletSize / 2) - bulletSize / 2;
                float bulletY = playerTank.getCenterY() + (float) Math.sin(angle) * (barrelW + bulletSize / 2) - bulletSize / 2;
                bulletPool.shoot(bulletX, bulletY, angle);
                reloadTimer = reloadSpeed;
            }

            if (IsKeyPressed(KEY_Q)) {
                float orbitX = (float) (Math.random() * GameScreen.worldW);
                float orbitY = (float) (Math.random() * GameScreen.worldH);
                shapePool.create(orbitX, orbitY, (float) (Math.random() * Math.PI * 2));
            }

            // Draw in order

            shapePool.updateAndDraw(); // Shapes
            playerTank.update();
            Rectangle source = newRectangle(0, 0, barrelW, barrelH);
            Rectangle dest = newRectangle(playerTank.getCenterX(), playerTank.getCenterY(), barrelW, barrelH);
            Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
            DrawTexturePro(barrel, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor); // Barrel
            
            playerTank.setAngle(angle);
            playerTank.draw(); // Tank

           
            bulletPool.updateAndDraw(); // Bullets

            EndMode2D();
            EndDrawing();
        }

        UnloadTexture(tank);
        UnloadTexture(barrel);
        UnloadTexture(bullet);
        UnloadTexture(square);
        UnloadTexture(triangle);
        UnloadTexture(pentagon);
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
        if (zoomLevel > 4.0f) zoomLevel = 4.0f;
    }
}
