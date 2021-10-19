package game.graphic;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ImageSet {
    private Map<String, Image> animationSheets;

    public ImageSet() {
        this.animationSheets = new HashMap<>();
    }

    public void addSheet(String name, Image animationSheet) {
        animationSheets.put(name, animationSheet);
    }

    public Image get(String name) {
        return animationSheets.get(name);
    }
}
