import static com.raylib.Raylib.*;

import com.raylib.Raylib.Image;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.Texture;
import com.raylib.Raylib.Vector2;

import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class MenuScreen extends Screen {

    public static final int defaultScreenW = 1280;
    public static final int defaultScreenH = 720;

    public static int screenW;
    public static int screenH;

    public int playX, playY, creditsX, creditsY;

    public Texture background, playBtn, playBtnHover, creditsBtn, creditsBtnHover;

    public Rectangle backgroundRect, playRect, creditsRect;

    public boolean showCredits = false;

    public MenuScreen() {

        SetConfigFlags(FLAG_WINDOW_RESIZABLE);
        InitWindow(defaultScreenW, defaultScreenH, "TanXX");
        SetWindowMinSize(1280, 720);
        SetTargetFPS(60);

        Image icon = LoadImage("resources/assets/menu/XX.png");
        SetWindowIcon(icon);
        UnloadImage(icon);
        
        // Load Textures
        background = LoadTexture("resources/assets/menu/mainMenu.png");
        playBtn = LoadTexture("resources/assets/menu/playButton.png");
        playBtnHover = LoadTexture("resources/assets/menu/playButtonHover.png");
        creditsBtn = LoadTexture("resources/assets/menu/creditsButton.png");
        creditsBtnHover = LoadTexture("resources/assets/menu/creditsButtonHover.png");

        updateLayout();

        while (!WindowShouldClose()) {

            if (IsWindowResized()) updateLayout();

            // Get mouse position
            Vector2 mouse = GetMousePosition();

            BeginDrawing();
            ClearBackground(RAYWHITE);

            drawScaled(background, backgroundRect);

            // Play button
            if (!showCredits) {
                 drawButton(playBtn, playBtnHover, playRect, mouse, () -> {
                    unload();
                    CloseWindow();
                    new GameScreen();
                });

                drawButton(creditsBtn, creditsBtnHover, creditsRect, mouse, () -> {
                    showCredits = true;
                });
            }

            if (showCredits) {
                drawCredits();
                if (IsKeyPressed(KEY_SPACE)) showCredits = false;
            }

            if (IsKeyPressed(KEY_SPACE)) {
                    showCredits = false;
            }

            EndDrawing();
        }

        unload();
        CloseWindow();
    }

    void updateLayout() {
        screenW = GetScreenWidth();
        screenH = GetScreenHeight();

        backgroundRect = newRectangle(0, 0, screenW, screenH);

        float playW = 600f * screenW / defaultScreenW;
        float playH = 240f * screenH / defaultScreenH;
        playRect = newRectangle(screenW / 2f - playW / 2f, screenH / 2f, playW, playH);

        float credW = 200f * screenW / defaultScreenW;
        float credH = 80f * screenH / defaultScreenH;

        creditsRect = newRectangle( 10f * screenW / defaultScreenW, screenH - credH - 10f * screenH / defaultScreenH, credW, credH);
    }

    public void drawScaled(Texture tex, Rectangle dest) {
        Rectangle source = newRectangle(0, 0, tex.width(), tex.height());
        DrawTexturePro(tex, source, dest, new Vector2().x(0).y(0), 0f, WHITE);
    }

    void drawButton(Texture normal, Texture hover, Rectangle rect, Vector2 mouse, Runnable onClick) {
        boolean over = CheckCollisionPointRec(mouse, rect);
        drawScaled(over ? hover : normal, rect);

        if (over && IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) onClick.run();
    }

    void drawCredits() {
        DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 180));

        int boxW = 400;
        int boxH = 200;
        int boxX = (screenW - boxW) / 2;
        int boxY = (screenH - boxH) / 2;

        DrawRectangleRounded(newRectangle(boxX, boxY, boxW, boxH), 0.2f, 10, RAYWHITE);
        DrawRectangleRoundedLines(newRectangle(boxX, boxY, boxW, boxH), 0.2f, 10, DARKGRAY);

        DrawText("Credits", boxX + boxW / 2 - MeasureText("Credits", 30) / 2, boxY + 20, 30, BLACK);
        DrawText("Made by:", boxX + 40, boxY + 60, 20, BLACK);
        DrawText("Jonathan Yu", boxX + 40, boxY + 95, 20, BLACK);
        DrawText("Cheney Chen", boxX + 40, boxY + 130, 20, BLACK);
        DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 14) / 2,  boxY + 165, 14, BLACK);
    }

    void unload() {
        UnloadTexture(background);
        UnloadTexture(playBtn);
        UnloadTexture(playBtnHover);
        UnloadTexture(creditsBtn);
        UnloadTexture(creditsBtnHover);
    }
}