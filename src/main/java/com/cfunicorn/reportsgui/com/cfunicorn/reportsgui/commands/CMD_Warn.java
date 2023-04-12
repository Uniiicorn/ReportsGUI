package com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands;

import com.cfunicorn.reportsgui.main.Main;
import com.cfunicorn.reportsgui.utils.Loader;
import com.cfunicorn.reportsgui.utils.UUIDFetcher;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CMD_Warn implements CommandExecutor {

  private final String prefix, noPerm;
  private final Loader loader;

  public CMD_Warn(Main main) {
    Objects.requireNonNull(main.getCommand("warn")).setExecutor(this);
    this.loader = main.getLoader();
    this.prefix = loader.getPrefix();
    this.noPerm = loader.getNoPerm();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
      @NotNull String[] args) {

    if (!(sender instanceof Player p)) {
      sender.sendMessage(noPerm);
      return true;
    }

    if (!(p.hasPermission(Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.ViewReports"))))) {
      p.sendMessage(noPerm);
      return true;
    }

    if (!(loader.getTeamChat().contains(p))) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.TeamChat.NotLoggedIn"))));
      return true;
    }

    if (args.length != 2) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Warn.Usage"))));
      return true;
    }

    String reason = null;
    UUID uuid = UUIDFetcher.getUUID(args[0]);

    if (uuid == null) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
              Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Warn.NoValidPlayer")))
          .replace("%PLAYER%", args[0]));
      return true;
    }

    for (String s : loader.getReasons()) {
      if (s.equalsIgnoreCase(args[1])) {
        reason = s;
      }
    }

    if (reason == null) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
              Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Warn.NoValidReason")))
          .replace("%REASON%", args[1]));
      return true;
    }

    String warn = "&7#" + (loader.getPlayerHandler().getWarns(uuid).size() + 1) + " &c" + reason + "&7 - "
        + p.getName();
    loader.getPlayerHandler().addWarn(uuid, p.getName(), args[0], warn, reason);

    p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Warn.WarnSuccessful")))
        .replace("%REASON%", args[1]).replace("%PLAYER%", args[0]));
    return true;
  }
}
