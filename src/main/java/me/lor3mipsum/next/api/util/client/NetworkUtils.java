package me.lor3mipsum.next.api.util.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {
    public static List<String> getHWIDList() {
        List<String> HWIDs = new ArrayList<>();
        try {
            final URL url = new URL("https://pastebin.com/raw/fdjpdGWr");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                HWIDs.add(inputLine);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return HWIDs;
    }
}
