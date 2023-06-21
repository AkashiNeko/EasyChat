package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: EasyChat
 * @FileName: Server
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 服务器监听
 */

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;                
    private final ReceiveEvent receiveEvent = new ReceiveEvent();
    private boolean stopServer = false;
    private final int port;
    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() { 
        while (!stopServer) {
            try {
                socket = serverSocket.accept();
                Logger.println("\033[32mclient connected\033[0m");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                String msg = bufferedReader.readLine();
                if (msg != null) {
                    Logger.println("port:" + port + " \"" + msg + "\"");
                    String executeResult = receiveEvent.execute(msg);
                    Logger.println("\033[36mreceive: " + executeResult + "\033[0m");
                    printWriter.println(executeResult);
                }
            } catch (Exception e) {
                Logger.println("server error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.println("client close failed: " + e.getMessage());
                }
                Logger.println("\033[33mclient closed\033[0m");
            }
        }
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        stopServer = true;
    }
}
