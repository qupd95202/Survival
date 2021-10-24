package game.scene;

;

import game.Menu.ChooseRoleScene;
import game.controllers.SceneController;
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
    private boolean isServer;
//    private ArrayList<MapObject> unPassMapObjects;      連接Scene時，建構子時{set自己角色(new 角色)，加進players}，Scenebegin()，再sent自己創角的訊息出去，server在等人連接的畫面只要一直consume即可。
//    private ArrayList<TransformObstacle> transformObstacles;

    private static ConnectTool ct;

    public ConnectTool() {
        objectArr = new ObjectArr();
        isConnect = false;
        this.mainPlayers = new ArrayList<>();
        isServer = false;
    }

    public static ConnectTool instance() {
        if (ct == null) {
            ct = new ConnectTool();
        }
        return ct;
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
        game.network.Client.ClientClass.getInstance().connect(host, port);
        if (mainPlayer != null) {
            mainPlayer.setID(game.network.Client.ClientClass.getInstance().getID());
            mainPlayers.add(mainPlayer);
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
                                Player newPlayer;
                                if (serialNum == 100) {
                                    newPlayer = new Player(Integer.parseInt(strs.get(0)), Integer.parseInt(strs.get(1)), ChooseRoleScene.imgArrAndTypeParseInt(Integer.parseInt(strs.get(2))), Player.RoleState.HUNTER, strs.get(3));
                                } else {
                                    newPlayer = new Player(Integer.parseInt(strs.get(0)), Integer.parseInt(strs.get(1)), ChooseRoleScene.imgArrAndTypeParseInt(Integer.parseInt(strs.get(2))), Player.RoleState.PREY, strs.get(3));
                                }
                                newPlayer.setID(serialNum);
                                mainPlayers.add(newPlayer);
                                ClientClass.getInstance().sent(CONNECT, bale(strs.get(0), strs.get(1)));
                            }
                            break;
                        case COMPUTER_MOVE:
                            if (serialNum == 100) {
                                switch (Integer.parseInt(strs.get(0))) {
                                    case 0:
                                        objectArr.getComputerPlayersConnectPoint().get(0).translate(Integer.parseInt(strs.get(1)), Integer.parseInt(strs.get(2)));
                                    case 1:
                                        objectArr.getComputerPlayersConnectPoint().get(1).translate(Integer.parseInt(strs.get(1)), Integer.parseInt(strs.get(2)));
                                    case 2:
                                        objectArr.getComputerPlayersConnectPoint().get(2).translate(Integer.parseInt(strs.get(1)), Integer.parseInt(strs.get(2)));
                                }
                            }
                            break;
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
                            break;
                        case COMPUTER_MAINPLAYER_WHOISNEAR:
                            mainPlayers.forEach(player -> {
                                if (player.ID() == serialNum) {
                                    objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(0))).whoIsNear(player);
                                }
                            });
                            break;
                        case COMPUTER_WHOISNEAR:
                            objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(0))).whoIsNear(objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(1))));
                            break;
                        case PLAYER_COLLISION_PLAYER:
                            Player player1 = null;
                            Player player2 = null;
                            for (Player player : mainPlayers) {
                                if (player.ID() == Integer.parseInt(strs.get(0))) {
                                    player1 = player;
                                }
                                if (player.ID() == Integer.parseInt(strs.get(1))) {
                                    player2 = player;
                                }
                            }
                            if (player1 != null && player2 != null) {
                                player1.exchangeRoleInConnect(player2);
                            }
                            break;
                        case PLAYER_COLLISION_COMPUTER:
                            mainPlayers.forEach(player -> {
                                if (player.ID() == serialNum) {
                                    player.exchangeRole(objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(0))));
                                }
                            });
                            break;
                        case COMPUTER_COLLISION_COMPUTER:
                            objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(0))).exchangeRole(objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(1))));
                            break;
                        case PLAYER_COLLISION_PROPS:
                            mainPlayers.forEach(player -> {
                                if (player.ID() == serialNum) {
                                    for (int i = 0; i < objectArr.getPropsArrConnectPoint().size(); i++) {
                                        if (i == Integer.parseInt(strs.get(0))) {
                                            player.collideProps(objectArr.getPropsArrConnectPoint().get(i));
                                            objectArr.getPropsArrConnectPoint().get(i).setGotByPlayer(true);
                                            objectArr.getPropsArrConnectPoint().remove(i--);
                                        }
                                    }
//                                    Props props = objectArr.getPropsArrConnectPoint().get(Integer.parseInt(strs.get(0)));
//                                    player.collideProps(props);
//                                    props.setGotByPlayer(true);
//                                    objectArr.getPropsArrConnectPoint().remove(props);
                                }
                            });
                            break;
                        case COMPUTER_COLLISION_PROPS:
                            Props props = objectArr.getPropsArrConnectPoint().get(Integer.parseInt(strs.get(1)));
                            objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(0))).collideProps(props);
                            props.setGotByPlayer(true);
                            objectArr.getPropsArrConnectPoint().remove(props);
                            break;
                        case PLAYER_CHOOSE_TRANSFORM:
                            mainPlayers.forEach(player -> {
                                if (player.ID() == serialNum) {
                                    player.chooseTransformObject(ObjectArr.transformObstaclList1.get(Integer.parseInt(strs.get(0))));
                                }
                            });
                            break;
                        case PLAYER_USE_TELEPORTATION:
                            mainPlayers.forEach(player -> {
                                if (player.ID() == serialNum) {
                                    player.useTeleportation(Integer.parseInt(strs.get(0)), Integer.parseInt(strs.get(1)));
                                }
                            });
                            break;
                        case UPDATE_POSITION:
                            if (serialNum != ClientClass.getInstance().getID()) {
                                mainPlayers.forEach(player -> {
                                    if (player.ID() == serialNum) {
                                        player.setXY(Integer.parseInt(strs.get(0)), Integer.parseInt(strs.get(1)));
                                    }
                                });
                            }
                            break;
                        case COMPUTER_UPDATE_POSITION:
                            objectArr.getComputerPlayersConnectPoint().get(Integer.parseInt(strs.get(0))).setXY(Integer.parseInt(strs.get(1)), Integer.parseInt(strs.get(2)));
                            break;
                        case START_GAME:
                            SceneController.getInstance().change(new ConnectPointGameScene());
                            break;
                        case POINT_UPDATE:
                            mainPlayers.forEach(player -> {
                                if (player.ID() == Integer.parseInt(strs.get(0))) {
                                    player.setPoint(Integer.parseInt(strs.get(1)));
                                }
                            });
                            break;
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

    public static void reset() {
        ct = null;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public boolean isServer() {
        return isServer;
    }
}