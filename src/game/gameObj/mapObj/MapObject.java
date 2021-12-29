package game.gameObj.mapObj;

import game.controllers.SceneController;
import game.core.Global;
import game.core.Global.*;
import game.gameObj.GameObject;
import game.utils.Path;

import java.awt.*;


//第一層地圖物件
public class MapObject extends GameObject {
    private Type type;
    private Image img;


    //列出會有哪些地圖素材名稱，且先存好路徑
    public enum Type {
        tree1(MapAreaType.FOREST, new Path().img().background().tree1()),
        tree2(MapAreaType.FOREST, new Path().img().background().tree2()),
        winterTree1(MapAreaType.ICEFIELD, new Path().img().background().winterTree1()),
        winterTree2(MapAreaType.ICEFIELD, new Path().img().background().winterTree2()),
        rock(MapAreaType.VILLAGE, new Path().img().background().rock()),
        volcanoTree(MapAreaType.VILLAGE, new Path().img().background().volcanoTree()),
        castleWall(MapAreaType.VILLAGE, new Path().img().background().castleWall()),
        tower(MapAreaType.VILLAGE, new Path().img().background().tower()),
        house1(MapAreaType.VILLAGE, new Path().img().background().house1()),
        ;

        private MapAreaType mapAreaType; //所屬的地圖類型
        private String path;

        Type(MapAreaType mapAreaType, String path) {
            this.mapAreaType = mapAreaType;
            this.path = path;
        }

        public MapAreaType getMapAreaType() {
            return mapAreaType;
        }
    }


    //要能帶入資源 以利格子上決定哪個圖

    public Image getImg() {
        return img;
    }

    public MapObject(int x, int y, int width, int height, String MapObjectName) {
        super(x, y, width, height);

        switch (MapObjectName) {
            case "tree1":
                this.type = Type.tree1;
                collider().scale(painter().width() - 50, painter().height() - 80);
                painter().setCenter(collider().centerX(), collider().centerY() - 40);
                break;
            case "tree2":
                this.type = Type.tree2;
                collider().scale(painter().width() - 50, painter().height() - 80);
                painter().setCenter(collider().centerX(), collider().centerY() - 40);
                break;

            case "winterTree1":
                this.type = Type.winterTree1;
                collider().scale(painter().width() - 50, painter().height() - 65);
                painter().setCenter(collider().centerX(), collider().centerY() - 30);
                break;
            case "winterTree2":
                this.type = Type.winterTree2;
                collider().scale(painter().width() - 50, painter().height() - 65);
                painter().setCenter(collider().centerX(), collider().centerY() - 30);
                break;
            case "rock":
                this.type = Type.rock;
                collider().scale(painter().width() - 20, painter().height() - 20);
                painter().setCenter(collider().centerX(), collider().centerY() - 20);
                break;
            case "volcanoTree":
                this.type = Type.volcanoTree;
                collider().scale(painter().width() - 40, painter().height() - 60);
                painter().setCenter(collider().centerX(), collider().centerY() - 20);
                break;
            case "castleWall":
                this.type = Type.castleWall;
                collider().scale(painter().width() - 10, painter().height() - 10);
                painter().setCenter(collider().centerX(), collider().centerY());
                break;

            case "tower":
                this.type = Type.tower;
                collider().scale(painter().width(), painter().height() - 50);
                painter().setCenter(collider().centerX(), collider().centerY() - 25);
                break;
            case "house1":
                this.type = Type.house1;
                collider().scale(painter().width(), painter().height() - 50);
                painter().setCenter(collider().centerX(), collider().centerY() - 25);
                break;


            default:
                this.type = Type.tree1;
                break;
        }
        img = SceneController.getInstance().imageController().tryGetImage(this.type.path);
    }


    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img,
                painter().left(),
                painter().top(),
                painter().width(),
                painter().height(),
                null);
    }

    public boolean isXYNotIn(int x, int y) {
        if (x <= collider().right() && x >= collider().left() - 51 && y <= collider().bottom() && y >= collider().top() - 51
        ) {
            return true;
        }
        return false;
    }

    public boolean isXYNotInMap(int x, int y) {
        if (x >= Global.MAP_PIXEL_WIDTH - 51 || x <= 10 || y >= Global.MAP_PIXEL_HEIGHT - 51 || y <= 40) {
            return true;
        }
        return false;
    }

    @Override
    public void update() {
    }

    public boolean getCanPass() {
        return canPass;
    }

    public void setCanPass(boolean canPass) {
        this.canPass = canPass;
    }
}
