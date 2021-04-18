package me.lor3mipsum.next.client.setting;

import com.google.gson.JsonObject;
import me.lor3mipsum.next.client.module.Module;

import java.util.function.Predicate;

public abstract class Setting {
    public String name;
    public boolean focused;

    public abstract void addToJsonObject(JsonObject obj);

    public abstract void fromJsonObject(JsonObject obj);
}
