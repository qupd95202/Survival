package game.Menu;

import game.gameObj.GameObject;
import game.gameObj.Rect;

import java.awt.*;

public class Label extends GameObject {
    private String words;
    private Font font;
    private Color color;
    public Label(int x,int y,String inputWords,Font font){
        super(x,y,0,0);
        this.words=inputWords;
        this.font=font;
        this.color=Color.WHITE;
    }
    public Label(int x,int y,String inputWords){
        super(x,y,0,0);
        this.words=inputWords;
        this.font=FontLoader.High(20);
        this.color=Color.WHITE;
    }
    public Label(int x,int y,String inputWords,Font font,Color color){
        super(x,y,0,0);
        this.words=inputWords;
        this.font=font;
        this.color=color;
    }
    public Label(int x,int y,String inputWords,Color color){
        super(x,y,0,0);
        this.words=inputWords;
        this.color=color;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(words,painter().left(),painter().top());
    }

    @Override
    public void update() {

    }

    public void setWords(String words) {
        this.words = words;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return  words ;
    }
}
