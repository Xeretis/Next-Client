package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.client.setting.Setting;

import java.util.function.Predicate;

public class NumberSetting<T extends Number> extends Setting<T> {
    private T min;
    private T max;

    public NumberSetting(String name, T defaultVal, T min, T max) {
        this(name, defaultVal, min, max, null);
    }

    public NumberSetting(String name, T defaultVal, T min, T max, Predicate<T> validator) {
        super(name, defaultVal, validator == null ? val -> val.doubleValue() >= min.doubleValue() && val.doubleValue() <= max.doubleValue() : validator.and(val -> val.doubleValue() >= min.doubleValue() && val.doubleValue() <= max.doubleValue()));
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {

                if (getObject() instanceof Integer) {
                    setObject((T) Integer.valueOf(obj.get(getName()).getAsNumber().intValue()));
                }
                if (getObject() instanceof Long) {
                    setObject((T) Long.valueOf(obj.get(getName()).getAsNumber().longValue()));
                }
                if (getObject() instanceof Float) {
                    setObject((T) Float.valueOf(obj.get(getName()).getAsNumber().floatValue()));
                }
                if (getObject() instanceof Double) {
                    setObject((T) Double.valueOf(obj.get(getName()).getAsNumber().doubleValue()));
                }
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + getName() + "'");
        }
    }
}
