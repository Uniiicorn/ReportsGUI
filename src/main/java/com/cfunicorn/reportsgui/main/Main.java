package com.cfunicorn.reportsgui.main;

import com.cfunicorn.reportsgui.utils.Loader;
import com.cfunicorn.reportsgui.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  private static Main main;
  private Loader loader;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    setMain(this);

    setLoader(new Loader(this));

    getLoader().registerCommands(this);
    getLoader().registerListeners(this);

    new UpdateChecker(this, 109227).getVersion(version -> {
      if (this.getDescription().getVersion().equals(version)) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ReportsGUI] You're running the latest version.");
      } else {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ReportsGUI] There is a new version available!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ReportsGUI] " + getDescription().getWebsite());
      }
    });
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }

  public Loader getLoader() {
    return loader;
  }

  private void setLoader(Loader loader) {
    this.loader = loader;
  }

  public static Main getMain() {
    return main;
  }

  private static void setMain(Main main) {
    Main.main = main;
  }
}
