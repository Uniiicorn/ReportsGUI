package com.cfunicorn.reportsgui.utils;

import com.cfunicorn.reportsgui.main.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerHandler {

  private final Loader loader;
  private File playerFile;
  private FileConfiguration playerConfig;

  public PlayerHandler(Loader loader) {
    if (checkForFile(Main.getMain())) {
      setPlayerFile(new File(Main.getMain().getDataFolder() + "/Players", "PlayerManager.yml"));
      setPlayerConfig(YamlConfiguration.loadConfiguration(getPlayerFile()));
    }
    setPlayerFile(new File(Main.getMain().getDataFolder() + "/Players", "PlayerManager.yml"));
    setPlayerConfig(YamlConfiguration.loadConfiguration(getPlayerFile()));
    this.loader = loader;
  }

  private boolean checkForFile(Main main) {
    File temp = new File(main.getDataFolder() + "/Players", "PlayerManager.yml");
    File dir = new File(main.getDataFolder() + "/Players");
    if (!(dir.exists()) || !(temp.exists())) {
      try {
        if (dir.mkdir()) {
          main.getLogger().log(Level.INFO, "[PlayerManager] created directory");
        } else {
          main.getLogger().log(Level.WARNING, "[PlayerManager] could not create directory");
        }
        if (temp.createNewFile()) {
          main.getLogger().log(Level.INFO, "[PlayerManager] created file Players/PlayerManager.yml");
        } else {
          main.getLogger()
              .log(Level.WARNING, "[PlayerManager] could not create file Players/PlayerManager.yml");
        }
        return false;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return true;
  }

  public void setMuted(UUID uuid, String reason, boolean b1) {
    getPlayerConfig().set(uuid + ".Muted", b1);
    getPlayerConfig().set(uuid + ".MutedReason", reason);
    try {
      getPlayerConfig().save(getPlayerFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isMuted(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return false;
    }
    return getPlayerConfig().getBoolean(uuid + ".Muted");
  }

  public String getMutedReason(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return "ERR_REASON_NOT_FOUND";
    }
    return getPlayerConfig().getString(uuid + ".MutedReason");
  }

  public void setFrozen(UUID uuid, String reason, boolean b1) {
    getPlayerConfig().set(uuid + ".Frozen", b1);
    getPlayerConfig().set(uuid + ".FrozenReason", reason);
    try {
      getPlayerConfig().save(getPlayerFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isFrozen(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return false;
    }
    return getPlayerConfig().getBoolean(uuid + ".Frozen");
  }

  public String getFrozenReason(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return "ERR_REASON_NOT_FOUND";
    }
    return getPlayerConfig().getString(uuid + ".FrozenReason");
  }

  public void setBanned(UUID uuid, String reason, boolean b1) {
    getPlayerConfig().set(uuid + ".Banned", b1);
    getPlayerConfig().set(uuid + ".BannedReason", reason);
    try {
      getPlayerConfig().save(getPlayerFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isBanned(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return false;
    }
    return getPlayerConfig().getBoolean(uuid + ".Banned");
  }

  public String getBannedReason(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return "ERR_REASON_NOT_FOUND";
    }
    return getPlayerConfig().getString(uuid + ".BannedReason");
  }

  public List<String> getWarns(UUID uuid) {
    if (!getPlayerConfig().contains(uuid.toString())) {
      return new ArrayList<>();
    }
    return getPlayerConfig().getStringList(uuid + ".Warns");
  }

  public void addWarn(UUID uuid, String playerName, String warnedName, String warn, String reason) {
    List<String> warns;
    if (!getPlayerConfig().contains(uuid.toString())) {
      warns = new ArrayList<>();
    } else {
      warns = getPlayerConfig().getStringList(uuid + ".Warns");
    }
    warns.add(warn);
    getPlayerConfig().set(uuid + ".Warns", warns);
    sendHoverTextMessage(playerName, warnedName, reason);
    try {
      getPlayerConfig().save(getPlayerFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void sendHoverTextMessage(String s1, String s2, String s3) {
    BaseComponent[] message = new ComponentBuilder(
        ChatColor.GRAY + "[" + ChatColor.DARK_RED + "TeamChat" + ChatColor.GRAY + "] "
            + ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(
                Objects.requireNonNull(loader.getConfig().getString("Messages.WarnNotification.MainMessage"))
                    .replace("%PLAYER%", s1).replace("%TARGET%", s2)))).event(
        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(
                    Objects.requireNonNull(loader.getConfig().getString("Messages.WarnNotification.HoverEvent"))
                        .replace("%REASON%", s3)))).create())).create();

    for (Player all : loader.getTeamChat()) {
      if (all.hasPermission(Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.WarnPlayers")))) {
        all.sendMessage(" ");
        all.spigot().sendMessage(message);
        all.sendMessage(" ");
        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
      }
    }
  }

  public File getPlayerFile() {
    return playerFile;
  }

  private void setPlayerFile(File playerFile) {
    this.playerFile = playerFile;
  }

  public FileConfiguration getPlayerConfig() {
    return playerConfig;
  }

  public void setPlayerConfig(FileConfiguration playerConfig) {
    this.playerConfig = playerConfig;
  }
}
