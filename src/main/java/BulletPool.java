import static com.raylib.Raylib.*;

import com.raylib.Raylib.Color;
import com.raylib.Raylib.Texture;

public class BulletPool {
    private Bullet[] bullets;
    private int index = 0;

    public BulletPool(int maxBullets, Texture texture, int size, float speed, Color color) {
        bullets = new Bullet[maxBullets];
        for (int i = 0; i < maxBullets; i++) {
            bullets[i] = new Bullet(0, 0, size, 0, speed, texture, color);
            bullets[i].setAlive(false); // start inactive
        }
    }

    public void shoot(float x, float y, float angle) {
        bullets[index].spawn(x, y, angle);

        index++;
        if (index >= bullets.length) index = 0; // circular buffer
    }

    public void updateAndDraw() {
        for (Bullet b : bullets) {
            if (b.isAlive()) {
                b.update();
                b.draw();
            }
        }
    }
}
