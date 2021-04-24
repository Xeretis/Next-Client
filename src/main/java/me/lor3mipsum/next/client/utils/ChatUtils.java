package me.lor3mipsum.next.client.utils;

import me.lor3mipsum.next.mixin.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.lor3mipsum.next.client.module.Module;

public class ChatUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static void sendMsg(int id, String prefix, PrefixType type, String msg, Formatting color) {

        String formatted = msg.replaceAll("\\(default\\)", color.toString()).replaceAll("\\(highlight\\)", Formatting.WHITE.toString());

        BaseText message = new LiteralText(formatted);
        message.setStyle(message.getStyle().withFormatting(color));

        sendMsg(id, prefix, type, message);
    }

    private static void sendMsg(int id, String prefix, PrefixType type, Text msg) {
        if (mc.world == null) return;

        BaseText message = new LiteralText("");
        message.append(getPrefix(prefix, type));
        message.append(msg);

        ((ChatHudAccessor) mc.inGameHud.getChatHud()).add(message, id);
    }

    private static void message(int id, Formatting color, String msg, Object... args) {
        sendMsg(id, null, PrefixType.None, formatMsg(msg, color, args), color);
    }

    public static void info(int id, String msg, Object... args) {
        message(id, Formatting.GRAY, msg, args);
    }

    public static void info(String msg, Object... args) {
        message(0, Formatting.GRAY, msg, args);
    }

    public static void info(String prefix, Text msg) {
        sendMsg(0, prefix, PrefixType.Other, msg);
    }

    public static void info(Text msg) {
        sendMsg(0, null, PrefixType.None, msg);
    }

    public static void warning(String msg, Object... args) {
        message(0, Formatting.YELLOW, msg, args);
    }
    public static void error(String msg, Object... args) {
        message(0, Formatting.RED, msg, args);
    }

    private static void prefixMessage(Formatting color, String prefix, String msg, Object... args) {
        sendMsg(0, prefix, PrefixType.Other, formatMsg(msg, color, args), color);
    }

    public static void prefixInfo(String prefix, String msg, Object... args) {
        prefixMessage(Formatting.GRAY, prefix, msg, args);
    }

    public static void prefixWarning(String prefix, String msg, Object... args) {
        prefixMessage(Formatting.YELLOW, prefix, msg, args);
    }

    public static void prefixError(String prefix, String msg, Object... args) {
        prefixMessage(Formatting.RED, prefix, msg, args);
    }

    private static void moduleMessage(Formatting color, Module module, String msg, Object... args) {
        sendMsg(0, module.getName(), PrefixType.Module, formatMsg(msg, color, args), color);
    }

    private static void moduleMessage(Formatting color, Module module, Text msg) {
        sendMsg(0, module.getName(), PrefixType.Module, msg);
    }

    public static void moduleInfo(Module module, String msg, Object... args) {
        moduleMessage(Formatting.GRAY, module, msg, args);
    }

    public static void moduleInfo(Module module, Text msg) {
        moduleMessage(Formatting.GRAY, module, msg);
    }

    public static void moduleWarning(Module module, String msg, Object... args) {
        moduleMessage(Formatting.YELLOW, module, msg, args);
    }

    public static void moduleWarning(Module module, Text msg) {
        moduleMessage(Formatting.YELLOW, module, msg);
    }

    public static void moduleError(Module module, String msg, Object... args) {
        moduleMessage(Formatting.RED, module, msg, args);
    }

    public static void moduleError(Module module, Text msg) {
        moduleMessage(Formatting.RED, module, msg);
    }

    private static BaseText getPrefix(String title, PrefixType type) {
        BaseText next;
        BaseText prefix = new LiteralText("");


        next = new LiteralText("Next");
        next.setStyle(next.getStyle().withFormatting(Formatting.DARK_AQUA));


        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY));
        prefix.append("[");
        prefix.append(next);
        prefix.append("] ");

        if (type != PrefixType.None) {
            BaseText moduleTitle = new LiteralText(title);
            moduleTitle.setStyle(moduleTitle.getStyle().withFormatting(type.color));
            prefix.append("[");
            prefix.append(moduleTitle);
            prefix.append("] ");
        }

        return prefix;
    }

    public static String formatMsg(String format, Formatting defaultColor, Object... args) {
        String msg = String.format(format, args);
        msg = msg.replaceAll("\\(default\\)", defaultColor.toString());
        msg = msg.replaceAll("\\(highlight\\)", Formatting.WHITE.toString());
        msg = msg.replaceAll("\\(underline\\)", Formatting.UNDERLINE.toString());

        return msg;
    }

    private enum PrefixType {
        Module(Formatting.AQUA),
        Other(Formatting.BLUE),
        None(Formatting.RESET);

        public Formatting color;

        PrefixType(Formatting color) {
            this.color = color;
        }
    }
}
