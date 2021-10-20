package game.graphic;

import game.core.Global;

import java.awt.*;
import java.util.ArrayList;

public class ImgArrAndType {
    private ArrayList<Image> imageArrayList;
    private Global.MapAreaType mapAreaType;

    public ImgArrAndType(ArrayList<Image> imageArrayList, Global.MapAreaType mapAreaType) {
        this.imageArrayList = imageArrayList;
        this.mapAreaType = mapAreaType;
    }

    public ArrayList<Image> getImageArrayList() {
        return imageArrayList;
    }

    public void setImageArrayList(ArrayList<Image> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }

    public Global.MapAreaType getMapAreaType() {
        return mapAreaType;
    }
}
