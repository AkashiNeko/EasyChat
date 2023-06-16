package src;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @BelongsProject: EasyChat
 * @FileName: Logger
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 打印日志
 */

public class Logger {
    public static boolean printLog =  true;
    public static void print(String message) {
        if (printLog) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            System.out.println(formattedDateTime + " | " + message);
        }
    }
}
