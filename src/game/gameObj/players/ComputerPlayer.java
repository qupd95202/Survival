package game.gameObj.players;

import game.core.Global;

import game.gameObj.GameObject;

import game.gameObj.Pact;
import game.gameObj.Props;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;
import game.network.Client.ClientClass;
import game.utils.Delay;

import java.awt.*;
import java.util.ArrayList;

import static game.gameObj.Pact.bale;


public class ComputerPlayer extends Player {
    public enum Mode {
        SINGLE_POINT_GAME,
        SINGLE_SURVIVAL_GAME;
    }

    private Mode mode;

    private boolean isChase;
    private boolean isRun;

    //可不可穿牆
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
    public Delay propsThunderDelay;

    //連線相關
    private int id;
    private boolean isConnect;

    public ComputerPlayer(int x, int y, ImgArrAndType imageArrayList, RoleState roleState, String num) {
        super(x, y, imageArrayList, roleState, " COM" + " " + num);
        mode = Mode.SINGLE_POINT_GAME;
        speed = Global.COMPUTER_SPEED1;
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
        propsThunderDelay = new Delay(300);
        super.movement.setSpeed(speed);
        randomMoveDelay = new Delay(180);
        randomMoveDelay.play();
        randomMoveDelay.loop();
        iniMoveOnX = Global.random(-2, 1);
        iniMoveOnY = Global.random(-2, 1);
    }

    public ComputerPlayer(int x, int y, ImgArrAndType imageArrayList, RoleState roleState, int id, String num) {
        super(x, y, imageArrayList, roleState, " COM" + " " + num);
        isConnect = true;
        speed = Global.COMPUTER_SPEED2;
        mode = Mode.SINGLE_POINT_GAME;
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
        propsThunderDelay = new Delay(300);
        super.movement.setSpeed(speed);
        randomMoveDelay = new Delay(180);
        randomMoveDelay.play();
        randomMoveDelay.loop();
        iniMoveOnX = Global.random(-2, 1);
        iniMoveOnY = Global.random(-2, 1);
        this.id = id;
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
        exchangeUpdate();
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
        if (isRun) {
            if (Global.getProbability(70)) {
                escape();
                return;
            }
        }
        if (randomMoveDelay.count()) {
            if (currentAnimation == originalAnimation) {
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
            } else {
                iniMoveOnX = Global.random(-2, 1);
                iniMoveOnY = Global.random(-2, 1);
                if (Global.getProbability(50)) {
                    iniMoveOnX = 0;
                    iniMoveOnY = 0;
                }
            }
        }
        cpMove();
    }

    public void hunterUpdate() {
        if (Global.getProbability(10) && mode == Mode.SINGLE_POINT_GAME) {
            outrage();
        }
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
        if (!canMove) {
            return;
        }
        //一般移動位置的部分
        translateInConnect(movement.getVector2D().getX(), movement.getVector2D().getY());
        keepInMap();
    }

    public void cpMove() {
        cpMove(iniMoveOnX, iniMoveOnY);
    }

    public void escape() {
        setSpeed(4);
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
        if (chasedPlayer.collider().bottom() > painter().centerY()) {
            moveOnY = -moveOnY;
        }
        if (chasedPlayer.collider().centerX() < painter().centerX()) {
            moveOnX = -moveOnX;
        }
        cpMove(moveOnX, moveOnY);
        setSpeed(Global.COMPUTER_SPEED2);
    }

    @Override
    public void exchangeUpdate() {
        if (canMoveDelay.count()) {
            roleStateExchange(bumpPlayer);
            canMove = true;
            bumpPlayer.canMove = true;
        }
        if (collisionDelay.count()) {
            animationUpdate();
            bumpPlayer.animationUpdate();
        }
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

    @Override
    public void keepInMap() {
        if (touchBottom() || touchLeft() || touchRight() || touchTop()) {
            translateInConnect(-movement.getVector2D().getX(), -movement.getVector2D().getY());
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
        if (chasedProps == null) {
            return;
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
            if (this.chasedPlayer == null) {
                return;
            }
            if (nearest < chaseDistance) { //一定距離內會偵測且有移動或屬性突兀
                if (chasedPlayer.movingState == Player.MovingState.WALK) {
                    isChase = true;
                }
            }
            if (nearest < Global.WINDOW_WIDTH / 2) { //玩家圖片屬性與當前地區物件屬性不同
                if (chasedPlayer.getPositionType() != chasedPlayer.getTransformationAnimationType()) {
                    isChase = true;
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
            if (player.roleState == RoleState.HUNTER) {
                if (dc < nearest) {
                    nearest = dc;
                    this.chasedPlayer = player;
                }
                if (this.chasedPlayer == null) {
                    return;
                }
                if (nearest < Global.SCREEN_X / 5) { //一定距離內會偵測且有移動或屬性突兀
                    if (chasedPlayer.movingState == Player.MovingState.WALK) {
                        isRun = true;
                    }
                }
            }
            if (chasedPlayer != null && isRun) {
                float chaseDx = Math.abs(chasedPlayer.collider().centerX() - painter().centerX());
                float chaseDy = Math.abs(chasedPlayer.collider().bottom() - painter().centerY() - 10);
                float chaseDc = (float) Math.sqrt(chaseDx * chaseDx + chaseDy * chaseDy);//計算斜邊,怪物與人物的距離
                if (chaseDc >= Global.COMPUTER_GIVE_UP_DISTANCE2) {
                    isRun = false;
                    nearest = Global.NEAREST;
                }
                if (chasedPlayer.roleState != RoleState.HUNTER) {
                    isRun = false;
                    nearest = Global.NEAREST;
                }
            }
            if (roleState == RoleState.BUMPING) {
                isRun = true;
            }
        }
    }

    @Override
    public void transform() {
        storedTransformAnimation = new Animation(AllImages.getRandomImgArrAndType());
        if (!canTransform || roleState == RoleState.HUNTER) {
            return;
        }
        currentAnimation = storedTransformAnimation;
        transformCD.play();
        if (!transformTime.count()) {
            transformTime.stop();
        }
        transformTime.play();
        canTransform = false;
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


    public void upAndDownIsCollision(GameObject gameObject, int y) {
        if (collider().right() < gameObject.collider().left()) {
            return;
        }
        if (collider().left() > gameObject.collider().right()) {
            return;
        }
        if (collider().bottom() + y < gameObject.collider().top()) {
            return;
        }
        if (collider().top() + y > gameObject.collider().bottom()) {
            return;
        }
        if (!canPassWall) {
            translateY(-y);
        }
    }

    public void rightAndLeftIsCollision(GameObject gameObject, int x) {
        if (collider().right() + x < gameObject.collider().left()) {
            return;
        }
        if (collider().left() + x > gameObject.collider().right()) {
            return;
        }
        if (collider().bottom() < gameObject.collider().top()) {
            return;
        }
        if (collider().top() > gameObject.collider().bottom()) {
            return;
        }
        if (!canPassWall) {
            translateX(-x);
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
        speed -= 1;
        movement.setSpeed(speed);
    }

    @Override
    public void propsEffectUpdate() {
        if (propsStopTimeDelay.count()) {
            canMove = true;
        }
        if (propsThunderDelay.count()) {
            speed += 1;
            movement.setSpeed(speed);
        }
    }

    public void AILevel1() {
        setSpeed(Global.COMPUTER_SPEED1);
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE1;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE1;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE1;
        notStopDelay.stop();
    }

    public void AILevel2() {
        setSpeed(Global.COMPUTER_SPEED2);
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE2;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE2;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE2;
        notStopDelay.play();
        notStopDelay.loop();
    }

    public void AILevel3() {
        setSpeed(Global.COMPUTER_SPEED3);
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE3;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE3;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE3;
        notStopDelay.play();
        notStopDelay.loop();
    }

    public void AILevel4() {
        setSpeed(Global.COMPUTER_SPEED4);
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE4;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE3;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE4;
        notStopDelay.play();
        notStopDelay.loop();
    }

    public void AILevel5() {
        setSpeed(Global.COMPUTER_SPEED4 + 1);
        chaseDistance = Global.COMPUTER_CHASE_DISTANCE4;
        propsChaseDistance = Global.COMPUTER_PROPS_CHASE_DISTANCE3;
        giveUpDistance = Global.COMPUTER_GIVE_UP_DISTANCE4;
        notStopDelay.play();
        notStopDelay.loop();
    }


    public void translateInConnect(int x, int y) {
        if (isConnect) {
            ClientClass.getInstance().sent(Pact.COMPUTER_MOVE, bale(Integer.toString(id), Integer.toString(x), Integer.toString(y)));
        } else {
            translate(x, y);
        }
    }


    public void setSpeed(int speed) {
        this.speed = speed;
        movement.setSpeed(this.speed);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
