import static com.raylib.Raylib.*;

public class ShapePool {
    private Shape[] shapes;
    private int index = 0;

    public ShapePool(int maxShapes, Texture[] textures, int size, float speed, Color[] colors) {
        shapes = new Shape[maxShapes];
        for (int i = 0; i < maxShapes; i++) {
            int j = (int) (Math.random() * 3);
            shapes[i] = new Shape(0, 0, size, 0, speed, textures[j], colors[j]);
           
        }
    }

    public void create(float x, float y, float angle) {
        shapes[index].spawn(x, y, angle);
        index++;
        if (index >= shapes.length) index = 0; // circular buffer
    }

    public void updateAndDraw() {
        for (Shape s : shapes) {
            if (s.isAlive()) {
                s.update();
                s.draw();
            }
        }
    }
}
