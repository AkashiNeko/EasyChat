package src.networker;
import src.chat.SystemAlert;
import src.chat.TextMsg;
import src.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @BelongsProject: EasyChat
 * @FileName: MsgReaderThread
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 接收并处理来自服务器端消息的线程
 */
public class MsgReaderThread extends Thread {
    BlockingQueue<String> buffer = new ArrayBlockingQueue<>(32);
    BufferedReader reader;
    String username;
    MainWindow mainWindow;
    private volatile boolean stopThread = false;
    public MsgReaderThread(BufferedReader bufferedReader) {
        this.reader = bufferedReader;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
    public BlockingQueue<String> getBuffer() {
        return buffer;
    }
    public void run() {
        while (!stopThread) {
            String msg;
            try {
                msg = reader.readLine();
                if (msg == null) {
                    buffer.put("null");
                } else if (msg.startsWith("server")) {
                    messageByServer(msg);
                } else {
                    buffer.put(msg);
                }
            } catch (Exception e) {
                // 掉线直接退出
                JOptionPane.showMessageDialog(null,
                        "与服务器断开连接..", "连接断开", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }
    }
    public void messageByServer(String msg) {
        // System.out.println(msg);
        String[] args = msg.split(":");
        switch (args[1]) {
        case "receive":
            receive(args[2], args[3], args[4]);
            break;
        case "addfriend":
            addFriend(args[2]);
            break;
        case "deletefriend":
            deleteFriend(args[2]);
            break;
        case "online":
            friendLogin(args[2]);
            break;
        case "offline":
            friendOffline(args[2]);
            break;
        default:
            break;
        }
    }
    public void receive(String srcUser, String dateTime, String base64msg) {
        byte[] decoded = Base64.getDecoder().decode(base64msg);
        String msg = new String(decoded, StandardCharsets.UTF_8);
        TextMsg textMsg = new TextMsg(srcUser, msg, dateTime);
        mainWindow.addMessage(srcUser, textMsg);
    }
    public void addFriend(String srcUser) {
        SystemAlert.alert(username, srcUser + " 已添加你为好友");
        mainWindow.addFriend(srcUser, false);
    }
    public void deleteFriend(String srcUser) {
        SystemAlert.alert(username, "好友 " + srcUser + " 删除了你");
        mainWindow.deleteFriend(srcUser, false);
    }

    public void friendLogin(String friend) {
        SystemAlert.alert(username, "好友 " + friend + " 上线了");
        mainWindow.onlineFriend.add(friend);
        mainWindow.friendMap.get(friend).getButton().setForeground(Color.GREEN);
    }

    public void friendOffline(String friend) {
        SystemAlert.alert(username, "好友 " + friend + " 下线了");
        mainWindow.onlineFriend.remove(friend);
        mainWindow.friendMap.get(friend).getButton().setForeground(Color.WHITE);
    }
    public void stopThread() {
        stopThread = true;
    }
}
