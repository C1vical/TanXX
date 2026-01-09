import static com.raylib.Raylib.*;

public class Screen {
    public static Texture resizeImage(Image img, int newWidth, int newHeight) {
        ImageResize(img, newWidth, newHeight);
        Texture tex = LoadTextureFromImage(img);
        UnloadImage(img);
        return tex;
    }
}
