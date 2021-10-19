package game.Menu;

import game.gameObj.GameObject;

import java.awt.*;

public class Label extends GameObject {
    private String words;
    private int size;
    public Label(int x,int y,String inputWords,int size){
        super(x,y,0,0);
        this.words=inputWords;
        this.size=size;
    }

    @Override
    public void paintComponent(Graphics g) {
        Font font=new Font("", Font.BOLD, size);
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString(words,painter().left(),painter().top());
    }

    @Override
    public void update() {

    }
}
