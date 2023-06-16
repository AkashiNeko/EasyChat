package src.window;

import src.networker.Connector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @BelongsProject: EasyChat
 * @FileName: DeleteFriendWindow
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 删除好友窗口类
 */
public class DeleteFriendWindow extends JFrame {

    private JTextField textField;
    Connector connector;

    private final String username;

    private final String passwordMD5;

    public DeleteFriendWindow(Connector connector, String username, String passwordMD5) {
        this.connector = connector;
        this.username = username;
        this.passwordMD5 = passwordMD5;
    }

    public void showDeleteFriendWindow(MainWindow mainWindow) {
        this.setTitle("删除好友");
        this.setResizable(false);

        // 创建面板和布局管理器
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30,30,30));
        panel.setForeground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 创建标签、文本输入框、添加按钮和取消按钮
        JLabel label = new JLabel("好友名 ");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);

        textField = new JTextField(20);
        textField.setFont(new Font("Courier New", Font.PLAIN, 16));
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(100,100,100));
        textField.setText(mainWindow.getSelectFriend());
        textField.setMargin(new Insets(0, 5, 0, 5));
        textField.addActionListener(e -> deleteFriendButtonPushed(mainWindow));

        JButton deleteButton = new JButton("删除");
        deleteButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.BLACK);
        deleteButton.setPreferredSize(new Dimension(120, 30));
        deleteButton.setMinimumSize(new Dimension(120, 30));
        deleteButton.setMaximumSize(new Dimension(120, 30));
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deleteButton.setBackground(Color.GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                deleteButton.setBackground(Color.BLACK);
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.BLACK);
        cancelButton.setPreferredSize(new Dimension(120, 30));
        cancelButton.setMinimumSize(new Dimension(120, 30));
        cancelButton.setMaximumSize(new Dimension(120, 30));
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(Color.GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(Color.BLACK);
            }
        });

        // 创建面板和布局管理器
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30,30,30));
        buttonPanel.setForeground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        // 将标签、文本输入框和面板添加到面板中
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(30,30,30));
        inputPanel.setForeground(Color.WHITE);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(label, BorderLayout.WEST);
        inputPanel.add(textField, BorderLayout.CENTER);

        // 为面板添加内边距，四边分别留出10像素的空白
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(inputPanel);
        panel.add(buttonPanel);

        // 添加事件监听器
        deleteButton.addActionListener(e -> deleteFriendButtonPushed(mainWindow));

        cancelButton.addActionListener(e -> dispose());

        // 将面板添加到窗口中
        getContentPane().add(panel, BorderLayout.CENTER);

        // 设置窗口大小、位置和可见性
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteFriendButtonPushed(MainWindow mainWindow) {
        String friend = textField.getText().trim();
        if (friend.isEmpty()) {
            // 输入内容为空
            JOptionPane.showMessageDialog(DeleteFriendWindow.this, "请输入用户名");
        } else if (friend.equals(username)) {
            // 添加的是自己
            JOptionPane.showMessageDialog(DeleteFriendWindow.this, "不能删除自己");
        } else if (!checkUserExist(friend) || !isFriend(friend)) {
            // 用户名不存在
            JOptionPane.showMessageDialog(DeleteFriendWindow.this, "你和 " + friend + " 不是好友哦，请检查是否输入正确");
        } else {
            // 删除好友
            mainWindow.deleteFriend(friend, true);
            dispose();
        }
    }

    private boolean isFriend(String user) {
        String result = connector.sendRequest("isfriend:" + passwordMD5 + ":" + username + ":" + user);
        return result.equals("true");
    }

    private boolean checkUserExist(String user) {
        // TODO: 检查用户名是否存在
        String result = connector.sendRequest("userexist:" + passwordMD5 + ":" + username + ":" + user);
        return result.equals("true");
    }


}
