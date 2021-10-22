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
    public enum Mode {
        SINGLE_POINT_GAME,
        SINGLE_SURVIVAL_GAME;
    }

    private Mode mode;

    private boolean isChase;
    private boolean isRun;

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

    private int speed;
    private int chaseDistance;
    private int propsChaseDistance;
    private int giveUpDistance;

    //道具相關
    public Delay propsStopTimeDelay;
    //可不可穿牆

    public ComputerPlayer(int x, int y, ImgArrAndType imageArrayList, RoleState roleState) {
        super(x, y, imageArrayList, roleState);
        this.mode = mode;
        speed = Global.COMPUTER_SPEED2;
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE2;
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
        propsStopTimeDelay = new Delay(180);
        super.movement.setSpeed(speed);
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
        propsEffectUpdate();
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
            if (Global.getProbability(80)) {
                transform();
            }
        }
        if (randomMoveDelay.count()) {
            iniMoveOnX = Global.random(-2, 1);
            iniMoveOnY = Global.random(-2, 1);
            if (Global.getProbability(50)) {
                iniMoveOnX = 0;
                iniMoveOnY = 0;
            }
            if (isRun) {
                if (Global.getProbability(50)) {
                    translate(10, 10);
                }
                if (Global.getProbability(50)) {
                    translate(-10, -10);
                }
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
        if (chasedPlayer == null) {
            return;
        }
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
        if (isChaseProps && !chasedProps.isGotByPlayer()) {
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
        if (props.isGotByPlayer()) {
            return;
        }
        float dx = Math.abs(props.collider().centerX() - painter().centerX());
        float dy = Math.abs(props.collider().bottom() - painter().centerY() - 10);
        float dc = (float) Math.sqrt(dx * dx + dy * dy);//計算斜邊,怪物與人物的距離
        if (dc < propsNearest) {
            propsNearest = dc;
            chasedProps = props;
        }
        if (propsNearest < propsChaseDistance) {
            if (!chasedProps.isGotByPlayer()) {
                isChaseProps = true;
            }
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

    public void whoIsNearInSurvivalGame(Player player) {
        float dx = Math.abs(player.collider().centerX() - painter().centerX());
        float dy = Math.abs(player.collider().bottom() - painter().centerY() - 10);
        float dc = (float) Math.sqrt(dx * dx + dy * dy);//計算斜邊,怪物與人物的距離
        if (dc < nearest) {
            nearest = dc;
            this.chasedPlayer = player;
        }
        if (nearest < chaseDistance) { //一定距離內會偵測且有移動或屬性突兀
            if (player.movingState == Player.MovingState.WALK) {
                isChase = true;
            }
        }
        if (this.chasedPlayer != null) {
            if (chasedPlayer.getPositionType() == chasedPlayer.getTransformationAnimationType()) {
                isChase = false;
                return;
            }
        }
        if (chasedPlayer != null && isChase) {
            float chaseDx = Math.abs(chasedPlayer.collider().centerX() - painter().centerX());
            float chaseDy = Math.abs(chasedPlayer.collider().bottom() - painter().centerY() - 10);
            float chaseDc = (float) Math.sqrt(chaseDx * chaseDx + chaseDy * chaseDy);//計算斜邊,怪物與人物的距離
            if (chaseDc >= giveUpDistance) {
                isChase = false;
                nearest = Global.NEAREST;
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
            if (nearest < chaseDistance) { //一定距離內會偵測且有移動或屬性突兀
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
            if (chasedPlayer != null && isChase) {
                float chaseDx = Math.abs(chasedPlayer.collider().centerX() - painter().centerX());
                float chaseDy = Math.abs(chasedPlayer.collider().bottom() - painter().centerY() - 10);
                float chaseDc = (float) Math.sqrt(chaseDx * chaseDx + chaseDy * chaseDy);//計算斜邊,怪物與人物的距離
                if (chaseDc >= Global.COMPUTER_GIVE_UP_DISTANCE2) {
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


    public void nonStopUpdate() {
        if (notStopDelay.count()) {
            canPassWall = true;
            stopDelay.play();
        }

        if (stopDelay.count()) {
            canPassWall = false;
        }
    }

    @Override
    public void notMove() {
        if (!canPassWall) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }

    public void collidePropsInSurvivalMode(Props props) {
        switch (props.getPropsType()) {
            case trap:
                canMove = false;
                trapDelay.play();
                break;
        }
    }

    public void decreaseSpeed() {
        speed -= 2;
    }

    @Override
    public void propsEffectUpdate() {
        if (propsStopTimeDelay.count()) {
            canMove = true;
        }
    }

    public void AILevel1() {
        speed = Global.COMPUTER_SPEED1;
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE1;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE1;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE1;
        notStopDelay.stop();
    }

    public void AILevel2() {
        speed = Global.COMPUTER_SPEED2;
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE2;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE2;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE2;
        notStopDelay.play();
        notStopDelay.loop();
    }

    public void AILevel3() {
        speed = Global.COMPUTER_SPEED3;
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE3;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE3;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE3;
        notStopDelay.play();
        notStopDelay.loop();
    }

}
