package src;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @BelongsProject: EasyChat
 * @FileName: UserDAO
 * @Author: Akashi
 * @Version: 1.1
 * @Description: 数据库访问类
 */

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        String DB_URL = DatabaseConfig.getURL();
        String DB_USER = DatabaseConfig.getUsername();
        String DB_PASSWORD = DatabaseConfig.getPassword();
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Logger.println("Database connected: UserName=" + DB_USER);
        } catch (Exception e) {
            Logger.println("Database connect failed: " + e.getMessage());
            System.exit(-1);
        }
    }

    public boolean userExists(String user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM user WHERE username = ?");
        statement.setString(1, user);
        ResultSet result = statement.executeQuery();
        result.next();
        int count = result.getInt(1);
        return count > 0;
    }

    public void userRegister(String username, String password, String email) throws SQLException {
        // 查询当前用户总数
        String querySql = "SELECT num FROM total_users";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(querySql);
        rs.next();
        int totalUsers = rs.getInt("num");
        rs.close();
        stmt.close();
        // 构造 SQL 语句
        String eid = String.format("%08d", totalUsers + 1);
        String sql = "INSERT INTO user (eid, username, password, email) VALUES (?, ?, ?, ?)";
        // 创建 PreparedStatement 对象
        PreparedStatement pstmt = connection.prepareStatement(sql);
        // 设置参数
        pstmt.setString(1, eid);
        pstmt.setString(2, username);
        pstmt.setString(3, password);
        pstmt.setString(4, email);
        // 执行 SQL 语句
        int rows = pstmt.executeUpdate();
        // 如果受影响的行数大于 0，说明插入成功
        if (rows > 0) {
            // 更新用户总数
            String updateSql = "UPDATE total_users SET num = ?";
            PreparedStatement updatePstmt = connection.prepareStatement(updateSql);
            updatePstmt.setInt(1, totalUsers + 1);
            updatePstmt.executeUpdate();
            updatePstmt.close();
        }
    }
    public boolean checkUserPassword(String user, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT password FROM user WHERE username= ?");
        statement.setString(1, user);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            String actualPassword = result.getString(1);
            return !actualPassword.equals(password);
        }
        return true;
    }

    public String getFriendList(String username) throws SQLException {
        List<String> friends = null;
        
        // Find eid for the given username
        PreparedStatement stmt1 = connection.prepareStatement("SELECT eid FROM user WHERE username = ?");
        stmt1.setString(1, username);
        ResultSet rs1 = stmt1.executeQuery();
        if (rs1.next()) {
            String eid = rs1.getString("eid");
            friends = new ArrayList<>();

            PreparedStatement stmt_eid1 = connection.prepareStatement("SELECT eid2 FROM friendship WHERE eid1 = ?");
            stmt_eid1.setString(1, eid);
            ResultSet rs_eid1 = stmt_eid1.executeQuery();
            while (rs_eid1.next()) {
                String friendEid = rs_eid1.getString("eid2");
                PreparedStatement stmt = connection.prepareStatement("SELECT username FROM user WHERE eid = ?");
                stmt.setString(1, friendEid);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String friendUsername = rs.getString("username");
                    if (UserThread.isOnline(friendUsername))
                        friendUsername = "*" + friendUsername;
                    friends.add(friendUsername);
                }
                rs.close();
                stmt.close();
            }
            rs_eid1.close();
            stmt_eid1.close();
            
            PreparedStatement stmt_eid2 = connection.prepareStatement("SELECT eid1 FROM friendship WHERE eid2 = ?");
            stmt_eid2.setString(1, eid);
            ResultSet rs_eid2 = stmt_eid2.executeQuery();
            while (rs_eid2.next()) {
                String friendEid = rs_eid2.getString("eid1");
                PreparedStatement stmt = connection.prepareStatement("SELECT username FROM user WHERE eid = ?");
                stmt.setString(1, friendEid);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String friendUsername = rs.getString("username");
                    if (UserThread.isOnline(friendUsername))
                        friendUsername = "*" + friendUsername;
                    friends.add(friendUsername);
                }
                rs.close();
                stmt.close();
            }
            rs_eid2.close();
            stmt_eid2.close();
        }
        rs1.close();
        stmt1.close();
        
        StringBuilder result = new StringBuilder("friendlist");
        if (friends != null) {
            for (String friend : friends) {
                result.append(":").append(friend);
            }
        }
        return result.toString();
    }

    public boolean isFriend(String user1, String user2) throws SQLException {
        String eid1 = getEID(user1);
        String eid2 = getEID(user2);
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM friendship WHERE (eid1=? and eid2=?) or (eid1=? and eid2=?)");
        statement.setString(1, eid1);
        statement.setString(2, eid2);
        statement.setString(3, eid2);
        statement.setString(4, eid1);

        ResultSet result = statement.executeQuery();
        result.next();
        int count = result.getInt(1);
        return count > 0;
    }

    public String getEID(String username) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
            "select eid from user where username=?"
        );
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        result.next();
        return result.getString(1);
    }

    public void addFriend(String user1, String user2) throws SQLException {
        PreparedStatement statement;
        String eid1 = this.getEID(user1);
        String eid2 = this.getEID(user2);
        statement = connection.prepareStatement(
            "INSERT INTO friendship (eid1, eid2) VALUES (?, ?);"
        );
        statement.setString(1, eid1);
        statement.setString(2, eid2);
        statement.execute();
        String tableName = getTableName(eid1, eid2);
        statement = connection.prepareStatement(
            "CREATE TABLE if not exists " + tableName +
            "(eid CHAR(12) not null, time DATETIME DEFAULT CURRENT_TIMESTAMP, base64msg VARCHAR(11000) not null);"
        );
        Logger.println("create table: " + tableName);
        statement.execute();
        // 转发
        synchronized (UserThread.onlineUsers) {
            if (UserThread.isOnline(user2)) {
                PrintWriter printWriter = UserThread.onlineUsers.get(user2);
                printWriter.println("server:addfriend:" + user1);
            }
        }
    }

    public String deleteFriend(String user1, String user2) throws SQLException {
        String eid1 = this.getEID(user1);
        String eid2 = this.getEID(user2);

        PreparedStatement statement1 = connection.prepareStatement(
            "select count(*) from friendship where eid1=? and eid2=?;"
        );
        statement1.setString(1, eid1);
        statement1.setString(2, eid2);
        ResultSet result1 = statement1.executeQuery();
        result1.next();
        int count1 = result1.getInt(1);
        if (count1 > 0) {
            statement1 = connection.prepareStatement("delete from friendship where eid1=? and eid2=?;");
            statement1.setString(1, eid1);
            statement1.setString(2, eid2);
            int ret = statement1.executeUpdate();
            if (ret == 1) {
                Logger.println("delete friendship: \"" + user1 + "\" and \"" + user2 + "\"");
                // 转发
                synchronized (UserThread.onlineUsers) {
                    if (UserThread.isOnline(user2)) {
                        PrintWriter printWriter = UserThread.onlineUsers.get(user2);
                        printWriter.println("server:deletefriend:" + user1);
                    }
                }
                return "true";
            }
            return "false";
        }

        PreparedStatement statement2 = connection.prepareStatement(
            "select count(*) from friendship where eid1=? and eid2=?;"
        );
        statement2.setString(1, eid2);
        statement2.setString(2, eid1);
        statement2.execute();
        ResultSet result2 = statement2.executeQuery();
        result2.next();
        int count2 = result2.getInt(1);
        if (count2 > 0) {
            statement2 = connection.prepareStatement("delete from friendship where eid1=? and eid2=?;");
            statement2.setString(1, eid2);
            statement2.setString(2, eid1);
            int ret = statement2.executeUpdate();
            if (ret == 1) {
                Logger.println("delete friendship: \"" + user1 + "\" and \"" + user2 + "\"");
                // 转发
                synchronized (UserThread.onlineUsers) {
                    if (UserThread.isOnline(user2)) {
                        PrintWriter printWriter = UserThread.onlineUsers.get(user2);
                        printWriter.println("server:deletefriend:" + user1);
                    }
                }
                return "true";
            }
            return "false";
        }
        return "false";
    }

    public boolean tableNotExists(String tableName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
            "show tables where Tables_in_easychat=?;"
        );
        statement.setString(1, tableName);
        ResultSet rs = statement.executeQuery();
        return !rs.next();
    }

    public String sendMessage(String username, String target, String base64msg) throws SQLException {
        String eid1 = this.getEID(username);
        String eid2 = this.getEID(target);
        String tableName = getTableName(eid1, eid2);
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO " + tableName + "(eid, base64msg) VALUES (?, ?);"
        );
        statement.setString(1, eid1);
        statement.setString(2, base64msg);
        int count = statement.executeUpdate();
        if (count == 0) return "false";

        byte[] decoded = Base64.getDecoder().decode(base64msg);
        String msg = new String(decoded, StandardCharsets.UTF_8);
        Logger.println("\033[35m" + username + ": \"" + msg + "\"\033[0m");
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        // 消息转发
        synchronized (UserThread.onlineUsers) {
            if (UserThread.isOnline(target)) {
                PrintWriter printWriter = UserThread.onlineUsers.get(target);
                printWriter.println("server:receive:" + username + ":" + currentTime + ":" + base64msg);
            }
        }
        return "true:" + currentTime;
    }

    public String getRecordsAfterTime(String user1, String user2, String dateTime) throws SQLException {
        String eid1 = this.getEID(user1);
        String eid2 = this.getEID(user2);
        String tableName = getTableName(eid1, eid2);
        PreparedStatement statement = connection.prepareStatement(
            "SELECT eid, time, base64msg FROM " + tableName + " WHERE time > '" + dateTime + "';"
        );
        ResultSet rs = statement.executeQuery();
        StringBuilder result = new StringBuilder("records");
        while (rs.next()) {
            // eid
            String eid = rs.getString("eid");
            String username = eid.equals(eid1) ? user1 : user2;
            
            // time
            Timestamp timestamp = rs.getTimestamp("time");
            Date date = new Date(timestamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String datetime = sdf.format(date);

            // message
            String base64msg = rs.getString("base64msg");

            // 组合
            String record = username + "," + datetime + "," + base64msg;
            result.append(":").append(record);
        }
        if (result.toString().equals("records"))
            return "records:null";
        return result.toString();
    }

    private String getTableName(String eid1, String eid2) {
        if (eid1.compareTo(eid2) == -1)
            return "chat_" + eid1 + "_" + eid2;
        else
            return "chat_" + eid2 + "_" + eid1;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
