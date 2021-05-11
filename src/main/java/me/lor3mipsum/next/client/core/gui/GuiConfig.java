package me.lor3mipsum.next.client.core.gui;

import com.lukflug.panelstudio.config.IConfigList;
import com.lukflug.panelstudio.config.IPanelConfig;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.config.LoadConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GuiConfig implements IConfigList {
    private final String fileLocation;
    private Map<String, Object> panelMap = null;

    public GuiConfig(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    public void begin(boolean loading) {
        if (loading) {
            if (!Files.exists(Paths.get(fileLocation + "GuiPanels" + ".yaml")))
                return;
            try {
                Yaml yaml = new Yaml();

                InputStream inputStream;
                inputStream = Files.newInputStream(Paths.get(fileLocation + "GuiPanels" + ".yaml"));

                panelMap = yaml.load(inputStream);
                inputStream.close();
            } catch (Exception e) {
                LoadConfig.backup("Failed to load panels");
                Main.LOG.error("Failed to load panels");
                Main.LOG.error(e.getStackTrace());
            }
        } else {
            panelMap = new LinkedHashMap<>();
        }
    }

    @Override
    public void end(boolean loading) {
        if (panelMap == null)
            return;
        if (!loading) {
            try {
                DumperOptions options = new DumperOptions();
                options.setIndent(4);
                options.setPrettyFlow(true);

                Yaml yaml = new Yaml(options);
                OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileLocation + "GuiPanels" + ".yaml"), StandardCharsets.UTF_8);

                StringWriter writer = new StringWriter();
                yaml.dump(panelMap, writer);

                fileOutputStreamWriter.write(writer.toString());
                fileOutputStreamWriter.close();
            } catch (Exception e) {
                Main.LOG.error("Failed to save panels");
                Main.LOG.error(e.getStackTrace());
            }
            panelMap = null;
        }
    }

    @Override
    public IPanelConfig addPanel(String title) {
        if (panelMap == null) return null;
        Map<String, Object> valueMap = new LinkedHashMap<>();
        panelMap.put(title, valueMap);
        return new NextPanelConfig(valueMap);
    }

    @Override
    public IPanelConfig getPanel(String title) {
        if (panelMap == null) return null;
        try {
            Map<String, Object> configMap = (Map<String, Object>) panelMap.get(title);
            if (configMap == null)
                return null;
            return new NextPanelConfig(configMap);
        } catch (Exception e) {
            Main.LOG.error("Failed to get panel");
            Main.LOG.error(e.getStackTrace());
        }
        return null;
    }

    private static class NextPanelConfig implements IPanelConfig {

        Map<String, Object> configMap;

        public NextPanelConfig(Map<String, Object> configMap) {
            this.configMap = configMap;
        }

        @Override
        public void savePositon(Point position) {
            configMap.put("PosX", position.x);
            configMap.put("PosY", position.y);
        }

        @Override
        public Point loadPosition() {
            Point point = new Point();
            try {
                point.x = (int) configMap.get("PosX");
                point.y = (int) configMap.get("PosY");
            } catch (Exception e) {
                LoadConfig.backup("Failed to load panel position");
                Main.LOG.error("Failed to load panel position");
                Main.LOG.error(e.getStackTrace());
                return null;
            }
            return point;
        }

        @Override
        public void saveState(boolean state) {
            configMap.put("State", state);
        }

        @Override
        public boolean loadState() {
            boolean state;
            try {
                state = (boolean) configMap.get("State");
            } catch (Exception e) {
                LoadConfig.backup("Failed to load panel state");
                Main.LOG.error("Failed to load panel state");
                Main.LOG.error(e.getStackTrace());
                return false;
            }
            return state;
        }
    }
}
