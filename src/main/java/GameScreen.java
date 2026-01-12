import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class GameScreen extends GameState {

    // World dimensions
    public static final int worldW = 4000;
    public static final int worldH = 4000;
    public static final int borderSize = 4000;

    // Grid dimensions
    public final int tileSize = 20;

    // World colors
    public final Color worldGridColour = newColor(65, 65, 65, 255);
    public final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public final Color borderGridColour = newColor(34, 34, 34, 255);
    public final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Colors
    public final Color tankColor = newColor(252, 186, 3, 255);
    public final Color barrelColor = newColor(100, 99, 107, 255);
    public final Color bulletColor = newColor(252, 186, 3, 255);
    public final Color squareColor = newColor(214, 208, 30, 255);
    public final Color triangleColor = newColor(214, 51, 30, 255);
    public final Color pentagonColor = newColor(82, 58, 222, 255);

    // Tank
    public Texture tank;
    public static Tank playerTank;
    public static final int tankWidth = 125;
    public static final int tankHeight = 125;
    public static float angle;
    public static final int tankSpeed = 300; // pixels per second

    // Barrel
    public Texture barrel;
    public static final int barrelW = tankHeight;
    public static final int barrelH = tankHeight / 2;

    // Bullets
    public Texture bullet;
    public static float reloadSpeed = 0.3f; // default 0.8f
    public static float reloadTimer = 0f;
    public static int bulletWidth = barrelH; // default barrelH
    public static int bulletHeight = barrelH;
    public static int bulletSpeed = 400; // pixels per second
    List<Bullet> bullets = new ArrayList<>();

    // Shapes
    public Texture square, triangle, pentagon;

    public static float squareWidth = 80;
    public static float squareHeight = 80;

    public static float triangleWidth = (float) (squareWidth / 2f * Math.sqrt(3));
    public static float triangleHeight = squareHeight;

    public static float pentagonWidth = (float) (squareWidth / 2f * (Math.sqrt(5 + 2 * Math.sqrt(5))));
    public static float pentagonHeight = (float) (squareHeight / 2f * (1 + Math.sqrt(5)));

    List<Shape> shapes = new ArrayList<>();
    private static final int startShapes = 15;

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
    public static boolean hitbox = false, autoFire = false, autoSpin = false;

    // Lerp values
    public final float movementLerp = 0.25f;
    public final float zoomLerp = 0.1f;

    // Recoil speed
    public static final float recoil = 100;

    ScreenType requestedScreen = ScreenType.GAME;

    public GameScreen() {
        // Load Textures
        tank = resizeImage(LoadImage("resources/game/tank.png"), tankWidth, tankHeight);
        barrel = resizeImage(LoadImage("resources/game/barrel.png"), barrelW, barrelH);
        bullet = resizeImage(LoadImage("resources/game/bullet.png"), bulletWidth, bulletHeight);
        square = resizeImage(LoadImage("resources/game/square.png"), (int) squareWidth, (int) squareHeight);
        triangle = resizeImage(LoadImage("resources/game/triangle.png"), (int) triangleWidth, (int) triangleHeight);
        pentagon = resizeImage(LoadImage("resources/game/pentagon.png"), (int) pentagonWidth, (int) pentagonHeight);
        settings = resizeImage(LoadImage("resources/game/settings.png"), settingsSize, settingsSize);

        // Set player tank
        playerTank = new Tank(worldW / 2f - tankWidth / 2f, worldH / 2f - tankHeight / 2f, tankWidth, tankHeight, angle, tankSpeed, tank, tankColor);

        // Set camera
        camera = new Camera2D();
        camera.target(new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()));
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        camera.zoom(zoomLevel);

        spawnShapes(startShapes);

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
            if (!autoSpin) {
                angle = (float) Math.atan2(mouse.y() - playerTank.getCenterY(), mouse.x() - playerTank.getCenterX());
            } else {
                angle += 1f * dt;
            }
            
            if (IsKeyPressed(KEY_B)) {
                hitbox = !hitbox;
            }
            
            if (reloadTimer > 0f) {
                reloadTimer -= dt;
            }
            
            if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && reloadTimer <= 0f) {
                fireBullet();
                playerTank.applyRecoil();
            }

            if (IsKeyPressed(KEY_Q)) {
                addShape();
            }

            if (IsKeyPressed(KEY_E)) {
                autoFire = !autoFire;
            }

            if(autoFire && reloadTimer <= 0f) {
                fireBullet();
                playerTank.applyRecoil();
            }

            if (IsKeyPressed(KEY_C)) {
                autoSpin = !autoSpin;
            }

            if(shapes.size() < startShapes) {
                spawnShapes(startShapes - shapes.size());
            }

            for (Shape s : shapes) s.update();
            playerTank.update();

            for (Bullet b : bullets) b.update();

            // Bullet vs Shape collisions
            checkCollisions();
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

    private void fireBullet() {
        float bulletX = playerTank.getCenterX() + (float) Math.cos(angle) * (barrelW + bulletWidth / 2f) - bulletWidth / 2f;
        float bulletY = playerTank.getCenterY() + (float) Math.sin(angle) * (barrelW + bulletHeight / 2f) - bulletHeight / 2f;
        bullets.add(new Bullet(bulletX, bulletY, bulletWidth, bulletHeight, angle, bulletSpeed, bullet, bulletColor));
        reloadTimer = reloadSpeed;
    }

    @Override
    public void draw() {
        ClearBackground(borderGridColour);

        BeginMode2D(camera);
        
        drawWorld();

        // Shapes
        for (Shape s : shapes) {
            s.draw();
            if (hitbox) {
                s.drawHitBox();
            }
        }

        // Bullets
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            if (b.isAlive()) {
                b.draw();
                if (hitbox) {
                    b.drawHitBox();
                }
            } else {
                it.remove();
            }
        }

        // Barrel
        Rectangle source = newRectangle(0, 0, barrelW, barrelH);
        Rectangle dest = newRectangle(playerTank.getCenterX(), playerTank.getCenterY(), barrelW, barrelH);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrel, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor); // Barrel

        // Tank
        playerTank.setAngle(angle);
        playerTank.draw();
        if (hitbox) {
            playerTank.drawHitBox();
        }

        EndMode2D();

        drawButton(settings, settingsRect, settingsHover, showSettings);
        
        if (showSettings) {
            DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 180));
            int boxW = 400, boxH = 200, boxX = (screenW - boxW) / 2, boxY = (screenH - boxH) / 2;

            Rectangle rect = newRectangle(boxX, boxY, boxW, boxH);

            DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
            DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);

            DrawText("Settings", boxX + boxW / 2 - MeasureText("Settings", 30) / 2, boxY + 20, 30, BLACK);
            DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 14) / 2,  boxY + 165, 14, BLACK);
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
        screenW = GetScreenWidth();
        screenH = GetScreenHeight();

        float ratioW = screenW / (float) defaultScreenW;
        float ratioH = screenH / (float) defaultScreenH;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(screenW - settingsW - 10 * ratioW, 10 * ratioH, settingsW, settingsH);
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

    private void spawnShapes(int num) {
        for (int i = 0; i < num; i++) {
            addShape();
        }
    }

    private void addShape() {
        int type = (int) (Math.random() * 3);

        Texture tex = switch (type) {
            case 0 -> square;
            case 1 -> triangle;
            default -> pentagon;
        };

        Color col = switch (type) {
            case 0 -> squareColor;
            case 1 -> triangleColor;
            default -> pentagonColor;
        };

        float width = switch (type) {
            case 0 -> squareWidth;
            case 1 -> triangleWidth;
            default -> pentagonWidth;
        };

        float height = switch (type) {
            case 0 -> squareHeight;
            case 1 -> triangleHeight;
            default -> pentagonHeight;
        };

        Shape.Type shapeType;
        switch (type) {
            case 0 -> shapeType = Shape.Type.SQUARE;
            case 1 -> shapeType = Shape.Type.TRIANGLE;
            default -> shapeType = Shape.Type.PENTAGON;
        }

        float orbitX = (float) (Math.random() * GameScreen.worldW);
        float orbitY = (float) (Math.random() * GameScreen.worldH);

        shapes.add(new Shape(orbitX, orbitY, width, height, 0, 0, tex, col, shapeType));
    }

    public void checkCollisions() {
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();

            Iterator<Shape> shapeIt = shapes.iterator();
            while (shapeIt.hasNext()) {
                Shape s = shapeIt.next();

                if (CheckCollisionCircles(new Vector2().x(b.getCenterX()).y(b.getCenterY()),b.width / 2f, new Vector2().x(s.getCenterX()).y(s.getCenterY()), squareWidth / 2f)) {
                    bulletIt.remove();
                    shapeIt.remove();
                    break;
                }
            }
        }

        Iterator<Shape> shapeIt = shapes.iterator();
        while (shapeIt.hasNext()) {
            Shape s = shapeIt.next();
            if (CheckCollisionCircles(new Vector2().x(s.getCenterX()).y(s.getCenterY()),s.width / 2f, new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()), playerTank.width / 2f)) {
                shapeIt.remove();
                break;
            }
        }
    }
}
