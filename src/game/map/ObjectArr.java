package game.map;

import game.core.Global;
import game.gameObj.Props;
import game.gameObj.obstacle.MovingObstacle;
import game.gameObj.obstacle.TransformObstacle;
import game.graphic.AllImages;
import game.graphic.Animation;

import java.util.ArrayList;
import java.util.List;

public class ObjectArr {
    public static ArrayList<Props> propsArr = new ArrayList<Props>(List.of(new Props(), new Props(), new Props()));

    //0-1920, 0-1920        森林有：bee bunny1 bunny2 frog snail snack
    //1920-3840, 0-1920     火山有：barnacle bat ladyBug snakeLava snakeSlime
    //0-1920, 1920-3840     冰原有：slime slimeBlue slimeGreen
    //1902-3840, 1920-3840  村莊有：fly flyMan mouse spider
    public static ArrayList<TransformObstacle> transformObstaclList1 = new ArrayList<>(List.of(
            //森林
            new MovingObstacle(900, 900, AllImages.bee),
            new MovingObstacle(1700, 300, AllImages.bunny1),
            new MovingObstacle(600, 1700, AllImages.bunny2),
            new MovingObstacle(550, 1100, AllImages.frog),
            new MovingObstacle(1400, 780, AllImages.snail),
            new MovingObstacle(1100, 1600, AllImages.snack),

            //火山
            new MovingObstacle(2300, 900, AllImages.barnacle),
            new MovingObstacle(3000, 450, AllImages.bat),
            new MovingObstacle(2200, 230, AllImages.ladyBug),
            new MovingObstacle(2800, 1700, AllImages.snakeLava),
            new MovingObstacle(3700, 600, AllImages.snakeSlime),
            //冰原
            new MovingObstacle(1800, 3300, AllImages.slime),
            new MovingObstacle(340, 2600, AllImages.slimeBlue),
            new MovingObstacle(980, 2000, AllImages.slimeGreen),

            //村莊
            new MovingObstacle(3300, 2000, AllImages.fly),
            new MovingObstacle(2500, 3000, AllImages.flyMan),
            new MovingObstacle(2700, 3700, AllImages.mouse),
            new MovingObstacle(2900, 2900, AllImages.spider)

    ));

    public static ArrayList<TransformObstacle> transformObstaclList2 () {
        ArrayList<TransformObstacle> allBeeList = new ArrayList<>();
        for(int i = 0; i < 100; i ++) {
            allBeeList.add(new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee));
        }
        return allBeeList;
    }

    public static ArrayList<TransformObstacle> transformObstaclList3 = new ArrayList<>(List.of(
            //森林
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            new MovingObstacle(Global.random(0, 1920), Global.random(0, 1920), AllImages.bee),
            //火山
            new MovingObstacle(2300, 900, AllImages.barnacle),
            new MovingObstacle(3000, 450, AllImages.bat),
            new MovingObstacle(2200, 230, AllImages.ladyBug),
            new MovingObstacle(2800, 1700, AllImages.snakeLava),
            new MovingObstacle(3700, 600, AllImages.snakeSlime),
            //冰原
            new MovingObstacle(1800, 3300, AllImages.slime),
            new MovingObstacle(340, 2600, AllImages.slimeBlue),
            new MovingObstacle(980, 2000, AllImages.slimeGreen),

            //村莊
            new MovingObstacle(3300, 2000, AllImages.fly),
            new MovingObstacle(2500, 3000, AllImages.flyMan),
            new MovingObstacle(2700, 3700, AllImages.mouse),
            new MovingObstacle(2900, 2900, AllImages.spider)
    ));

}
