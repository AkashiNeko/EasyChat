package src;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @BelongsProject: EasyChat
 * @FileName: APP
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 端口池，端口管理
 */

public class PortPool {
    private static final Set<Integer> usedPorts = new HashSet<>();

    private PortPool() {}

    public static int getRandomPort() {
        while (true) {
            int port = new Random().nextInt(1000) + 55001;
            if (!usedPorts.contains(port)) {
                try (ServerSocket ignored = new ServerSocket(port)) {
                    usedPorts.add(port);
                    return port;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void releasePort(int port) {
        usedPorts.remove(port);
    }
}
