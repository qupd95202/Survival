package game.map;

import game.core.Global;
import game.gameObj.Props;
import game.gameObj.obstacle.MovingObstacle;
import game.gameObj.obstacle.TransformObstacle;
import game.gameObj.players.ComputerPlayer;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.graphic.PropsAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectArr {
    public static ArrayList<Props> propsArr = new ArrayList<Props>(List.of(new Props(), new Props(), new Props(), new Props(), new Props()));
    private ArrayList<Props> propsArrConnectPoint = new ArrayList<Props>(List.of(new Props(100, 500, Props.Type.addPoint), new Props(500, 500, Props.Type.addSpeed), new Props(2000, 500, Props.Type.teleportation), new Props(2500, 3000, Props.Type.trap)));
    private ArrayList<ComputerPlayer> computerPlayersConnectPoint = new ArrayList<>(List.of(new ComputerPlayer(100, 100, AllImages.beige, Player.RoleState.HUNTER,0),
            new ComputerPlayer(3000, 100, AllImages.blue, Player.RoleState.PREY,1),
            new ComputerPlayer(100, 3000, AllImages.blue, Player.RoleState.PREY,2)));
    public static ArrayList<Props> propsArrSurvivalGame = new ArrayList<Props>(List.of(new Props(1), new Props(1), new Props(1), new Props(1), new Props(1)));

    //0-1920, 0-1920        森林有：bee bunny1 bunny2 frog snail snack
    //1920-3840, 0-1920     火山有：barnacle bat ladyBug snakeLava snakeSlime
    //0-1920, 1920-3840     冰原有：slime slimeBlue slimeGreen
    //1902-3840, 1920-3840  村莊有：fly flyMan mouse spider
    public static ArrayList<TransformObstacle> transformObstaclList1 = new ArrayList<>(List.of(
            //森林
            new MovingObstacle(900, 900, AllImages.bee),
            new MovingObstacle(1200, 480, AllImages.bee),
            new MovingObstacle(1700, 300, AllImages.bunny1),
            new MovingObstacle(30, 500, AllImages.bunny1),
            new MovingObstacle(600, 1700, AllImages.bunny2),
            new MovingObstacle(490, 700, AllImages.bunny2),
            new MovingObstacle(550, 1100, AllImages.frog),
            new MovingObstacle(1300, 1250, AllImages.frog),
            new MovingObstacle(1400, 780, AllImages.snail),
            new MovingObstacle(1400, 780, AllImages.snail),
            new MovingObstacle(600, 300, AllImages.snack),
            new MovingObstacle(550, 1200, AllImages.snack),

            //火山
            new MovingObstacle(2313, 900, AllImages.barnacle),
            new MovingObstacle(2072, 1400, AllImages.barnacle),
            new MovingObstacle(2738, 450, AllImages.bat),
            new MovingObstacle(2845, 755, AllImages.bat),
            new MovingObstacle(3504, 413, AllImages.ladyBug),
            new MovingObstacle(2100, 789, AllImages.ladyBug),
            new MovingObstacle(2800, 1700, AllImages.snakeLava),
            new MovingObstacle(2800, 1068, AllImages.snakeLava),
            new MovingObstacle(1955, 1432, AllImages.snakeSlime),
            new MovingObstacle(3631, 185, AllImages.snakeSlime),
            //冰原
            new MovingObstacle(587, 1944, AllImages.slime),
            new MovingObstacle(966, 2147, AllImages.slime),
            new MovingObstacle(1800, 3535, AllImages.slime),
            new MovingObstacle(400, 2424, AllImages.slimeBlue),
            new MovingObstacle(200, 3041, AllImages.slimeBlue),
            new MovingObstacle(770, 2004, AllImages.slimeBlue),
            new MovingObstacle(1309, 2701, AllImages.slimeGreen),
            new MovingObstacle(980, 3650, AllImages.slimeGreen),
            new MovingObstacle(398, 1937, AllImages.slimeGreen),

            //村莊
            new MovingObstacle(3300, 2791, AllImages.fly),
            new MovingObstacle(2381, 2965, AllImages.fly),
            new MovingObstacle(3301, 3083, AllImages.fly),
            new MovingObstacle(2515, 2699, AllImages.flyMan),
            new MovingObstacle(2634, 2718, AllImages.flyMan),
            new MovingObstacle(2500, 2769, AllImages.flyMan),
            new MovingObstacle(3691, 2405, AllImages.mouse),
            new MovingObstacle(2700, 3700, AllImages.mouse),
            new MovingObstacle(3168, 2857, AllImages.mouse),
            new MovingObstacle(2900, 2900, AllImages.spider),
            new MovingObstacle(2728, 3024, AllImages.spider),
            new MovingObstacle(3246, 2900, AllImages.spider)


    ));


    //隨機產生
    public static ArrayList<TransformObstacle> transformObstacRandomlList() {
        ArrayList<TransformObstacle> randomList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomList.add(new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee));
        }
        for (int i = 0; i < 10; i++) {
            randomList.add(new MovingObstacle(Global.random(1920, 3840), Global.random(0, 1920), AllImages.bat));
        }
        for (int i = 0; i < 10; i++) {
            randomList.add(new MovingObstacle(Global.random(0, 1920), Global.random(1920, 3840), AllImages.slimeBlue));
        }
        for (int i = 0; i < 10; i++) {
            randomList.add(new MovingObstacle(Global.random(1920, 3840), Global.random(1920, 3840), AllImages.mouse));
        }
        return randomList;
    }

    //連線生產道具
    public void genPropsInConnectPoint(int x, int y, Props.Type type) {
        propsArrConnectPoint.add(new Props(x, y, type));
    }

    public ArrayList<Props> getPropsArrConnectPoint() {
        return propsArrConnectPoint;
    }


    public ArrayList<ComputerPlayer> getComputerPlayersConnectPoint() {
        return computerPlayersConnectPoint;
    }

}
