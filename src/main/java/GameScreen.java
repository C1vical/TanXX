import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class GameScreen extends GameState {

    // World dimensions
    public static final int worldW = 2000;
    public static final int worldH = 2000;
    public static final int borderSize = 5000;

    // Grid dimensions
    public final int tileSize = 20;
    public final int rows = worldH / tileSize;
    public final int cols = worldW / tileSize;

    // World colours
    public final Color worldGridColour = newColor(65, 65, 65, 255);
    public final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public final Color borderGridColour = newColor(34, 34, 34, 255);
    public final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Colours
    public final Color tankColor = newColor(252, 186, 3, 255);
    public final Color barrelColor = newColor(100, 99, 107, 255);
    public final Color bulletColor = newColor(252, 186, 3, 255);
    public final Color squareColor = newColor(214, 208, 30, 255);
    public final Color triangleColor = newColor(214, 51, 30, 255);
    public final Color pentagonColor = newColor(82, 58, 222, 255);

    // Tank
    public Texture tank;
    public static Tank playerTank;
    public static final int tankSize = 100;
    public static float angle;
    public static final int tankSpeed = 300; // pixels per second

    // Barrel
    public Texture barrel;
    public static final int barrelW = tankSize;
    public static final int barrelH = tankSize / 2;

    // Bullets
    public Texture bullet;
    public static BulletPool bulletPool;
    public static float reloadSpeed = 0.3f; // default 0.8f
    public static float reloadTimer = 0f;
    public static int bulletSize = barrelH; // default barrelH
    public static int bulletSpeed = 400; // pixels per second

    // Shapes
    public Texture square, triangle, pentagon;
    public static ShapePool shapePool;
    public static int shapeSize = 150;

    // Settings icon
    public Texture settings;
    public static int settingsSize = 50;
    public Rectangle settingsRect;
    public boolean settingsHover = false;
    public static boolean showSettings = false;
    
    // Camera
    public static Camera2D camera = new Camera2D();

    // Camera zoom level
    public static float zoomLevel = 1.0f;

    // Frame time
    public static float dt;
    public int fps;
    public int boxW = 125, boxH = 50, boxX = 10, boxY = 10;
    public Rectangle rect =  newRectangle(boxX, boxY, boxW, boxH);

    // Booleans
    public static boolean hitbox = false;

    // Lerp values
    public final float movementLerp = 0.25f;
    public final float zoomLerp = 0.1f;

    ScreenType requestedScreen = ScreenType.GAME;

    public GameScreen() {
        // Load Textures
        tank = resizeImage(LoadImage("resources/game/tank.png"), tankSize, tankSize);
        barrel = resizeImage(LoadImage("resources/game/barrel.png"), barrelW, barrelH);
        bullet = resizeImage(LoadImage("resources/game/bullet.png"), bulletSize, bulletSize);
        square = resizeImage(LoadImage("resources/game/square.png"), shapeSize, shapeSize);
        triangle = resizeImage(LoadImage("resources/game/triangle.png"), shapeSize, shapeSize);
        pentagon = resizeImage(LoadImage("resources/game/pentagon.png"), shapeSize, shapeSize);
        settings = resizeImage(LoadImage("resources/game/settings.png"), settingsSize, settingsSize);

        Texture[] shapeTextures = {square, triangle, pentagon};
        Color[] shapeColours = {squareColor, triangleColor, pentagonColor};

        // Set player tank
        playerTank = new Tank(worldW / 2 - tankSize / 2, worldH / 2 - tankSize / 2, tankSize, angle, tankSpeed, tank, tankColor);

        // Set camera
        camera = new Camera2D();
        camera.target(new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()));
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        camera.zoom(zoomLevel);
        
        bulletPool = new BulletPool(500, bullet, bulletSize, bulletSpeed, bulletColor);
        shapePool = new ShapePool(500, shapeTextures, shapeSize, 0, shapeColours);

        updateLayout();
    }

    @Override
    public void update() {

        if (IsWindowResized()) updateLayout();

        // Get frame time
        dt = GetFrameTime();

        // Camera
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));

        // Smooth lerp
        Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY());
        camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
        camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

        if (!showSettings) {
             // Zoom
            getZoomLevel();
            float desiredZoom = zoomLevel;
            camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);

            // Input
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

            shapePool.update();
            playerTank.update();
            bulletPool.update();
        }
       
        Vector2 mouse1 = GetMousePosition();
        if (isHover(settingsRect, mouse1)) {
            settingsHover = true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                showSettings = true;
            }
        }

        if (IsKeyPressed(KEY_SPACE)) {
            showSettings = false;
        }
    }

    @Override
    public void draw() {
        ClearBackground(borderGridColour);

        BeginMode2D(camera);
        
        drawWorld();

        // Shapes
        shapePool.draw();

        // Bullets
        bulletPool.draw();

        // Barrel
        Rectangle source = newRectangle(0, 0, barrelW, barrelH);
        Rectangle dest = newRectangle(playerTank.getCenterX(), playerTank.getCenterY(), barrelW, barrelH);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrel, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor); // Barrel

        // Tank
        playerTank.setAngle(angle);
        playerTank.draw(); 
        EndMode2D();

        drawButton(settings, settingsRect, settingsHover, showSettings);
        
        if (showSettings) {
            DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 180));
            int boxW = 400, boxH = 200, boxX = (screenW - boxW) / 2, boxY = (screenH - boxH) / 2;

            Rectangle rect = newRectangle(boxX, boxY, boxW, boxH);

            DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
            DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);

            DrawText("Settings", boxX + boxW / 2 - MeasureText("Settings", 30) / 2, boxY + 20, 30, BLACK);
            // DrawText("Made by:", boxX + 40, boxY + 60, 20, BLACK);
            // DrawText("Jonathan Yu", boxX + 40, boxY + 95, 20, BLACK);
            // DrawText("Cheney Chen", boxX + 40, boxY + 130, 20, BLACK);
            // DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 14) / 2,  boxY + 165, 14, BLACK);
        }

        fps = GetFPS();
        
        DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
        DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);
        DrawText(fps + " FPS", boxX + boxW / 2 - MeasureText(fps + " FPS", 25) / 2, boxY + boxH / 2 - 25/2, 25, BLACK);
    }
    

    @Override
    public void unload() {
        UnloadTexture(tank);
        UnloadTexture(barrel);
        UnloadTexture(bullet);
        UnloadTexture(square);
        UnloadTexture(triangle);
        UnloadTexture(pentagon);
    }

    @Override
    public ScreenType getRequestedScreen() {
        return requestedScreen;
    }

    public void updateLayout() {
        float ratioW = screenW / (float) defaultScreenW;
        float ratioH = screenH / (float) defaultScreenH;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(screenW - settingsW - 10 * screenW / defaultScreenW, 10 * ratioH, settingsW, settingsH);
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

    public Texture resizeImage(Image img, int newWidth, int newHeight) {
        ImageResize(img, newWidth, newHeight);
        Texture tex = LoadTextureFromImage(img);
        UnloadImage(img);
        return tex;
    }
}
