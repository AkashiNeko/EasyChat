package src.chat;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

/**
 * @BelongsProject: EasyChat
 * @FileName: SystemAlert
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 系统提示
 */
public class SystemAlert {
    public static void alert(String caption, String text) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("images/图标.png");
            TrayIcon trayIcon = new TrayIcon(image, "SystemTray");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("SystemTray");
            try { tray.add(trayIcon); }
            catch (AWTException ignored) {}
            trayIcon.displayMessage(caption, text, MessageType.INFO);
        }
    }
}
