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

public class CMD_Profile implements CommandExecutor {


  private final String prefix, noPerm;
  private final Loader loader;

  public CMD_Profile(Main main) {
    Objects.requireNonNull(main.getCommand("profile")).setExecutor(this);
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

    if (!(p.hasPermission(Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.ViewProfile"))))) {
      p.sendMessage(noPerm);
      return true;
    }

    if(!(loader.getTeamChat().contains(p))) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.TeamChat.NotLoggedIn"))));
      return true;
    }

    if (args.length != 1) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Profile.Usage"))));
      return true;
    }

    UUID uuid = UUIDFetcher.getUUID(args[0]);

    if(uuid == null) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
              Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Profile.NoValidPlayer")))
          .replace("%PLAYER%", args[0]));
      return true;
    }

    loader.getGuiHandler().playerProfile(p, uuid);

    return true;
  }
}
