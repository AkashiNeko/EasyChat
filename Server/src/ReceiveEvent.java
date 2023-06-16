package src;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: EasyChat
 * @FileName: ReceiveEvent
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 回复事件，收到客户端的请求后回复
 */

public class ReceiveEvent {
    UserDAO userDAO = new UserDAO();
    public ReceiveEvent() {}

    public String execute(String msg) {
        String[] args = msg.split(":");
        switch (args[0]) {
        case "login" -> {
            // login : password : username
            if (args.length != 3)
                return "login:unknown";
            return this.login(args[2], args[1]);
        }
        case "register" -> {
            // register : password : username : email
            if (args.length != 4)
                return "register:unknown";
            return this.register(args[2], args[1], args[3]);
        }
        case "getfriendlist" -> {
            // getfriendlist : password : username
            if (args.length != 3)
                return "getfriendlist:unknown";
            return this.getFriendList(args[2], args[1]);
        }
        case "userexist" -> {
            // userexist : password : username : checkuser
            if (args.length != 4)
                return "userexist:unknown";
            return this.userExists(args[2], args[1], args[3]);
        }
        case "isfriend" -> {
            // isfriend : password : username1 : username2
            if (args.length != 4)
                return "isfriend:unknown";
            return this.isFriend(args[2], args[1], args[3]);
        }
        case "addfriend" -> {
            // addfriend : password : username : inputusername
            if (args.length != 4)
                return "addfriend:unknown";
            return this.addFriend(args[2], args[1], args[3]);
        }
        case "deletefriend" -> {
            // addfriend : password : username : inputusername
            if (args.length != 4)
                return "deletefriend:unknown";
            return this.deleteFriend(args[2], args[1], args[3]);
        }
        case "sendmessage" -> {
            // sendmessage : password : username : targetuser : message
            if (args.length != 5)
                return "sendmessage:unknown";
            return this.sendMessage(args[2], args[1], args[3], args[4]);
        }
        case "getrecords" -> {
            // getrecords : password : username : targetuser : lasttime
            if (args.length != 5)
                return "getrecords:unknown";
            return this.getRecordsAfterTime(args[2], args[1], args[3], args[4]);
        }
        default -> {
            Logger.print("\033[31munknown command: \"" + msg + "\"\033[0m");
            return "error:unknown_command";
        }
        }
    }

    public List<String> getOnlineFriends(String username) {
        String friendListString;
        try {
            friendListString = userDAO.getFriendList(username);
            String[] friendList = friendListString.split(":");
            List<String> onlineFriends = new ArrayList<>();
            for (int i = 1; i < friendList.length; ++i) {
                if (friendList[i].charAt(0) == '*')
                    onlineFriends.add(friendList[i].substring(1));
            }
            return onlineFriends;
        } catch (SQLException e) {
            Logger.print(e.getMessage());
            return new ArrayList<>();
        }
    }

    private String login(String username, String passwordMD5) {
        try {
            if (!userDAO.userExists(username)) {
                return "error:not_exist";
            }
            if (userDAO.checkUserPassword(username, passwordMD5)) {
                return "error:wrong_password";
            }
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
        int newPort = PortPool.getRandomPort();
        UserThread newTread = new UserThread(newPort, username);
        newTread.start();
        return "true:" + newPort;
    }

    private String register(String username, String passwordMD5, String email) {
        try {
            if (userDAO.userExists(username)) {
                return "error:username_exist";
            }
            userDAO.userRegister(username, passwordMD5, email);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
        return "true";
    }

    private String getFriendList(String username, String passwordMD5) {
        String result;
        try {
            if (!userDAO.userExists(username)) {
                return "error:not_exist";
            }
            if (userDAO.checkUserPassword(username, passwordMD5)) {
                return "error:wrong_password";
            }
            result = userDAO.getFriendList(username);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
        return result;
    }

    private String userExists(String username, String passwordMD5, String checkUser) {
        try {
            if (userDAO.checkUserPassword(username, passwordMD5))
                return "error:wrong_password";
            if (userDAO.userExists(checkUser))
                return "true";
            return "false";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    private String isFriend(String username, String passwordMD5, String friend) {
        try {
            if (userDAO.checkUserPassword(username, passwordMD5))
                return "error:wrong_password";
            if (userDAO.isFriend(username, friend))
                return "true";
            return "false";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
    private String addFriend(String username, String passwordMD5, String newFriend) {
        try {
            if (userDAO.checkUserPassword(username, passwordMD5))
                return "error:wrong_password";
            if (username.equals(newFriend))
                return "error:is_yourself";
            if (userDAO.isFriend(username, newFriend))
                return "error:not_friends";
            userDAO.addFriend(username, newFriend);
            return "true";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    private String deleteFriend(String username, String passwordMD5, String deleteFriend) {
        try {
            if (userDAO.checkUserPassword(username, passwordMD5))
                return "error:wrong_password";
            if (username.equals(deleteFriend))
                return "error:is_yourself";
            if (!userDAO.isFriend(username, deleteFriend))
                return "error:is_not_friends";
            return userDAO.deleteFriend(username, deleteFriend);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    private String sendMessage(String username, String passwordMD5, String targetUser, String msgBase64) {
        try {
            if (userDAO.checkUserPassword(username, passwordMD5))
                return "error:wrong_password";
            if (username.equals(targetUser))
                return "error:is_yourself";
            if (!userDAO.isFriend(username, targetUser))
                return "error:is_not_friends";
            
            return userDAO.sendMessage(username, targetUser, msgBase64);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    private String getRecordsAfterTime(String username, String passwordMD5, String targetUser, String dateTimeString) {
        try {
            if (userDAO.checkUserPassword(username, passwordMD5))
                return "error:wrong_password";
            if (username.equals(targetUser))
                return "error:is_yourself";
            if (!userDAO.isFriend(username, targetUser))
                return "error:is_not_friends";
            DateTimeFormatter fTrans = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime time = LocalDateTime.parse(dateTimeString, fTrans);
            String dateTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return userDAO.getRecordsAfterTime(username, targetUser, dateTime);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    public void close() {
        try {
            userDAO.close();
        } catch (SQLException e) {
            Logger.print("userDAO close error:" + e.getMessage());
        }
    }
}
