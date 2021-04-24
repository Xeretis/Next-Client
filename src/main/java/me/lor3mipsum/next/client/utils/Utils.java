package me.lor3mipsum.next.client.utils;

import com.google.common.hash.Hashing;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.utils.world.Dimension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;

public class Utils {
    public static boolean isReleasingTrident;

    public static String getKeyName(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_UNKNOWN: return "Null";
            case GLFW.GLFW_KEY_ESCAPE: return "Esc";
            case GLFW.GLFW_KEY_GRAVE_ACCENT: return "Grave Accent";
            case GLFW.GLFW_KEY_PRINT_SCREEN: return "Print Sc";
            case GLFW.GLFW_KEY_PAUSE: return "Pause";
            case GLFW.GLFW_KEY_INSERT: return "Insert";
            case GLFW.GLFW_KEY_DELETE: return "Delete";
            case GLFW.GLFW_KEY_HOME: return "Home";
            case GLFW.GLFW_KEY_PAGE_UP: return "PgUp";
            case GLFW.GLFW_KEY_PAGE_DOWN: return "PgDown";
            case GLFW.GLFW_KEY_END: return "End";
            case GLFW.GLFW_KEY_TAB: return "Tab";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCtrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCtrl";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LAlt";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RAlt";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LShift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RShift";
            case GLFW.GLFW_KEY_UP: return "Arrow U";
            case GLFW.GLFW_KEY_DOWN: return "Arrow D";
            case GLFW.GLFW_KEY_LEFT: return "Arrow L";
            case GLFW.GLFW_KEY_RIGHT: return "Arrow R";
            case GLFW.GLFW_KEY_APOSTROPHE: return "Apostrophe";
            case GLFW.GLFW_KEY_BACKSPACE: return "Backspace";
            case GLFW.GLFW_KEY_CAPS_LOCK: return "CLock";
            case GLFW.GLFW_KEY_MENU: return "Menu";
            case GLFW.GLFW_KEY_LEFT_SUPER: return "LSuper";
            case GLFW.GLFW_KEY_RIGHT_SUPER: return "RSuper";
            case GLFW.GLFW_KEY_ENTER: return "Enter";
            case GLFW.GLFW_KEY_NUM_LOCK: return "NLock";
            case GLFW.GLFW_KEY_SPACE: return "Space";
            case GLFW.GLFW_KEY_F1: return "F1";
            case GLFW.GLFW_KEY_F2: return "F2";
            case GLFW.GLFW_KEY_F3: return "F3";
            case GLFW.GLFW_KEY_F4: return "F4";
            case GLFW.GLFW_KEY_F5: return "F5";
            case GLFW.GLFW_KEY_F6: return "F6";
            case GLFW.GLFW_KEY_F7: return "F7";
            case GLFW.GLFW_KEY_F8: return "F8";
            case GLFW.GLFW_KEY_F9: return "F9";
            case GLFW.GLFW_KEY_F10: return "F10";
            case GLFW.GLFW_KEY_F11: return "F11";
            case GLFW.GLFW_KEY_F12: return "F12";
            case GLFW.GLFW_KEY_F13: return "F13";
            case GLFW.GLFW_KEY_F14: return "F14";
            case GLFW.GLFW_KEY_F15: return "F15";
            case GLFW.GLFW_KEY_F16: return "F16";
            case GLFW.GLFW_KEY_F17: return "F17";
            case GLFW.GLFW_KEY_F18: return "F18";
            case GLFW.GLFW_KEY_F19: return "F19";
            case GLFW.GLFW_KEY_F20: return "F20";
            case GLFW.GLFW_KEY_F21: return "F21";
            case GLFW.GLFW_KEY_F22: return "F22";
            case GLFW.GLFW_KEY_F23: return "F23";
            case GLFW.GLFW_KEY_F24: return "F24";
            case GLFW.GLFW_KEY_F25: return "F25";
            default:
                String keyName = GLFW.glfwGetKeyName(key, 0);
                if (keyName == null) return "Null";
                return StringUtils.capitalize(keyName);
        }
    }

    public static int getKeyFromName(String name) {
        if (name.equalsIgnoreCase("null"))
            return -1;

        int key;
        try {
            key = InputUtil.fromTranslationKey("key.keyboard." + name.toLowerCase(Locale.ENGLISH)).getCode();
        } catch (IllegalArgumentException e) {
            if (name.toLowerCase(Locale.ENGLISH).startsWith("right")) {
                try {
                    key = InputUtil.fromTranslationKey("key.keyboard." + name.toLowerCase(Locale.ENGLISH).replaceFirst("right", "right.")).getCode();
                } catch (IllegalArgumentException e1) {
                    return -2;
                }
            } else if (name.toLowerCase(Locale.ENGLISH).startsWith("r")) {
                try {
                    key = InputUtil.fromTranslationKey("key.keyboard." + name.toLowerCase(Locale.ENGLISH).replaceFirst("r", "right.")).getCode();
                } catch (IllegalArgumentException e1) {
                    return -2;
                }
            } else
                return -2;
        }
        return key;
    }

    public static byte[] rawHWID() throws NoSuchAlgorithmException {
        String main = System.getenv("PROCESS_IDENTIFIER")
                + System.getenv("PROCESSOR_LEVEL")
                + System.getenv("PROCESSOR_REVISION")
                + System.getenv("PROCESSOR_ARCHITECTURE")
                + System.getenv("PROCESSOR_ARCHITEW6432")
                + System.getenv("NUMBER_OF_PROCESSORS")
                + System.getenv("COMPUTERNAME");
        byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return messageDigest.digest(bytes);
    }

    public static String encrypt(String strToEncrypt, String secret) {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(secret));
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }


    public static SecretKeySpec getKey(String myKey) {
        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEncryptedHWID(String key){
        try {
            String a = Hashing.sha1().hashString(new String(rawHWID(), StandardCharsets.UTF_8), StandardCharsets.UTF_8).toString();
            String b = Hashing.sha256().hashString(a, StandardCharsets.UTF_8).toString();
            String c = Hashing.sha512().hashString(b, StandardCharsets.UTF_8).toString();
            String d = Hashing.sha1().hashString(c, StandardCharsets.UTF_8).toString();
            return encrypt(d,"spartanB312" + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static Dimension getDimension() {
        switch (MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath()) {
            case "the_nether": return Dimension.Nether;
            case "the_end":    return Dimension.End;
            default:           return Dimension.Overworld;
        }
    }
}
