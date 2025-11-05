package ru.gortexdev.seepvparea.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class ColorUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private static final boolean SUPPORTS_HEX = (Bukkit.getVersion().contains("1.16") ||
            Bukkit.getVersion().contains("1.17") ||
            Bukkit.getVersion().contains("1.18") ||
            Bukkit.getVersion().contains("1.19"));

    public static String color(String message) {
        if (message == null)
            return "";
        if (SUPPORTS_HEX) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find())
                matcher.appendReplacement(buffer,
                        ChatColor.of("#" + matcher.group(1)).toString());
            message = matcher.appendTail(buffer).toString();
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> color(List<String> messages) {
        return (List<String>)messages.stream()
                .map(ColorUtils::color)
                .collect(Collectors.toList());
    }
}
