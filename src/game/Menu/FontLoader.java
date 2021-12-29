package game.Menu;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

public class FontLoader {
    private static HashMap<String, Font> map = new HashMap();

    public FontLoader() {
    }

    public static Font loadFont(String fontFileName, float fontSize) {
        String key = fontFileName + fontSize;
        if (map.containsKey(key)) {
            return (Font)map.get(key);
        } else {
            try {
                File file = new File(fontFileName);
                FileInputStream aixing = new FileInputStream(file);
                Font dynamicFont = Font.createFont(0, aixing);
                Font dynamicFontPt = dynamicFont.deriveFont(fontSize);
                aixing.close();
                map.put(key, dynamicFontPt);
                return dynamicFontPt;
            } catch (Exception var7) {
                var7.printStackTrace();
                return new Font("宋体", 0, 14);
            }
        }
    }

    public static Font Blocks(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/Blocks.ttf", size);
        return font;
    }

    public static Font Future(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/Future.ttf", size);
        return font;
    }

    public static Font Future_Narrow(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/FutureNarrow.ttf", size);
        return font;
    }

    public static Font High(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/High.ttf", size);
        return font;
    }

    public static Font High_Square(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/HighSquare.ttf", size);
        return font;
    }

    public static Font Mini(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/Mini.ttf", size);
        return font;
    }

    public static Font Mini_Square(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/MiniSquare.ttf", size);
        return font;
    }
    public static Font Pixel(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/Pixel.ttf", size);
        return font;
    }
    public static Font Pixel_Square(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/PixelSquare.ttf", size);
        return font;
    }
    public static Font Rocket(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/RocketSquare.ttf", size);
        return font;
    }
    public static Font dotChinese(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/dotchinese.ttf", size);
        return font;
    }

    public static Font cuteChinese(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/cutechinese.ttf", size);
        return font;
    }
    public static Font englishWeird(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/IrishGrover-Regular.ttf", size);
        return font;
    }
    public static Font englishNormal(float size) {
        String root = System.getProperty("user.dir");
        Font font = loadFont(root + "/src/resources/Fonts/JosefinSans-Regular.ttf", size);
        return font;
    }
}
