package game.controllers;

import game.core.Global;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ImageResourceController {
    private static class KeyPair {
        private String path;
        private Image img;

        public KeyPair(String path, Image img) {
            this.path = path;
            this.img = img;
        }
    }

    private ArrayList<KeyPair> content;

    public ImageResourceController() {
        content = new ArrayList<>();
    }

    public Image tryGetImage(String path) {
        KeyPair pair = findKeyPair(path);
        if (pair == null) {
            return addImage(path);
        }
        return pair.img;
    }

    private KeyPair findKeyPair(String path) {
        for (int i = 0; i < content.size(); i++) {
            KeyPair pair = content.get(i);
            if (pair.path.equals(path)) {
                return pair;
            }
        }
        return null;
    }

    private Image addImage(String path) {
        try {
            if (Global.IS_DEBUG) {
                System.out.println("load img from:" + path);
            }
            Image img = ImageIO.read(getClass().getResource(path));
            content.add(new KeyPair(path, img));
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        content.clear();
    }
}
