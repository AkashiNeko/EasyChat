package src.networker;
import java.io.*;
import java.net.Socket;

/**
 * @BelongsProject: EasyChat
 * @FileName: Sender
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 向服务器发送登录和注册请求
 */
public class Sender {
    private static String serverAddress;
    private static int port;
    public Sender() {}

    public static void setHost(String host) {
        Sender.serverAddress = host;
    }

    public static void setPort(int port) {
        Sender.port = port;
    }

    public String send(String msg) {
        try {
            Socket socket = new Socket(serverAddress, port);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter.println(msg);
            String ret = bufferedReader.readLine();
            socket.close();
            bufferedReader.close();
            return ret;
        } catch (IOException e) {
            return "error:" + e.getMessage();
        }
    }
}
