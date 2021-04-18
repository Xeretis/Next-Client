package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.client.setting.Setting;

import java.awt.*;

public class ColorSetting extends Setting implements com.lukflug.panelstudio.settings.ColorSetting {
    private boolean rainbow;
    private Color value;

    public ColorSetting (String name, final Color value) {
        this.name = name;
        this.value = value;
    }

    public long toInteger() {
        return this.value.getRGB();
    }

    public void fromInteger (long number) {
        this.value = new Color(Math.toIntExact(number),true);
    }

    public Color getValue() {
        if (rainbow) {
            return getRainbow(0, this.getColor().getAlpha());
        }
        return value;
    }

    public static Color getRainbow(int incr, int alpha) {
        Color color =  Color.getHSBColor(((System.currentTimeMillis() + incr * 200)%(360*20))/(360f * 20),0.5f,1f);
        return new Color(color.getRed(), color.getBlue(), color.getGreen(), alpha);
    }

    public Color getColor() {
        return value;
    }

    @Override
    public boolean getRainbow() {
        return this.rainbow;
    }

    @Override
    public void setValue(Color value) {
        this.value = value;
    }

    @Override
    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(name, toInteger());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(name)) {
            JsonElement element = obj.get(name);

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {
                fromInteger(element.getAsLong());
            } else {
                throw new IllegalArgumentException("Entry '" + name + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + name + "'");
        }
    }
}
