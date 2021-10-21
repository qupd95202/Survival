package game.Menu;

import game.gameObj.GameObject;

import java.awt.*;

public class Label extends GameObject {
    private String words;
    private int size;
    private String typeName;
    public Label(int x,int y,String inputWords,int size,String typeName){
        super(x,y,0,0);
        this.words=inputWords;
        this.size=size;
        this.typeName=typeName;
    }
    public Label(int x,int y,String inputWords,int size){
        super(x,y,0,0);
        this.words=inputWords;
        this.size=size;
        this.typeName="";
    }


    @Override
    public void paintComponent(Graphics g) {
        Font font=new Font(typeName, Font.BOLD, size);
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString(words,painter().left(),painter().top());
    }

    @Override
    public void update() {

    }

    public void setWords(String words) {
        this.words = words;
    }
}
