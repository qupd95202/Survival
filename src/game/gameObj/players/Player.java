package game.gameObj.players;

import game.Menu.Mouse;
import game.core.Global;
import game.core.Movement;
import game.core.Position;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.Transformation;
import game.gameObj.mapObj.MapObject;
import game.gameObj.obstacle.TransformObstacle;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.graphic.ImgArrAndType;
import game.scene_process.Camera;
import game.utils.CommandSolver;
import game.utils.Delay;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static game.gameObj.Props.Type.trap;

public class Player extends GameObject implements CommandSolver.KeyListener {


    public enum RoleState {
        HUNTER,
        BUMPING,
        PREY;
    }

    public enum MovingState {
        WALK,
        RUN,
        STAND,
        BUMP;
    }


    public static final Animation bumpAnimation = new Animation(AllImages.bump);


    //移動相關
    protected Movement movement;
    protected boolean canMove;
    public Delay canMoveDelay;
    protected MovingState movingState;

    //交換身分相關
    protected RoleState roleStateBeforeBump;
    public RoleState roleState;
    protected int point; //積分
    protected Player bumpPlayer; //撞到的角色
    protected Delay pointDelay;
    private Delay collisionDelay;

    //變身相關
    protected boolean canTransform;
    protected Animation storedTransformAnimation;
    protected Delay transformCD;//冷卻時間
    protected Delay transformTime;

    //動畫處理部分拉出
    protected Animation originalAnimation;
    protected Animation currentAnimation;

    //道具相關
    private boolean isUseTeleportation;//判斷是否按F(瞬間移動鍵)
    private boolean canUseTeleportation; //有沒有撿到此道具
    protected Delay trapDelay;
    public boolean isThunder;
    public boolean isSuperStar;
    private int currentSpeed;
    private Delay superStarDelay;
    public boolean isHunterStop;
    public boolean isDecreaseGameTime;
    public boolean isHunterWatcher;
    private Delay hunterWatcherDelay;

    //扣分相關
    private boolean isInClosedArea;

    //連線相關
    private int id;

    //for連線用的Player 需要 set當前動畫 和 set是什麼身分
//    public Player(int x, int y, ImgArrAndType imageArrayList, RoleState roleState) {
//        super(x, y, Global.PLAYER_WIDTH, Global.PLAYER_HEIGHT);
//        movement = new Movement(Global.NORMAL_SPEED);//一般角色移動
//        collider().scale(painter().width() - 10, painter().height() - 10);
//        painter().setCenter(collider().centerX(), collider().centerY());
//        point = 0;
//        this.id = id;
//        canMove = true;
//        isNothingBlock = true;
//        canPass = true;
//        canTransform = true;
//        isUseTeleportation = false;
//        canUseTeleportation = false;
//        isInClosedArea = false;
//
//        this.roleState = roleState;
//        roleStateBeforeBump = roleState;
//        this.currentAnimation = new Animation(imageArrayList);
//        originalAnimation = new Animation(imageArrayList);
//
//        pointDelay = new Delay(60);
//        collisionDelay = new Delay(180);
//        canMoveDelay = new Delay(300);
//        transformCD = new Delay(600); //十秒
//        transformTime = new Delay(900); //十五秒
//        trapDelay = new Delay(120);
//        superStarDelay = new Delay(600);
//        hunterWatcherDelay = new Delay(600);
//        movingState = MovingState.STAND;
//    }


    public Player(int x, int y, ImgArrAndType imageArrayList, RoleState roleState) {
        super(x, y, Global.PLAYER_WIDTH, Global.PLAYER_HEIGHT);
        movement = new Movement(Global.NORMAL_SPEED);//一般角色移動
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());
        point = 0;

        canMove = true;
        canPass = true;
        canTransform = true;
        isUseTeleportation = false;
        canUseTeleportation = false;
        isInClosedArea = false;

        this.roleState = roleState;
        roleStateBeforeBump = roleState;
        this.currentAnimation = new Animation(imageArrayList);
        originalAnimation = new Animation(imageArrayList);

        pointDelay = new Delay(60);
        collisionDelay = new Delay(180);
        canMoveDelay = new Delay(300);
        transformCD = new Delay(600); //十秒
        transformTime = new Delay(900); //十五秒
        trapDelay = new Delay(120);
        superStarDelay = new Delay(600);
        hunterWatcherDelay = new Delay(600);
        movingState = MovingState.STAND;
    }


    @Override
    public void paintComponent(Graphics g) {
        currentAnimation.paint(painter().left(), painter().top(), painter().width(), painter().height(), g);
    }

    @Override
    public void update() {
        propsEffectUpdate();
        transformResetUpdate();
        if (trapDelay.count()) {
            canMove = true;
        }
        addPoint();
        currentAnimation.update();
    }

    @Override
    public void keyPressed(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.UP.getValue() ||
                commandCode == Global.KeyCommand.DOWN.getValue() ||
                commandCode == Global.KeyCommand.LEFT.getValue() ||
                commandCode == Global.KeyCommand.RIGHT.getValue()) {
            movingState = MovingState.WALK;
        }
        //觸發Movement監聽到的移動變化
        movement.keyPressed(commandCode, trigTime);
        if (commandCode == Global.KeyCommand.TRANSFORM.getValue()) {
            transform();
        }
        if (commandCode == Global.KeyCommand.TELEPORTATION.getValue()) {
            clickedTeleportation();
        }
        move();
    }


    @Override
    public void keyReleased(int commandCode, long trigTime) {
        if (commandCode == Global.KeyCommand.UP.getValue() ||
                commandCode == Global.KeyCommand.DOWN.getValue() ||
                commandCode == Global.KeyCommand.LEFT.getValue() ||
                commandCode == Global.KeyCommand.RIGHT.getValue()) {
            movingState = MovingState.STAND;
        }
    }

    @Override
    public void keyTyped(char c, long trigTime) {

    }


    public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime, ArrayList<MapObject> unPassMapObjects, ArrayList<TransformObstacle> transformObstacles, Camera camera, Mouse mouse) {
        if (state == CommandSolver.MouseState.CLICKED) {
            int mouseX = e.getX() + camera.painter().left();
            int mouseY = e.getY() + camera.painter().top();
            for (TransformObstacle transformObstacle : transformObstacles) {
                if (transformObstacle.isXYin(mouseX, mouseY)) {
                    chooseTransformObject(transformObstacle);
                }
            }
            for (MapObject mapObject : unPassMapObjects) {
                if (mapObject.isXYin(mouseX, mouseY)) {
                    return;
                }
            }
            useTeleportation(mouseX, mouseY);
        }
        mouse.mouseTrig(e, state, trigTime);
    }

//    /**
//     * 改變角色動畫方向
//     */
//    protected void manageDirection() {
//        if (movement.isMoving()) { //如果有在移動 代表有x y變化 需要判定Direction
//            this.dir = Global.KeyCommand.fromMovementChangeDirection(movement);
//        }
//    }

    public void move() {
        if (!canMove) {
            return;
        }
        //一般移動位置的部分
        translate(movement.getVector2D().getX(), movement.getVector2D().getY());
        keepInMap();
    }

    /**
     * 讓角色不會超出邊界
     */
    public void keepInMap() {
        if (touchBottom() || touchLeft() || touchRight() || touchTop()) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }

    public void isCollisionForMovement(GameObject gameObject) {
        if (collider().bottom() + movement.getVector2D().getY() < gameObject.collider().top()) {
            return;
        }
        if (collider().top() + movement.getVector2D().getY() > gameObject.collider().bottom()) {
            return;
        }
        if (collider().right() + movement.getVector2D().getX() < gameObject.collider().left()) {
            return;
        }
        if (collider().left() + movement.getVector2D().getX() > gameObject.collider().right()) {
            return;
        }
        notMove();
    }


    public void notMove() {
        translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
    }


    public void exchangeRole(Player player) {
        if (roleState != player.roleState && roleState != RoleState.BUMPING && player.roleState != RoleState.BUMPING) {
            if (isCollision(player)) {
                if (currentAnimation != bumpAnimation && player.currentAnimation != bumpAnimation) {
                    bumpPlayer = player;
                    bump(bumpPlayer);
                    pointExchange(bumpPlayer);
                }
            }
        }
        if (collisionDelay.count()) {
            animationExchange(bumpPlayer);
        }
        if (canMoveDelay.count()) {
            roleStateExchange(bumpPlayer);
            canMove = true;
            bumpPlayer.canMove = true;
        }
    }

    public void pointExchange(Player player) {
        int temp = point;
        setPoint(player.getPoint());
        player.setPoint(temp);
    }


    public void bump(Player player) {
        setCurrentAnimation(bumpAnimation);
        player.setCurrentAnimation(bumpAnimation);
        if (roleState == RoleState.PREY) {
            canMove = false;
        }
        if (player.roleState == RoleState.PREY) {
            player.canMove = false;
        }
        //碰撞狀態
        roleState = RoleState.BUMPING;
        player.roleState = RoleState.BUMPING;

        collisionDelay.play();
        canMoveDelay.play();
    }

    public void animationExchange(Player player) {
        Animation temp = originalAnimation;
        originalAnimation = player.originalAnimation;
        player.originalAnimation = temp;
        //當前變回original
        currentAnimation = originalAnimation;
        player.currentAnimation = player.originalAnimation;
    }

    public void roleStateExchange(Player player) {
        roleState = player.roleStateBeforeBump;
        player.roleState = roleStateBeforeBump;
        roleStateBeforeBump = roleState;
        player.roleStateBeforeBump = player.roleState;
    }

    public void chooseTransformObject(Transformation transformation) {
        storedTransformAnimation = new Animation(transformation.getImgArrAndType());
    }

    public void transform() {
        if (storedTransformAnimation == null ||
                !canTransform ||
                roleState == RoleState.HUNTER) {
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

    protected void transformResetUpdate() {
        if (transformCD.count()) {
            canTransform = true;
        }
        if (transformTime.count()) {
            currentAnimation = originalAnimation;
        }
    }


    /**
     * 每秒增加1積分，若在扣分區，每秒扣2積分
     */
    protected void addPoint() {
        if (roleState == RoleState.HUNTER) {
            return;
        }
        pointDelay.play();
        if (pointDelay.count()) {
            if (isInClosedArea) {
                if (point > 0) {
                    point -= 1;
                }
            } else if (movingState == MovingState.WALK) {
                point++;

            }
        }
    }

    /**
     * 取得角色當前移動狀態
     *
     * @return
     */
//    public void setCanMove(boolean canMove) {
//        this.canMove = canMove;
//    }
//    public void notMove() {
//        isNothingBlock = false;
//    }
    public void notMoveInConnect() {
        translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
    }


//    public void setNothingBlock(boolean nothingBlock) {
//        isNothingBlock = nothingBlock;
//    }

    public void clickedTeleportation() {
        if (canUseTeleportation) {
            isUseTeleportation = true;
        }
    }

    public void useTeleportation(int x, int y) {
        if (!isUseTeleportation || !canUseTeleportation) {
            return;
        }
        setXY(x, y);
        isUseTeleportation = false;
        canUseTeleportation = false;
    }


    public void collideProps(Props props) {
        switch (props.getPropsType()) {
            case addSpeed:
                if (Global.IS_DEBUG) {
                    System.out.println("加速");
                }
                if (movement.getSpeed() < Global.SPEED_MAX) {
                    movement.addSpeed(1);
                }
                break;

            case teleportation:
                if (Global.IS_DEBUG) {
                    System.out.println("瞬移");
                }
                canUseTeleportation = true;
                break;

            case trap:
                if (Global.IS_DEBUG) {
                    System.out.println("不能動");
                }
                canMove = false;
                trapDelay.play();
                break;
            default:
                if (Global.IS_DEBUG) {
                    System.out.println("加分");
                }
                point += 10;
        }
    }

    public void collidePropsInSurvivalMode(Props props) {
        switch (props.getPropsType()) {
            case addSpeed:
                if (Global.IS_DEBUG)
                    System.out.println("加速");
                if (movement.getSpeed() < Global.SPEED_MAX) {
                    movement.addSpeed(2);
                }
                break;

            case teleportation:
                if (Global.IS_DEBUG)
                    System.out.println("瞬移");
                canUseTeleportation = true;
                break;

            case trap:
                if (Global.IS_DEBUG)
                    System.out.println("不能動");
                canMove = false;
                trapDelay.play();
                break;
            case thunder:
                if (Global.IS_DEBUG)
                    System.out.println("打雷");
                isThunder = true;
                break;
            case superStar:
                if (Global.IS_DEBUG)
                    System.out.println("超級星星");
                superStarDelay.play();
                isSuperStar = true;
                currentSpeed = movement.getSpeed();
                movement.setSpeed(Global.SPEED_MAX);
                break;
            case timeStop:
                if (Global.IS_DEBUG)
                    System.out.println("獵人時間暫停");
                isHunterStop = true;
                break;
            case hunterWatcher:
                if (Global.IS_DEBUG) {
                    System.out.println("透視");
                }
                hunterWatcherDelay.play();
                isHunterWatcher = true;
                break;
            default:
                if (Global.IS_DEBUG)
                    System.out.println("遊戲時間減少");
                isDecreaseGameTime = true;

        }
    }

    public void propsEffectUpdate() {
        if (superStarDelay.count()) {
            isSuperStar = false;
            movement.setSpeed(currentSpeed);
        }
        if (hunterWatcherDelay.count()) {
            isHunterWatcher = false;
        }
    }

    public MovingState getMovingState() {
        return movingState;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    private void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public Animation getStoredTransformAnimation() {
        return storedTransformAnimation;
    }

    public boolean isUseTeleportation() {
        return isUseTeleportation;
    }

    public boolean isCanUseTeleportation() {
        return canUseTeleportation;
    }

    public int transformCDTime() {
        return 10 - transformCD.getCount() / 60;
    }

    public Global.MapAreaType getPositionType() {
        if (painter().left() < Global.UNIT_WIDTH * Global.MAP_WIDTH / 2 && painter().top() < Global.UNIT_HEIGHT * Global.MAP_HEIGHT / 2) {
            return Global.MapAreaType.FOREST;
        } else if (painter().left() < Global.UNIT_WIDTH * Global.MAP_WIDTH / 2 && painter().top() < Global.UNIT_HEIGHT * Global.MAP_HEIGHT) {
            return Global.MapAreaType.ICEFIELD;
        } else if (painter().left() < Global.UNIT_WIDTH * Global.MAP_WIDTH && painter().top() < Global.UNIT_HEIGHT * Global.MAP_HEIGHT / 2) {
            return Global.MapAreaType.VOLCANO;
        } else {
            return Global.MapAreaType.VILLAGE;
        }
    }

    public Global.MapAreaType getTransformationAnimationType() {
        return currentAnimation.getMapAreaType();
    }


    public void setInClosedArea(boolean inClosedArea) {
        isInClosedArea = inClosedArea;
    }

    public boolean isInClosedArea() {
        return isInClosedArea;

    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int ID() {
        return id;
    }
}
