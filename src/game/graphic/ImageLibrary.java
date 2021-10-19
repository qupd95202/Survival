package game.graphic;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageLibrary {

    private final static String PATH_TO_ANIMAL = "/img/animal";

    //存：名稱, 圖片自定義類（裏面有path跟image）
    private Map<String, ImageSet> animals;

    public ImageLibrary() {
        animals = new HashMap<>();
        loadSpritesFromDisk();
    }

    private void loadSpritesFromDisk() {
        loadAnimals();
    }

    //將讀取關於animal資料夾內的部分拆分出來
    private void loadAnimals() {
        String[] folderNames = getFolderNames(PATH_TO_ANIMAL);

        //這邊是要將指定資料夾中 路徑和image存進Map<String, ImageSet> animals
        for(String folderName: folderNames) {
            ImageSet imageSet = new ImageSet();

            //  "/img/animal"裡面的子資料夾
            String pathToFolder = PATH_TO_ANIMAL + "/" + folderName;
            String[] imagesInFolder = getImageInFolder(pathToFolder);

            for(String sheetName: imagesInFolder) {
                imageSet.addSheet(
                        sheetName.substring(0, sheetName.length() - 4),//檔名 去掉.png
                        ImageUtils.loadImage(pathToFolder + "/" + sheetName));
            }
            animals.put(folderName, imageSet);
        }
    }

    /**
     *
     * @param pathToFolder 要讀取的圖片的子資料夾路徑
     * @return 圖片檔名 存在String[]
     */
    private String[] getImageInFolder(String pathToFolder) {
        URL resource = ImageLibrary.class.getResource(pathToFolder);
        File file = new File(resource.getFile());
        return file.list((current, name) -> new File(current, name).isFile());
    }

    /**
     *
     * @param basePath 要讀取的資料夾路徑
     * @return 資料夾內所有檔案的名稱（包含子資料夾名稱） 存在String[]
     */
    private String[] getFolderNames(String basePath) {
        //先取得資料夾在電腦路徑（ＵＲＬ）
        URL resource = ImageLibrary.class.getResource(basePath);
        System.out.println(resource);
        //一樣是資料夾路徑 但是轉換為File類 下一步要要做file.list用
        File file = new File(resource.getFile());
        System.out.println(file);
        //將資料夾路徑內所有檔案名稱存進一個 String[]並回傳
        return file.list((current, name) -> new File(current, name).isDirectory());
    }

    public ImageSet getAnimals(String name) {
        return animals.get(name);
    }

}
