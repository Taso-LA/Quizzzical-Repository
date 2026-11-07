package quizzzical.data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class PasswordUtil
{
    private PasswordUtil() { }

    public static String hash(String password)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for(byte value : bytes) result.append(String.format("%02x", value));
            return result.toString();
        }
        catch(Exception exception)
        {
            throw new IllegalStateException("Could not process password.", exception);
        }
    }
}
