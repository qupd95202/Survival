package game.Teach;

import game.gameObj.players.Player;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;

public class TeachPlayer extends Player {
    private int width;
    private int height;
    public TeachPlayer(int x, int y, ImgArrAndType imgArrAndType, RoleState roleState, int width, int height) {
        super(x, y, imgArrAndType, roleState);
        this.width=width;
        this.height=height;
    }
    @Override
    public void keepInMap() {
        if (collider().bottom()>height || touchLeft() || collider().right()>width || touchTop()) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }
    public Animation getCurrentAnimation(){
        return currentAnimation;
    }
}
