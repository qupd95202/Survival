//package game.gameObj;
//
//import game.controllers.SceneController;
//import game.core.Global;
//import game.core.Global.*;
//import game.utils.Path;
//
//import java.awt.*;
//
////第一層地圖物件
//public class MapObjectBlock extends Obstacle{
//    private Type type;
//    private Image img;
//    private boolean canPass;
//
//    //列出會有哪些地圖素材名稱，且先存好路徑
//    public enum Type {
//        SEA(MapAreaType.SEA, new Path().img().background().sea());
//
//        private MapAreaType mapAreaType; //所屬的地圖類型
//        private String path;
//
//        Type(Global.MapAreaType mapAreaType, String path) {
//            this.mapAreaType = mapAreaType;
//            this.path = path;
//        }
//
//        public MapAreaType getMapAreaType() {
//            return mapAreaType;
//        }
//    }
//
//
//    //要能帶入資源 以利格子上決定哪個圖
//    public MapObjectBlock(int x, int y, String MapObjectName) {
//        super(x, y, Global.UNIT_WIDTH, Global.UNIT_HEIGHT);
//        canPass = false;
//
//        switch (MapObjectName) {
//            case "sea" -> this.type = Type.SEA;
//            default -> this.type = Type.SEA;
//        }
//        img = SceneController.getInstance().imageController().tryGetImage(this.type.path);
//    }
//
//    public Image getImg() {
//        return img;
//    }
//
//
//    @Override
//    public void paintComponent(Graphics g) {
//        g.drawImage(img,
//                painter().left(),
//                painter().top(),
//                painter().width(),
//                painter().height(),
//                null);
//
//    }
//
//    @Override
//    public void update() {
//
//    }
//}
