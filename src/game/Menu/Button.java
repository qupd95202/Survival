package game.Menu;

import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.utils.Path;

import java.awt.*;

/**製造menu裡的Button*/
public class Button extends GameObject {
    private Image img;

    public Button(int x,int y,int width,int height){
        super(x,y,width,height);
        img= SceneController.getInstance().imageController().tryGetImage(new Path().img().menu().Button().button());
    }
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img,painter().left(),painter().top(),painter().width(), painter().height(),null);
    }

    @Override
    public void update() {

    }
}
