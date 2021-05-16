package me.lor3mipsum.next.api.util.player;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ChatUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendMessage(String message) {
        try {
            mc.inGameHud.getChatHud().addMessage(new LiteralText(message));
        } catch (Exception e) {
            Main.LOG.error(e.getStackTrace());
        }
    }

    public static String getClientPrefix() {
        return Formatting.GRAY + "[" + Formatting.BLUE + "Next" + Formatting.GRAY + "] " + Formatting.RESET;
    }

    public static String getModulePrefix(Module mod) {
        return Formatting.GRAY + "[" + Formatting.AQUA + mod.getName() + Formatting.GRAY + "] " + Formatting.RESET;
    }

    public static String getCommandPrefix(Command cmd) {
        return Formatting.GRAY + "[" + Formatting.DARK_AQUA + cmd.getName() + Formatting.GRAY + "] " + Formatting.RESET;
    }

    public static void formattedMessage(Formatting color, String msg) {
        sendMessage(color + msg);
    }

    public static void info(String msg) {
        sendMessage(getClientPrefix() + Formatting.GRAY + msg);
    }

    public static void warning(String msg) {
        sendMessage(getClientPrefix() + Formatting.YELLOW + msg);
    }

    public static void error(String msg) {
        sendMessage(getClientPrefix() + Formatting.RED + msg);
    }

    public static void noPrefixInfo(String msg) {
        sendMessage(Formatting.GRAY + msg);
    }

    public static void noPrefixWarning(String msg) {
        sendMessage(Formatting.YELLOW + msg);
    }

    public static void noPrefixError(String msg) {
        sendMessage(Formatting.RED + msg);
    }

    public static void moduleInfo(Module mod, String msg) {
        sendMessage(getClientPrefix() + getModulePrefix(mod) + Formatting.GRAY + msg);
    }

    public static void moduleWarning(Module mod, String msg) {
        sendMessage(getClientPrefix() + getModulePrefix(mod) + Formatting.YELLOW + msg);
    }

    public static void moduleError(Module mod, String msg) {
        sendMessage(getClientPrefix() + getModulePrefix(mod) + Formatting.RED + msg);
    }

    public static void commandInfo(Command cmd, String msg) {
        sendMessage(getClientPrefix() + getCommandPrefix(cmd) + Formatting.GRAY + msg);
    }

    public static void commandWarning(Command cmd, String msg) {
        sendMessage(getClientPrefix() + getCommandPrefix(cmd) + Formatting.YELLOW + msg);
    }

    public static void commandError(Command cmd, String msg) {
        sendMessage(getClientPrefix() + getCommandPrefix(cmd) + Formatting.RED + msg);
    }
}
