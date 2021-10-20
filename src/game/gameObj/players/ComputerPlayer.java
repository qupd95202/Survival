package game.gameObj.players;

import game.core.Global;

import game.gameObj.GameObject;

import game.gameObj.Props;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;
import game.utils.Delay;

import java.awt.*;
import java.util.ArrayList;


public class ComputerPlayer extends Player {

    private boolean isChase;
    private boolean isRun;

    private boolean isStopByWall;
    private boolean canPassWall;
    private Delay notStopDelay;
    private Delay stopDelay;

    private Player chasedPlayer;
    private Props chasedProps;
    private boolean isChaseProps;

    private float nearest;
    private float propsNearest;
    private Delay randomMoveDelay;
    private int iniMoveOnX;
    private int iniMoveOnY;

    public ComputerPlayer(int x, int y, ImgArrAndType imageArrayList, RoleState roleState) {
        super(x, y, imageArrayList, roleState);
        transformTime.play();
        transformTime.loop();
        isChase = false;
        isRun = false;
        canPassWall = false;
        isChaseProps = false;
        nearest = Global.NEAREST;
        propsNearest = Global.NEAREST;
        pointDelay = new Delay(60);
        notStopDelay = new Delay(300);
        notStopDelay.play();
        notStopDelay.loop();
        stopDelay = new Delay(120);
        super.movement.setSpeed(Global.COMPUTER_SPEED);
        randomMoveDelay = new Delay(180);
        randomMoveDelay.play();
        randomMoveDelay.loop();
        iniMoveOnX = Global.random(-2, 1);
        iniMoveOnY = Global.random(-2, 1);
    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {

    }


    @Override
    public void update() {
        if (roleState == RoleState.HUNTER) {
            hunterUpdate();
        } else {
            preyUpdate();
        }
        nonStopUpdate();
        transformResetUpdate();
        chaseProps();
        addPoint();
        currentAnimation.update();
    }

//    public boolean isChase() {
//        return isChase;
//    }
//
//    public void setChase(boolean chase) {
//        isChase = chase;
//    }

    public void preyUpdate() {
        if (transformTime.count()) {
            if (Global.getProbability(40)) {
                transform();
            }
        }
        if (randomMoveDelay.count()) {
            iniMoveOnX = Global.random(-2, 1);
            iniMoveOnY = Global.random(-2, 1);
            if (Global.getProbability(80)) {
                iniMoveOnX = 0;
                iniMoveOnY = 0;
            }
            if (isRun) {
                if (Global.getProbability(50)) {
                    iniMoveOnX = 1;
                } else {
                    iniMoveOnX = -1;
                }
                if (Global.getProbability(50)) {
                    iniMoveOnY = 1;
                } else {
                    iniMoveOnY = -1;
                }
            }
        }
        cpMove();
    }

    public void hunterUpdate() {
        if (isChase) {
            chase();
        } else {
            if (randomMoveDelay.count()) {
                iniMoveOnX = Global.random(-2, 1);
                iniMoveOnY = Global.random(-2, 1);
            }
            cpMove();
        }
    }

    public void cpMove(double moveOnX, double moveOnY) {
        movement.move(moveOnX, moveOnY);
        if (moveOnX == 0 && moveOnY == 0) {
            movingState = MovingState.STAND;
        } else {
            movingState = MovingState.WALK;
        }
        super.move();
    }

    public void cpMove() {
        cpMove(iniMoveOnX, iniMoveOnY);
    }

    private void chase() {
        float x = Math.abs(chasedPlayer.collider().centerX() - painter().centerX());
        float y = Math.abs(chasedPlayer.collider().bottom() - painter().centerY() - 10);
        if (x <= 20 && y <= 20) {
            return;
        }
        float distance = (float) Math.sqrt(x * x + y * y);//計算斜邊,怪物與人物的距離
        double moveOnX = (Math.cos(Math.toRadians((Math.acos(x / distance) / Math.PI * 180))) * this.movement.getSpeed()); //  正負向量
        double moveOnY = (Math.sin(Math.toRadians((Math.asin(y / distance) / Math.PI * 180))) * this.movement.getSpeed());
        if (chasedPlayer.collider().bottom() < painter().centerY()) {
            moveOnY = -moveOnY;
        }
        if (chasedPlayer.collider().centerX() < painter().centerX()) {
            moveOnX = -moveOnX;
        }
        cpMove(moveOnX, moveOnY);
    }

    public void chaseProps() {
        if (chasedProps == null || isChase) {
            isChaseProps = false;
            return;
        }
        if (isChaseProps) {
            float x = Math.abs(chasedProps.collider().centerX() - painter().centerX());
            float y = Math.abs(chasedProps.collider().bottom() - painter().centerY() - 10);
            float distance = (float) Math.sqrt(x * x + y * y);//計算斜邊,怪物與人物的距離
            double moveOnX = (Math.cos(Math.toRadians((Math.acos(x / distance) / Math.PI * 180))) * this.movement.getSpeed()); //  正負向量
            double moveOnY = (Math.sin(Math.toRadians((Math.asin(y / distance) / Math.PI * 180))) * this.movement.getSpeed());
            if (chasedProps.collider().bottom() < painter().centerY()) {
                moveOnY = -moveOnY;
            }
            if (chasedProps.collider().centerX() < painter().centerX()) {
                moveOnX = -moveOnX;
            }
            cpMove(moveOnX, moveOnY);
        }
    }


    public void whichPropIsNear(Props props) {
        float dx = Math.abs(props.collider().centerX() - painter().centerX());
        float dy = Math.abs(props.collider().bottom() - painter().centerY() - 10);
        float dc = (float) Math.sqrt(dx * dx + dy * dy);//計算斜邊,怪物與人物的距離

        if (dc < propsNearest) {
            propsNearest = dc;
            chasedProps = props;
        }
        if (propsNearest < Global.WINDOW_HEIGHT / 2) {
            isChaseProps = true;
        }
        if (chasedProps != null && isChaseProps) {
            float chaseDx = Math.abs(chasedProps.collider().centerX() - painter().centerX());
            float chaseDy = Math.abs(chasedProps.collider().bottom() - painter().centerY() - 10);
            float chaseDc = (float) Math.sqrt(chaseDx * chaseDx + chaseDy * chaseDy);//計算斜邊,怪物與人物的距離
            if (chaseDc >= Global.WINDOW_WIDTH) {
                isChaseProps = false;
            }
        }
    }

    public void whoIsNear(Player player) {
        float dx = Math.abs(player.collider().centerX() - painter().centerX());
        float dy = Math.abs(player.collider().bottom() - painter().centerY() - 10);
        float dc = (float) Math.sqrt(dx * dx + dy * dy);//計算斜邊,怪物與人物的距離
        if (roleState == RoleState.HUNTER) {
            if (dc < nearest) {
                nearest = dc;
                this.chasedPlayer = player;
            }
            if (nearest < Global.WINDOW_WIDTH / 3) { //一定距離內會偵測且有移動或屬性突兀
                if (player.movingState == Player.MovingState.WALK) {
                    isChase = true;
                }
            }

            if (nearest < Global.WINDOW_WIDTH / 2) { //玩家圖片屬性與當前地區物件屬性不同
                if (this.chasedPlayer != null) {
                    if (chasedPlayer.getPositionType() != chasedPlayer.getTransformationAnimationType()) {
                        isChase = true;
                    }
                }
            }
            if (isChase) {
                float chaseDx = Math.abs(chasedPlayer.collider().centerX() - painter().centerX());
                float chaseDy = Math.abs(chasedPlayer.collider().bottom() - painter().centerY() - 10);
                float chaseDc = (float) Math.sqrt(chaseDx * chaseDx + chaseDy * chaseDy);//計算斜邊,怪物與人物的距離
                if (chaseDc >= Global.WINDOW_WIDTH / 2) {
                    isChase = false;
                    nearest = Global.NEAREST;
                }
            }
        } else {
            if (dc < Global.WINDOW_WIDTH / 2) {
                isRun = true;
            } else {
                isRun = false;
            }
            isChase = false;
            if (nearest < Global.NEAREST) {
                nearest = Global.NEAREST;
            }
        }

    }

    @Override
    public void transform() {
        storedTransformAnimation = new Animation(AllImages.getRandomImgArrAndType());
        super.transform();
    }

    @Override
    public boolean isCollisionForMovement(GameObject gameObject) {
        if (collider().bottom() + movement.getVector2D().getY() < gameObject.collider().top()) {
            isStopByWall = false;
            return false;
        }
        if (collider().top() + movement.getVector2D().getY() > gameObject.collider().bottom()) {
            isStopByWall = false;
            return false;
        }
        if (collider().right() + movement.getVector2D().getX() < gameObject.collider().left()) {
            isStopByWall = false;
            return false;
        }
        if (collider().left() + movement.getVector2D().getX() > gameObject.collider().right()) {
            isStopByWall = false;
            return false;
        }
        isStopByWall = true;
        return true;
    }

    @Override
    public void notMove() {
        if (!canPassWall) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }

    public void nonStopUpdate() {
        if (notStopDelay.count()) {
            canPassWall = true;
            stopDelay.play();
        }

        if (stopDelay.count()) {
            canPassWall = false;
        }
    }

}
