package src.networker;

import src.window.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * @BelongsProject: EasyChat
 * @FileName: Connector
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 和服务器保持连接
 */
public class Connector {
    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    MsgReaderThread msgReaderThread;
    static String host;

    public static void setHost(String host) {
        Connector.host = host;
    }

    public Connector(int port) throws IOException {
        socket = new Socket(host, port);
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        msgReaderThread = new MsgReaderThread(bufferedReader);
        msgReaderThread.start();
    }

    public void setMainWindow(MainWindow mainWindow) {
        msgReaderThread.setMainWindow(mainWindow);
    }

    public String sendRequest(String msg) {
        printWriter.println(msg);
        BlockingQueue<String> buffer = msgReaderThread.getBuffer();
        try {
            return buffer.take();
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    public void close() {
        try {
            socket.close();
            bufferedReader.close();
            msgReaderThread.stopThread();
        }
        catch (IOException ignored) {}
    }
}
