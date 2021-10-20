package game.graphic;

import game.controllers.SceneController;
import game.core.Global;
import game.utils.Path;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AllImages {

    //角色
    public static Animation beige = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Beige_walk1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Beige_walk2()))), Global.MapAreaType.NONE);

    public static Animation blue = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Blue_walk1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Blue_walk2()))), Global.MapAreaType.NONE);

    public static Animation green = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Green_walk1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Green_walk2()))), Global.MapAreaType.NONE);

    public static Animation pink = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Pink_walk1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Pink_walk2()))), Global.MapAreaType.NONE);

    public static Animation yellow = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Yellow_walk1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().walk().Yellow_walk2()))), Global.MapAreaType.NONE);

    //爆炸狀態
    public static Animation bump = new Animation(new ArrayList<Image>(List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().bump().Bump1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().bump().Bump2()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().bump().Bump3()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().bump().Bump4()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().actors().bump().Bump5()))), Global.MapAreaType.NONE);

    //可移動物件
    //怪物(火山)
    public static Animation barnacle = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().barnacle1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().barnacle2()))), Global.MapAreaType.VOLCANO);
    //蝙蝠(火山)
    public static Animation bat = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bat1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bat2()))), Global.MapAreaType.VILLAGE);
    //蜜蜂(森林)
    public static Animation bee = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bee1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bee2()))), Global.MapAreaType.FOREST);
    //bunny1(森林)
    public static Animation bunny1 = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bunny1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bunny2()))), Global.MapAreaType.VILLAGE);
    //bunny2(森林)
    public static Animation bunny2 = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bunny21()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().bunny22()))), Global.MapAreaType.ICEFIELD);
    //fishGreen
    public static Animation fishGreen = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().fishGreen1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().fishGreen2()))), Global.MapAreaType.ICEFIELD);

    //fishPink
    public static Animation fishPink = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().fishPink1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().fishPink2()))), Global.MapAreaType.ICEFIELD);

    //fly(村莊)
    public static Animation fly = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().fly1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().fly2()))), Global.MapAreaType.VILLAGE);

    //flyMan(村莊)
    public static Animation flyMan = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().flyMan1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().flyMan2()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().flyMan3()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().flyMan4()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().flyMan5()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().flyMan6()))), Global.MapAreaType.VOLCANO);

    //frog(森林)
    public static Animation frog = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().frog1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().frog2()))), Global.MapAreaType.VOLCANO);

    //ladyBug(火山)
    public static Animation ladyBug = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().ladyBug1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().ladyBug2()))), Global.MapAreaType.FOREST);

    //mouse(村莊)
    public static Animation mouse = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().mouse1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().mouse2()))), Global.MapAreaType.VILLAGE);

    //slime(冰原)
    public static Animation slime = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().slime1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().slime2()))), Global.MapAreaType.FOREST);

    //slimeBlue(冰原)
    public static Animation slimeBlue = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().slimeBlue1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().slimeBlue2()))), Global.MapAreaType.ICEFIELD);

    //slimeGreen(冰原)
    public static Animation slimeGreen = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().slimeGreen1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().slimeGreen2()))), Global.MapAreaType.FOREST);

    //snail(森林)
    public static Animation snail = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snail1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snail2()))), Global.MapAreaType.FOREST);

    //snack(森林)
    public static Animation snack = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snake1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snake2()))), Global.MapAreaType.FOREST);

    //snakeLava(火山)
    public static Animation snakeLava = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snakeLava1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snakeLava2()))), Global.MapAreaType.FOREST);

    //snakeSlime(火山)
    public static Animation snakeSlime = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snakeSlime1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().snakeSlime2()))), Global.MapAreaType.FOREST);

    //spider(村莊)
    public static Animation spider = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().spider1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().move().spider2()))), Global.MapAreaType.VILLAGE);

    //addSpeed
    public static Animation addSpeed = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().addSpeed()))), Global.MapAreaType.NONE);

    //questionBox
    public static Animation questionBox = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().question1()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().question2()),
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().question3()))), Global.MapAreaType.NONE);

    //runnerDark順移動具不可使用
    public static Animation runnerDark = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().runnerDark()))), Global.MapAreaType.NONE);

    //runnerLight順移可使用
    public static Animation runnerLight = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().runnerLight()))), Global.MapAreaType.NONE);

    //runnerLight順移可使用
    public static Animation runnerNormal = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().runnernormal()))), Global.MapAreaType.NONE);

    //變身格
    public static Animation changeBody = new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().changeBody()))), Global.MapAreaType.NONE);

    public static Animation getRandomAnimation() {
        int random = Global.random(1, 8);
        switch (random) {
            case 1:
                return snack;
            case 2:
                return snail;
            case 3:
                return spider;
            case 4:
                return snakeLava;
            case 5:
                return slimeBlue;
            case 6:
                return slimeGreen;
            case 7:
                return slime;
            default:
                return mouse;
        }

    }

    //WARNING
    public static Animation WARNING=new Animation(new ArrayList<Image>(java.util.List.of(
            SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().warningLabel()),
    SceneController.getInstance().imageController().tryGetImage(new Path().img().objs().dontMove().nothing()))),Global.MapAreaType.NONE);;
}
