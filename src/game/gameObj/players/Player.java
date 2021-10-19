package game.gameObj.players;

import game.core.Global;
import game.core.Movement;
import game.core.Position;
import game.gameObj.GameObject;
import game.gameObj.Props;
import game.gameObj.Transformation;
import game.graphic.AllImages;
import game.graphic.Animation;
import game.utils.CommandSolver;
import game.utils.Delay;

import java.awt.*;

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

    public static final Animation bumpAnimation = AllImages.bump;


    //移動相關
    protected Movement movement;
    protected boolean canMove;
    private Delay canMoveDelay;
    private boolean isNothingBlock;
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
    private Delay trapDelay;


    public Player(int x, int y, Animation currentAnimation, RoleState roleState) {
        super(x, y, Global.PLAYER_WIDTH, Global.PLAYER_HEIGHT);
        movement = new Movement(Global.NORMAL_SPEED);//一般角色移動
        collider().scale(painter().width() - 10, painter().height() - 10);
        painter().setCenter(collider().centerX(), collider().centerY());
        point = 0;

        canMove = true;
        isNothingBlock = true;
        canPass = true;
        canTransform = true;
        isUseTeleportation = false;
        canUseTeleportation = false;

        this.roleState = roleState;
        roleStateBeforeBump = roleState;
        this.currentAnimation = currentAnimation;
        originalAnimation = currentAnimation;

        pointDelay = new Delay(60);
        collisionDelay = new Delay(180);
        canMoveDelay = new Delay(300);
        transformCD = new Delay(600); //十秒
        transformTime = new Delay(900); //十五秒
        trapDelay = new Delay(120);

        movingState = MovingState.STAND;

    }

    @Override
    public void paintComponent(Graphics g) {
        currentAnimation.paint(painter().left(), painter().top(), painter().width(), painter().height(), g);
    }

    @Override
    public void update() {
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
        if (isNothingBlock) {
            move();
        }

        if (commandCode == Global.KeyCommand.TRANSFORM.getValue()) {
            transform();
        }
        if (commandCode == Global.KeyCommand.TELEPORTATION.getValue()) {
            clickedTeleportation();
        }
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
            movement.move(0, 0);
        }
        //一般移動位置的部分
        translate(movement.getVector2D().getX(), movement.getVector2D().getY());
        keepInMap();
    }

//    /**
//     * 取得角色當前身分
//     *
//     * @return
//     */
//    public boolean isHunter() {
//        return originType == 6;  // imageType.getImageType() == ImageType.MapAndRoleType.HUNTER
//    }

    /**
     * 讓角色不會超出邊界
     */
    public void keepInMap() {
        if (touchBottom() || touchLeft() || touchRight() || touchTop()) {
            translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
        }
    }

//    public void notMove() {
//        translate(-movement.getVector2D().getX(), -movement.getVector2D().getY());
//    }

    public boolean isCollisionForMovement(GameObject gameObject) {
        if (collider().bottom() + movement.getVector2D().getY() < gameObject.collider().top()) {
            return false;
        }
        if (collider().top() + movement.getVector2D().getY() > gameObject.collider().bottom()) {
            return false;
        }
        if (collider().right() + movement.getVector2D().getX() < gameObject.collider().left()) {
            return false;
        }
        if (collider().left() + movement.getVector2D().getX() > gameObject.collider().right()) {
            return false;
        }
        return true;
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
        currentAnimation = player.originalAnimation;
        player.currentAnimation = originalAnimation;
        originalAnimation = currentAnimation;
        player.originalAnimation = player.currentAnimation;
    }

    public void roleStateExchange(Player player) {
        roleState = player.roleStateBeforeBump;
        player.roleState = roleStateBeforeBump;
        roleStateBeforeBump = roleState;
        player.roleStateBeforeBump = player.roleState;
    }

    public void chooseTransformObject(Transformation transformation) {
        storedTransformAnimation = transformation.getAnimation();
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
     * 每秒增加1積分
     */
    protected void addPoint() {
        if (roleState == RoleState.HUNTER) {
            return;
        }
        pointDelay.play();
        if (pointDelay.count()) {
            if (movingState == MovingState.WALK) {
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
    public void notMove() {
        isNothingBlock = false;
    }

    public void setNothingBlock(boolean nothingBlock) {
        isNothingBlock = nothingBlock;
    }

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
                System.out.println("加速");
                if (movement.getSpeed() < Global.SPEED_MAX) {
                    movement.addSpeed(1);
                }
                break;

            case teleportation:
                System.out.println("瞬移");
                canUseTeleportation = true;
                break;

            default:
                System.out.println("不能動");
                canMove = false;
                trapDelay.play();
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
}
