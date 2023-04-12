package com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands;

import com.cfunicorn.reportsgui.main.Main;
import com.cfunicorn.reportsgui.utils.Loader;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CMD_TeamChat implements CommandExecutor {


  private final String noPerm, prefix;
  private final Loader loader;

  public CMD_TeamChat(Main main) {
    Objects.requireNonNull(main.getCommand("teamchat")).setExecutor(this);
    this.loader = main.getLoader();
    this.noPerm = loader.getNoPerm();
    this.prefix = loader.getPrefix();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
      @NotNull String[] args) {

    if (!(sender instanceof Player p)) {
      sender.sendMessage(noPerm);
      return true;
    }

    if (!(p.hasPermission(Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.TeamChat"))))) {
      p.sendMessage(noPerm);
      return true;
    }

    if(loader.getTeamChat().contains(p)) {
      loader.getTeamChat().remove(p);
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.TeamChat.LoggedOut"))));
    } else {
      loader.getTeamChat().add(p);
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.TeamChat.LoggedIn"))));
    }

    return true;
  }
}
