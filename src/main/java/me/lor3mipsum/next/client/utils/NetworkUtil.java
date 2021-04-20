package me.lor3mipsum.next.client.utils;

import com.google.common.hash.Hashing;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class NetworkUtil {
    public static List<String> getHWIDList() {
        List<String> Recipies = new ArrayList<>();
        try {
            final URL url = new URL("https://pastebin.com/raw/fdjpdGWr");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Recipies.add(inputLine);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Recipies;
    }
}
