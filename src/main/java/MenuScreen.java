import static com.raylib.Raylib.*;

import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class MenuScreen extends Screen {

    public int playX, playY, creditsX, creditsY;
    public Texture background, playBtn, playBtnHover, creditsBtn, creditsBtnHover;
    public Rectangle playRect, creditsRect;

    public boolean showCredits = false;

    public MenuScreen() {

        InitWindow(Main.screenW, Main.screenH, "TanXX");
        SetTargetFPS(60);

        // Load Textures
        loadTextures();

        while (!WindowShouldClose()) {
            Vector2 mouse = GetMousePosition();

            BeginDrawing();
            ClearBackground(RAYWHITE);
            DrawTexture(background, 0, 0, WHITE);
            DrawTexture(playBtn, playX, playY, WHITE);
            DrawTexture(creditsBtn, creditsX, creditsY, WHITE);

            // Play button
            if (!showCredits) {
                if (CheckCollisionPointRec(mouse, playRect)) {
                    DrawTexture(playBtnHover, playX, playY, WHITE);
                    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                        UnloadTexture(background);
                        UnloadTexture(playBtn);
                        UnloadTexture(creditsBtn);
                        CloseWindow();
                        new GameScreen();
                    }
                } else {
                    DrawTexture(playBtn, playX, playY, WHITE);
                }

                // Credits button
                if (CheckCollisionPointRec(mouse, creditsRect)) {
                    DrawTexture(creditsBtnHover, creditsX, creditsY, WHITE);
                    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                        showCredits = true;
                    }
                } else {
                    DrawTexture(creditsBtn, creditsX, creditsY, WHITE);
                }
            }

            if (showCredits) {
                // Dark overlay
                DrawRectangle(0, 0, Main.screenW, Main.screenH, newColor(0, 0, 0, 180));
        
                // Popup box
                int boxW = 400;
                int boxH = 200;
                int boxX = (Main.screenW - boxW) / 2;
                int boxY = (Main.screenH - boxH) / 2;

                DrawRectangleRounded(newRectangle(boxX, boxY, boxW, boxH), 0.2f, 10, RAYWHITE);

                DrawRectangleRoundedLines(newRectangle(boxX, boxY, boxW, boxH), 0.2f, 10, DARKGRAY);

                // Text
                DrawText("Credits", boxX + 140, boxY + 25, 30, BLACK);
                DrawText("Made by:", boxX + 40, boxY + 80, 20, DARKGRAY);
                DrawText("Jonathan Yu", boxX + 40, boxY + 110, 20, BLACK);
                DrawText("Cheney Chen", boxX + 40, boxY + 140, 20, BLACK);
                DrawText("Press space to close", boxX + 80, boxY + 170, 14, GRAY);
                
                
            }

            if (IsKeyPressed(KEY_SPACE)) {
                    showCredits = false;
            }

            EndDrawing();
        }

        UnloadTexture(background);
        UnloadTexture(playBtn);
        UnloadTexture(creditsBtn);
        CloseWindow();
    }

    public void loadTextures() {
        // Load textures
        background = resizeImage(LoadImage("resources/assets/menu/mainMenu.png"), Main.screenW, Main.screenH);

        // Texture logo = resizeImage(LoadImage("resources/assets/menu/logo.png"), 626, 250);
        
        playBtn = resizeImage(LoadImage("resources/assets/menu/playButton.png"), 600, 240);
        playBtnHover = resizeImage(LoadImage("resources/assets/menu/playButtonHover.png"), 600, 240);
        playX = Main.screenW / 2 - playBtn.width() / 2;
        playY = Main.screenH / 2;

        creditsBtn = resizeImage(LoadImage("resources/assets/menu/creditsButton.png"), 200, 80);
        creditsBtnHover = resizeImage(LoadImage("resources/assets/menu/creditsButtonHover.png"), 200, 80);
        creditsX = 10;
        creditsY = Main.screenH - creditsBtn.height() - 10;

        playRect = newRectangle(playX, playY, playBtn.width(), playBtn.height());
        creditsRect = newRectangle(creditsX, creditsY, creditsBtn.width(), creditsBtn.height());
    }
}