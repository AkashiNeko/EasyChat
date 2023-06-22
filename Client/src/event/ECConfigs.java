package src.event;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @BelongsProject: EasyChat
 * @FileName: LocalConfig
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 本地记录用户登录信息，便于下次直接登录
 */
public class ECConfigs {
    private static final String CONFIG_FILE_PATH = "C:\\Users\\%s\\easychat.cfg";
    private static String hostName;
    private static int port;
    public static String getConfig(String config) {
        String username = System.getProperty("user.name");
        String filePath = String.format(CONFIG_FILE_PATH, username);
        Properties props = new Properties();
        try {
            // 创建文件输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 读取属性列表
            props.load(fis);
            // 关闭文件输入流
            fis.close();
            return props.getProperty(config);
        } catch (IOException e) {
            return null;
        }
    }
    public static void setConfig(String config, String value) {
        String username = System.getProperty("user.name");
        String filePath = String.format(CONFIG_FILE_PATH, username);
        Properties props = new Properties();
        try {
            // 创建文件输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 读取属性列表
            props.load(fis);
            // 关闭文件输入流
            fis.close();
            // 设置属性值
            props.setProperty(config, value);
            // 创建文件输出流
            FileOutputStream fos = new FileOutputStream(filePath);
            // 将属性写入文件
            props.store(fos, "EasyChat");
            // 关闭文件输出流
            fos.close();
        } catch (IOException ignored) {}
    }
}
