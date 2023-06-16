package src.chat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @BelongsProject: EasyChat
 * @FileName: TextMsg
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 文本消息
 */

public class TextMsg {
    String username;
    LocalDateTime dateTime;
    String message;
    public TextMsg(String username, String message, String dateTimeString) {
        this.username = username;
        this.message = message;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        this.dateTime = LocalDateTime.parse(dateTimeString, formatter);
    }
    public String getMessage() {
        return message;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String toString() {
        if (message.isEmpty()) return "";
        String dateTime = this.dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        return dateTime + " | " + username + "\n> " + getMessage() + '\n';
    }
}
