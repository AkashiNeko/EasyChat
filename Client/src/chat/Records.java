package src.chat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: EasyChat
 * @FileName: Records
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 聊天记录
 */
public class Records {
    List<TextMsg> records;
    public Records() {
        records = new ArrayList<TextMsg>();
    }
    public void add(TextMsg msg) {
        records.add(msg);
    }

    public String toString() {
        if (records.isEmpty())
            return "";
        StringBuilder result = new StringBuilder();
        for (TextMsg msg : records)
            result.append(msg.toString());
        return result.toString();
    }

    public String getLastTime() {
        if (records.isEmpty())
            return "2000_01_01_00_00_00";
        return records.get(records.size() - 1).getDateTime().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
    }
}
