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

public class CMD_Report implements CommandExecutor {

  private final String prefix, noPerm;
  private final Loader loader;

  public CMD_Report(Main main) {
    Objects.requireNonNull(main.getCommand("report")).setExecutor(this);
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

    if (args.length != 1) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Report.Usage"))));
      return true;
    }

    String target = args[0];

    if (!(loader.getReportManager().canReport(target))) {
      p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
              Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Report.CantReport")))
          .replace("%PLAYER%", args[0]));
      return true;
    }

    loader.getGuiHandler().selectReason(p, target);
    return true;

  }
}
