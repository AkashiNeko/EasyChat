package src.window;

import src.event.ECConfigs;
import src.networker.Connector;
import src.networker.Sender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @BelongsProject: EasyChat
 * @FileName: ServerWindow
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 服务器地址窗口
 */
public class ServerWindow {
    JFrame serverFrame;
    JTextField serverAddrField;
    JTextField portField;
    public ServerWindow() {
        serverFrame = new JFrame("连接服务器");
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.setSize(410, 230);
        serverFrame.setResizable(false);

        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(null);
        serverPanel.setBackground(new Color(25, 25, 25));

        // 服务器地址标签
        JLabel serverAddrLabel = new JLabel("服务器");
        serverAddrLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        serverAddrLabel.setForeground(Color.WHITE);
        serverAddrLabel.setBounds(30, 25, 60, 35);
        serverPanel.add(serverAddrLabel);

        // 服务器地址输入框
        serverAddrField = new JTextField(30);
        serverAddrField.setFont(new Font("Courier New", Font.PLAIN, 20));
        serverAddrField.setBounds(110, 25, 250, 38);
        serverAddrField.setForeground(Color.WHITE);
        serverAddrField.setBackground(new Color(100,100,100));
        serverAddrField.getCaret().setBlinkRate(300);
        serverAddrField.setMargin(new Insets(0, 5, 0, 5));

        serverPanel.add(serverAddrField);

        // 端口标签
        JLabel portLabel = new JLabel("端　口");
        portLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        portLabel.setForeground(new Color(255, 255, 255));
        portLabel.setBounds(30, 85, 60, 35);
        serverPanel.add(portLabel);

        // 端口输入框
        portField = new JTextField(30);
        portField.setFont(new Font("Courier New", Font.PLAIN, 20));
        portField.setBounds(110, 85, 250, 38);
        portField.setForeground(Color.WHITE);
        portField.setBackground(new Color(100,100,100));
        portField.getCaret().setBlinkRate(300);
        portField.setMargin(new Insets(0, 5, 0, 5));
        serverPanel.add(portField);

        JButton button = new JButton("连接");
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        button.setBounds(110, 145, 250, 36);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK);
            }
        });
        button.addActionListener(e -> connectServer());

        serverPanel.add(button);

        serverFrame.add(serverPanel);
        serverFrame.setVisible(true);

        String localServer = ECConfigs.getConfig("server");
        String localPort = ECConfigs.getConfig("port");
        if (localServer == null || localPort == null) {
            String defaultServer = "neko.akashi.top";
            String defaultPort = "55000";
            ECConfigs.setConfig("server", defaultServer);
            ECConfigs.setConfig("port", defaultPort);
            serverAddrField.setText(defaultServer);
            portField.setText(defaultPort);
        } else {
            serverAddrField.setText(localServer);
            portField.setText(localPort);
        }
    }
    public void connectServer() {
        String host = serverAddrField.getText();
        int port = Integer.parseInt(portField.getText());
        Sender.setHost(host);
        Sender.setPort(port);
        Sender sender = new Sender();
        String result = sender.send("check:online");
        if (result == null || !result.equals("true")) {
            JOptionPane.showMessageDialog(null, "无法连接至服务器，请检查服务器地址和端口是否输入正确", "无法连接至服务器", JOptionPane.ERROR_MESSAGE);
            System.out.println(result);
            return;
        }
        Connector.setHost(host);
        serverFrame.dispose();
        new LoginWindow();
    }
    public static void main(String[] args) {
        new ServerWindow();
    }
}
