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
        winterTree1(MapAreaType.ICEFIELD, new Path().img().background().winterTree1()),
        rock(MapAreaType.VILLAGE, new Path().img().background().rock()),
        house1(MapAreaType.VILLAGE, new Path().img().background().house1()),
        house2(MapAreaType.VILLAGE, new Path().img().background().house2()),
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
    public MapObject(int x, int y, int width, int height, String MapObjectName) {
        super(x, y, width, height);

        switch (MapObjectName) {
            case "tree1" :
                this.type = Type.tree1;
                collider().scale(painter().width() - 50, painter().height() - 80);
                painter().setCenter(collider().centerX() , collider().centerY() - 40);
                break;

            case "winterTree1" :
                this.type = Type.winterTree1;
                collider().scale(painter().width() - 50, painter().height() - 60);
                painter().setCenter(collider().centerX() , collider().centerY() - 30);
                break;
            case "rock" :
                this.type = Type.rock;
                collider().scale(painter().width() - 40, painter().height() - 40);
                painter().setCenter(collider().centerX() , collider().centerY() - 20);
                break;
            case "house1" :
                this.type = Type.house1;
                collider().scale(painter().width() - 100, painter().height() - 100);
                painter().setCenter(collider().centerX(), collider().centerY());
                break;
            case "house2" :
                this.type = Type.house2;
                collider().scale(painter().width() - 100, painter().height() - 100);
                painter().setCenter(collider().centerX(), collider().centerY());
                break;

            default :
                this.type = Type.tree1;
                break;
        }
        img = SceneController.getInstance().imageController().tryGetImage(this.type.path);
    }

    public Image getImg() {
        return img;
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

    public boolean isXYin(int x, int y) {
        if (x <= painter().right() && x >= painter().left() && y <= painter().bottom() && y >= painter().top()) {
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
