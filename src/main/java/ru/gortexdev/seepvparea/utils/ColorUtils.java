package ru.gortexdev.seepvparea.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class ColorUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final boolean SUPPORTS_HEX = checkHexSupport();

    private static boolean checkHexSupport() {
        try {
            String version = Bukkit.getVersion();
            return version.contains("1.16") || version.contains("1.17") ||
                    version.contains("1.18") || version.contains("1.19") ||
                    version.contains("1.20");
        } catch (Exception e) {
            return false;
        }
    }

    public static String color(String message) {
        if (message == null) return "";

        String colored = ChatColor.translateAlternateColorCodes('&', message);

        if (SUPPORTS_HEX) {
            try {
                Matcher matcher = HEX_PATTERN.matcher(colored);
                StringBuffer buffer = new StringBuffer();
                while (matcher.find()) {
                    String hex = matcher.group(1);
                    matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
                }
                colored = matcher.appendTail(buffer).toString();
            } catch (Exception e) {
            }
        }

        return colored;
    }

    public static List<String> color(List<String> messages) {
        return messages.stream()
                .map(ColorUtils::color)
                .collect(Collectors.toList());
    }
}