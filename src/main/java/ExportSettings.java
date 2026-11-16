import javafx.scene.paint.Color;

public class ExportSettings {
    private Color transparentColor;
    private boolean transparent;
    private int scale;

    public ExportSettings(Color tc, boolean shouldErase, int scaleBy) {
        transparentColor = tc;
        transparent = shouldErase;
        scale = scaleBy;
    }

    public Color getTransparentValue() {
        return transparentColor;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public int scaleByValue() {
        return scale;
    }

    public String toString() {
        String str = "Color: " + transparentColor + "\n";
        str += "Should Erase: " + transparent + "\n";
        str += "Scale Value: " + scale + "x\n";

        return str;
    }
}
