package com.cfunicorn.reportsgui.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

  private final JavaPlugin plugin;
  private final int resourceId;

  public UpdateChecker(JavaPlugin plugin, int resourceId) {
    this.plugin = plugin;
    this.resourceId = resourceId;
  }

  public void getVersion(final Consumer<String> consumer) {
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
        if (scanner.hasNext()) {
          Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[ReportsGUI] Checking for updates...");
          consumer.accept(scanner.next());
        }
      } catch (IOException exception) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ReportsGUI] Unable to check for updates: " + exception.getMessage());
      }
    });
  }
}
