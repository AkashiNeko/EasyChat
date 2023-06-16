package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @BelongsProject: EasyChat
 * @FileName: UserThread
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 保持和用户持续连接的线程，负责在指定端口与用户通信
 */

public class UserThread extends Thread {
    private ServerSocket serverSocket;
    private Socket socket;
    private final int port;
    private final String username;
    public static final Map<String, PrintWriter> onlineUsers = new HashMap<>();
    public static boolean isOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    public UserThread(int port, String username) {
        this.port = port;
        this.username = username;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Logger.print(port + ": " + e.getMessage());
        }
    }
    
    public void run() {
        ReceiveEvent receiveEvent = null;
        try {
            socket = serverSocket.accept();
            Logger.print("\033[32mclient login: user \"" + username + "\" login, port=" + port + "\033[0m");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            onlineUsers.put(username, printWriter);
            receiveEvent = new ReceiveEvent();

            List<String> onlineFriends = receiveEvent.getOnlineFriends(username);
            Logger.print("debug: " + onlineFriends);
            if (onlineFriends != null) {
                for (String onlineFriend : onlineFriends) {
                    PrintWriter pw = onlineUsers.get(onlineFriend);
                    String sendMsg = "server:online:" + username;
                    pw.println(sendMsg);
                    Logger.print("\033[34m" + username + " -> " + onlineFriend + ": \"" + sendMsg + "\"\033[0m");
                }
            }
            while (true) {
                String msg = bufferedReader.readLine();
                Logger.print(port + ": \"" + msg + "\"");
                if (msg == null || msg.equals("quit")) break;
                String executeResult = receiveEvent.execute(msg);
                Logger.print("\033[36mreceive: " + executeResult + "\033[0m");
                printWriter.println(executeResult);
            }
        } catch (Exception e) {
            Logger.print(port + ": " + e.getMessage());
        } finally {
            List<String> onlineFriends = null;
            if (receiveEvent != null)
                onlineFriends = receiveEvent.getOnlineFriends(username);
            if (onlineFriends != null) {
                for (String onlineFriend : onlineFriends) {
                    PrintWriter pw = onlineUsers.get(onlineFriend);
                    String sendMsg = "server:offline:" + username;
                    pw.println(sendMsg);
                    Logger.print("\033[34m" + username + " -> " + onlineFriend + ": \"" + sendMsg + "\"\033[0m");
                }
            }
            onlineUsers.remove(username);
            try {
                socket.close();
                serverSocket.close();
                if (receiveEvent != null)
                    receiveEvent.close();
                PortPool.releasePort(port);
                Logger.print("\033[33mclient close: user \"" + username + "\" offline. port=" + port + "\033[0m");
            } catch (Exception e) {
                Logger.print(port + ": " + e.getMessage());
            }
        }
    }
}
