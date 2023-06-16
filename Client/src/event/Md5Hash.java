package src.event;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @BelongsProject: EasyChat
 * @FileName: Md5Hash
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 取文本MD5值，用于加密密码
 */
public class Md5Hash {
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16).toUpperCase();
            int fillLength = 32 - hashText.length();
            if (fillLength == 0) return hashText;
            String zeroFill = new String(new char[fillLength]).replace('\0', '0');
            return zeroFill + hashText;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
