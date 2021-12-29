package game.Menu;

import game.controllers.SceneController;
import game.core.Global;
import game.gameObj.GameObject;
import game.graphic.Animation;
import game.utils.Path;

import java.awt.*;

/**製造menu裡的Button*/
public class Button extends GameObject {
    private Animation animation;
    private Label label;

    public Button(int x,int y,int width,int height){
        super(x,y,width,height);

    }
    public Button(int x, int y, int width, int height, Animation animation){
        super(x,y,width,height);
        this.animation=animation;

    }
    public Button(int x, int y, int width, int height, Label label){
        super(x,y,width,height);
        this.label=label;

    }
    public Button(Label label,int width,int height){
        super(label.collider().left(),label.collider().top()-height,width,height);
        this.label=label;
    }
    public Button(Label label,Animation animation,int width,int height){
        super(label.collider().left(),label.collider().top()-height,width,height);
        this.label=label;
    }
    @Override
    public void paintComponent(Graphics g) {
        if(animation !=null){
            animation.paint(painter().left(),painter().top(),painter().width(), painter().height(),g);
        }
        if(label !=null){
            label.paint(g);
        }

    }

    @Override
    public void update() {
        if(animation !=null) {
            animation.update();
        }
    }

    public Animation getAnimation() {
        return animation;
    }
    public void setAnimation(Animation animation) {
        this.animation=animation;
    }
}
