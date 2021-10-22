package game.scene;

;

import game.core.Global;
import game.gameObj.Pact;
import game.gameObj.Props;
import game.gameObj.players.Player;
import game.graphic.AllImages;
import game.map.ObjectArr;
import game.network.Client.ClientClass;
import game.utils.GameKernel;
import network.Client.CommandReceiver;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static game.gameObj.Pact.*;

public class ConnectTool implements GameKernel.GameInterface {
    private boolean isConnect;
    private Player mainPlayer;
    private ArrayList<Player> mainPlayers;
    private ObjectArr objectArr;
//    private ArrayList<MapObject> unPassMapObjects;      連接Scene時，建構子時{set自己角色(new 角色)，加進players}，Scenebegin()，再sent自己創角的訊息出去，server在等人連接的畫面只要一直consume即可。
//    private ArrayList<TransformObstacle> transformObstacles;


    public ConnectTool() {
        objectArr = new ObjectArr();
        isConnect = false;
        this.mainPlayers = new ArrayList<>();
    }

    public void setIsConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public void createRoom(int port) {
        game.network.Server.Server.instance().create(port);
        game.network.Server.Server.instance().start();
    }

    public String[] serverInformation() { //伺服器的資訊
        return game.network.Server.Server.instance().getLocalAddress();
    }

    public void connect(String host, int port) throws IOException {
        System.out.println("use connect");
        game.network.Client.ClientClass.getInstance().connect(host, port);
        if (mainPlayer != null) {
            System.out.println("mainPlayer");
            mainPlayer.setID(game.network.Client.ClientClass.getInstance().getID());
            mainPlayers.add(mainPlayer);
            System.out.println("connect Add");
        }
        isConnect = true;
    }

    public Player getSelf() {
        return mainPlayer;
    }

    public void setMainPlayer(Player mainPlayer) {
        this.mainPlayer = mainPlayer;
    }

    public ArrayList<Player> getMainPlayers() {
        return mainPlayers;
    }

    public ObjectArr getObjectArr() {
        return objectArr;
    }

    public void clear() {
        mainPlayer = null;
        mainPlayers = null;
    }

    public void consume() {
        if (isConnect) {
            game.network.Client.ClientClass.getInstance().consume(new CommandReceiver() {
                @Override
                public void receive(int serialNum, int commandCode, ArrayList<String> strs) {
                    switch (commandCode) {
                        case DISCONNECT:
                            for (int i = 1; i < mainPlayers.size(); i++) {
                                if (mainPlayers.get(i).ID() == serialNum) {
                                    mainPlayers.remove(i);
                                    break;
                                }
                            }
                            break;

                        case CONNECT: ///這個要最先做 ==> 還要有很多Scene創房間 輸入等等
                            boolean isburn = false;
                            for (int i = 0; i < mainPlayers.size(); i++) {
                                if (mainPlayers.get(i).ID() == serialNum) {
                                    isburn = true;
                                    break;
                                }
                            }
                            if (!isburn) {
                                Player newPlayer = new Player(Global.SCREEN_X / 2, Global.SCREEN_Y / 2, AllImages.blue, Player.RoleState.PREY);
                                newPlayer.setID(serialNum);
                                mainPlayers.add(newPlayer);
                                ClientClass.getInstance().sent(Pact.CONNECT, bale());
                            }
                            break;
                        case COMPUTER_MOVE:

                        case UP:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyPressed(Global.KeyCommand.UP.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case RELEASE_UP:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyReleased(Global.KeyCommand.UP.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case DOWN:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyPressed(Global.KeyCommand.DOWN.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case RELEASE_DOWN:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyReleased(Global.KeyCommand.DOWN.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case LEFT:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyPressed(Global.KeyCommand.LEFT.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case RELEASE_LEFT:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyReleased(Global.KeyCommand.LEFT.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case RIGHT:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyPressed(Global.KeyCommand.RIGHT.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case RELEASE_RIGHT:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyReleased(Global.KeyCommand.RIGHT.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case TRANSFORM:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyPressed(Global.KeyCommand.TRANSFORM.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case TELEPORTATION:
                            mainPlayers.forEach(player -> {
                                if (serialNum == player.ID()) {
                                    player.keyPressed(Global.KeyCommand.TELEPORTATION.getValue(), Long.parseLong(strs.get(0)));
                                }
                            });
                            break;
                        case PROPS_GEN:
                            if (serialNum == 100) {
                                objectArr.getPropsArrConnectPoint().add(new Props(Integer.parseInt(strs.get(0)), Integer.parseInt(strs.get(1)), Props.propsTypeParse(strs.get(2))));
                            }
                    }
                }
            });
        }
    }

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < mainPlayers.size(); i++) {
            mainPlayers.get(i).paint(g);
        }
    }

    @Override
    public void update() {
        for (int i = 0; i < mainPlayers.size(); i++) {
            mainPlayers.get(i).update();
        }
    }
}