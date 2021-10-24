package game.Teach;

import game.gameObj.players.ComputerPlayer;
import game.graphic.ImgArrAndType;

public class TeachComputerPlayer extends ComputerPlayer {
    private int width;
    private int height;

    public TeachComputerPlayer(int x, int y, ImgArrAndType imgArrAndType, RoleState roleState, int width, int height) {
        super(x, y, imgArrAndType, roleState,0,"");
        this.width=width;
        this.height=height;
    }
    @Override
    public void keepInMap() {
        if (collider().bottom()>height || touchLeft() || collider().right()>width || touchTop()) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }
    @Override
    public void update() {
        if(roleState==RoleState.HUNTER){
            translate(0,5);
        }
    }
}
