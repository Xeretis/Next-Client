package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.client.setting.Setting;

public class NumberSetting extends Setting implements com.lukflug.panelstudio.settings.NumberSetting {
    public double value;
    public double minimum;
    public double maximum;
    public double increment;

    public NumberSetting(String name, double value, double minimum, double maximum, double increment) {
        this.name = name;
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public double getIncrement() {
        return this.increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }

    @Override
    public double getMaximumValue() {
        return this.maximum;
    }

    @Override
    public double getMinimumValue() {
        return this.minimum;
    }

    @Override
    public double getNumber() {
        return this.value;
    }

    @Override
    public int getPrecision() {
        return 1;
    }

    @Override
    public void setNumber(double value) {
        double precision = 1.0D / this.increment;
        this.value = Math.round(Math.max(this.minimum, Math.min(this.maximum, value)) * precision) / precision;
    }

    //config
    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(name, getNumber());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(name)) {
            JsonElement element = obj.get(name);

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {

                setNumber(obj.get(name).getAsNumber().doubleValue());

            } else {
                throw new IllegalArgumentException("Entry '" + name + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + name + "'");
        }
    }
}
