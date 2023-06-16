package src.window;

import src.networker.Connector;
import src.chat.FriendInfo;
import src.chat.TextMsg;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @BelongsProject: EasyChat
 * @FileName: MainWindow
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 主窗口
 */

public class MainWindow extends JFrame {
    public JPanel leftPanel, rightPanel, inputPanel, friendPanel;
    public JScrollPane friendListPanel;
    JTextArea chatArea;
    JTextField inputField;
    private Connector connector = null;
    private final String username;
    private final String passwordMD5;
    public String selectFriend = "";
    public Map<String, FriendInfo> friendMap = new HashMap<>();
    public Set<String> onlineFriend = new HashSet<>();

    public MainWindow(int port, String username, String passwordMD5) {
        this.username = username;
        this.passwordMD5 = passwordMD5;
        try {
            connector = new Connector(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "登录失败，请检查您的网络", "登录失败", JOptionPane.ERROR_MESSAGE);
        }
        connector.setMainWindow(this);
        Image icon = new ImageIcon("images/logo.ico").getImage();
        this.setIconImage(icon);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // 关闭窗口时
                connector.sendRequest("quit");
                connector.close();
            }
        });
        initUI();
    }
    public void setSelectFriend(String selectFriend) {
        this.selectFriend = selectFriend;
    }
    public String getSelectFriend() {
        return selectFriend;
    }
    private void initUI() {
        setTitle(username + " - EasyChat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        String friendListString = connector.sendRequest("getfriendlist:" + passwordMD5 + ":" + username);
        String[] friendListSplit = friendListString.split(":");
        if (!friendListSplit[0].equals("friendlist"))
            JOptionPane.showMessageDialog(this, "获取好友列表失败", "错误", JOptionPane.ERROR_MESSAGE);
        String[] friendList = new String[friendListSplit.length - 1];
        for (int i = 1; i < friendListSplit.length; ++i) {
            if (friendListSplit[i].charAt(0) == '*') {
                String friend = friendListSplit[i].substring(1);
                friendList[i - 1] = friend;
                onlineFriend.add(friend);
            } else {
                friendList[i - 1] = friendListSplit[i];
            }
        }

        //整体布局
        setLayout(new BorderLayout());

        //好友列表滚动面板
        friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        friendPanel.setBackground(Color.BLACK);
        friendListPanel = new JScrollPane(friendPanel);
        // 创建自定义的ScrollBarUI
        ScrollBarUI ui = new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
            this.thumbColor = new Color(60, 60, 60);
            this.trackColor = new Color(40, 40, 40);
            }
        };
        friendListPanel.getVerticalScrollBar().setUI(ui);

        friendListPanel.setBackground(Color.BLACK);
        friendListPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        friendListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 好友按钮
        for (String friend : friendList)
            addButton(friend);

        JPanel ctrlFriendPanel = new JPanel();
        ctrlFriendPanel.setLayout(new BoxLayout(ctrlFriendPanel, BoxLayout.X_AXIS));
        ctrlFriendPanel.setBackground(Color.BLACK);

        // 添加好友
        JButton addFriendButton = getButton("添加好友");
        addFriendButton.addActionListener(e -> {
            // TODO 添加好友
            AddFriendWindow addFriendWindow = new AddFriendWindow(connector, username, passwordMD5);
            addFriendWindow.showAddFriendWindow(this);
        });

        // 删除好友
        JButton deleteFriendButton = getButton("删除好友");
        deleteFriendButton.addActionListener(e -> {
            // TODO 删除好友
            DeleteFriendWindow deleteFriendWindow = new DeleteFriendWindow(connector, username, passwordMD5);
            deleteFriendWindow.showDeleteFriendWindow(this);
        });

        //左侧好友列表
        leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // 个人信息按钮
        // JPanel selfPanel = new JPanel();
        // selfPanel.setLayout(new BoxLayout(selfPanel, BoxLayout.X_AXIS));
        // JButton selfButton = new JButton("个人信息");
        // selfButton.setBackground(new Color(44, 64, 104));
        // selfButton.setForeground(new Color(198, 217, 255));
        // selfButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        // selfButton.setPreferredSize(new Dimension(200, 30));
        // selfButton.setMinimumSize(new Dimension(200, 30));
        // selfButton.setMaximumSize(new Dimension(200, 30));
        // selfButton.addActionListener(null);
        // selfPanel.add(selfButton);

        // 个人信息
        // leftPanel.add(selfPanel);
        // 好友列表
        leftPanel.add(friendListPanel);
        // 好友操作按钮
        ctrlFriendPanel.add(addFriendButton);
        ctrlFriendPanel.add(deleteFriendButton);
        leftPanel.add(ctrlFriendPanel);

        //右侧聊天界面
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setLayout(new BorderLayout());

        //聊天消息显示区域
        chatArea = new JTextArea();
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane chatAreaScrollPane = new JScrollPane(chatArea);
        chatAreaScrollPane.getVerticalScrollBar().setUI(ui);
        chatArea.setEditable(false);
        chatArea.setBackground(Color.BLACK);
        chatArea.setForeground(Color.WHITE);
        chatArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(chatScrollPane, BorderLayout.CENTER);
        //消息输入框和发送按钮
        inputPanel = new JPanel();
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setLayout(new BorderLayout());
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        inputField = new JTextField();
        inputField.setMargin(new Insets(0, 5, 0, 5));

        for (FriendInfo friendInfo : friendMap.values()) {
            friendInfo.button.addActionListener(e -> inputField.requestFocus());
        }
        inputField.addActionListener(e -> sendMessage());
        inputField.setBackground(new Color(60, 60, 60));
        inputField.setForeground(Color.WHITE);
        inputField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        inputPanel.add(inputField, BorderLayout.CENTER);

        JButton sendButton = new JButton("发送");
        sendButton.setBackground(Color.BLACK);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        sendButton.setPreferredSize(new Dimension(80, 30));
        sendButton.setMinimumSize(new Dimension(80, 30));
        sendButton.setMaximumSize(new Dimension(80, 30));
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        //分割线
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);
    }
    //发送消息
    private void sendMessage() {
        if (selectFriend.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请在左侧选择要聊天的好友");
            return;
        }
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "不能发送空内容！");
            return;
        }
        // base64
        byte[] bytes;
        bytes = message.getBytes(StandardCharsets.UTF_8);
        String base64msg = Base64.getEncoder().encodeToString(bytes);
        String ret = connector.sendRequest("sendmessage:" + passwordMD5 + ":" + username + ":" + selectFriend + ":" + base64msg);
        String[] args = ret.split(":");
        if (args.length == 2 && args[0].equals("true")) {
            TextMsg msg = new TextMsg(username, message, args[1]);
            addMessage(selectFriend, msg);
            inputField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "发送失败");
        }
    }
    public void addFriend(String friend, boolean check) {
        // TODO: 执行添加好友的操作
        String result = check ? connector.sendRequest("addfriend:" + passwordMD5 + ":" + username + ":" + friend) : "true";
        // 添加好友成功后，关闭该窗口
        if (result.equals("true")) {
            addButton(friend);
            friendPanel.revalidate();
            friendPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败");
        }
    }
    public void deleteFriend(String friend, boolean check) {
        // TODO: 执行添加好友的操作
        String result = check ? connector.sendRequest("deletefriend:" + passwordMD5 + ":" + username + ":" + friend) : "true";
        // 添加好友成功后，关闭该窗口
        if (result.equals("true")) {
            JButton button = friendMap.get(friend).getButton();
            friendMap.remove(friend);
            friendPanel.remove(button);
            friendPanel.revalidate();
            friendPanel.repaint();
            if (friend.equals(selectFriend))
                setSelectFriend("");
        }
        else {
            JOptionPane.showMessageDialog(this, "删除失败");
        }
    }
    public void addButton(String friend) {
        JButton friendButton = new JButton(friend);
        friendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        friendButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        friendButton.setPreferredSize(new Dimension(180, 40));
        friendButton.setMinimumSize(new Dimension(180, 40));
        friendButton.setMaximumSize(new Dimension(180, 40));
        friendButton.setForeground(Color.WHITE);
        friendButton.setBackground(new Color(20,20,20));
        friendButton.setForeground(isOnline(friend) ? Color.GREEN : Color.WHITE);
        friendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (friendButton.isEnabled())
                    friendButton.setBackground(new Color(60, 63, 64));
                friendButton.setForeground(isOnline(friend) ? Color.GREEN : Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                friendButton.setBackground(friendButton.isEnabled() ? new Color(20,20,20) : new Color(13, 115, 184));
                friendButton.setForeground(isOnline(friend) ? Color.GREEN : Color.WHITE);
            }
        });
        friendButton.addActionListener(e -> {
            if (!selectFriend.isEmpty()) {
                if (selectFriend.equals(friend)) return;
                JButton selectedButton = friendMap.get(selectFriend).getButton();
                selectedButton.setBackground(new Color(20,20,20));
                selectedButton.setEnabled(true);
                friendMap.get(selectFriend).inputBuffer = inputField.getText();
            }
            friendButton.setEnabled(false);
            friendButton.setBackground(new Color(9,71,112));
            selectFriend = friend;
            chatArea.setText(friendMap.get(friend).getRecords().toString());
            inputField.setText(friendMap.get(friend).getInputBuffer());

            //TODO: 点击好友后的操作
            // System.out.println(friend);
            FriendInfo friendInfo = friendMap.get(friend);
            String lastTime = friendInfo.records.getLastTime();
            String ret = connector.sendRequest("getrecords:" + passwordMD5 + ":" + username + ":" + friend + ":" + lastTime);
            // System.out.println(ret);
            if (!ret.equals("records:null")) {
                String[] records = ret.split(":");
                for (int i = 1; i < records.length; ++i) {
                    String[] record = records[i].split(",");
                    String msg;
                    byte[] decoded = Base64.getDecoder().decode(record[2]);
                    msg = new String(decoded, StandardCharsets.UTF_8);
                    TextMsg textMsg = new TextMsg(record[0], msg, record[1]);
                    friendMap.get(selectFriend).getRecords().add(textMsg);
                    chatArea.append(textMsg.toString());
                }
            }
        });
        friendPanel.add(friendButton);
        friendMap.put(friend, new FriendInfo(friendButton));
    }
    public void addMessage(String friend, TextMsg msg) {
        friendMap.get(friend).getRecords().add(msg);
        if (friend.equals(selectFriend)) {
            chatArea.append(msg.toString());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } else {
            friendMap.get(friend).button.setForeground(Color.YELLOW);
        }
    }

    public boolean isOnline(String friend) {
        return onlineFriend.contains(friend);
    }

    private JButton getButton(String buttonName) {
        JButton button = new JButton(buttonName);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(100, 30));
        button.setMinimumSize(new Dimension(100, 30));
        button.setMaximumSize(new Dimension(100, 30));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(20,20,20));
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
        return button;
    }
}
