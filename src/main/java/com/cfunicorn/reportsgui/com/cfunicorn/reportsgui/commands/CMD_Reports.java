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

public class CMD_Reports implements CommandExecutor {

  private final String prefix, noPerm;
  private final Loader loader;

  public CMD_Reports(Main main) {
    Objects.requireNonNull(main.getCommand("reports")).setExecutor(this);
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

    if(!(loader.getTeamChat().contains(p))) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.TeamChat.NotLoggedIn"))));
      return true;
    }

    loader.getGuiHandler().viewReports(p);

    return true;
  }
}
