import static com.raylib.Raylib.*;

public class Collision {
    public static boolean circleCollision(float x1, float y1, float r1, float x2, float y2, float r2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float distanceSq = dx*dx + dy*dy;
        float radiusSum = r1 + r2;
        return distanceSq <= radiusSum * radiusSum;
    }
}
