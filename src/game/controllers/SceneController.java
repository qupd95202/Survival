package game.controllers;

import game.scene.Scene;
import game.utils.CommandSolver;

import java.awt.*;

public class SceneController {

    private static SceneController instance;

    private SceneController() {
        currentImageController = new ImageResourceController();
        lastImageController = new ImageResourceController();
    }

    public static SceneController getInstance() {
        if (instance == null) {
            instance = new SceneController();
        }
        return instance;
    }

    private Scene currentScene;
    private Scene lastScene;

    private ImageResourceController currentImageController;
    private ImageResourceController lastImageController;

    private CommandSolver.MouseCommandListener mouseListener;
    private CommandSolver.KeyListener keyListener;

    public void change(Scene scene) {
        lastScene = currentScene;
        scene.sceneBegin();
        mouseListener = scene.mouseListener();
        keyListener = scene.keyListener();
        currentScene = scene;

        ImageResourceController temp = currentImageController;
        currentImageController = lastImageController;
        lastImageController = temp;
    }

    public void paint(Graphics g) {
        currentScene.paint(g);
    }

    public void update() {
        if (lastScene != null) {
            lastScene.sceneEnd();
            lastScene = null;
            lastImageController.clear();
        }
        if (currentScene != null) {
            currentScene.update();
        }
    }

    public CommandSolver.MouseCommandListener mouseListener() {
        return mouseListener;
    }

    public CommandSolver.KeyListener keyListener() {
        return keyListener;
    }

    public ImageResourceController imageController() {
        return currentImageController;
    }
}
