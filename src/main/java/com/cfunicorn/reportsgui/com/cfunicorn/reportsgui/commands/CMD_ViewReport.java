package com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands;

import com.cfunicorn.reportsgui.main.Main;
import com.cfunicorn.reportsgui.utils.Loader;
import com.cfunicorn.reportsgui.utils.NameFetcher;
import com.cfunicorn.reportsgui.utils.ReportManager.Report;
import java.util.List;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CMD_ViewReport implements CommandExecutor {

  private final String prefix, noPerm;
  private final Loader loader;

  public CMD_ViewReport(Main main) {
    Objects.requireNonNull(main.getCommand("viewreport")).setExecutor(this);
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

    if (args.length != 1) {
      return true;
    }

    try {

      int id = Integer.parseInt(args[0]);
      Report report = loader.getReportManager().getReports().get(id -1);
      List<String> lore = List.of(ChatColor.GRAY + "ID: " + ChatColor.RED + id,
          ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ViewReports.ReportedBy")))
              .replace("%PLAYER%", NameFetcher.getName(report.getIssuer())),
          ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ViewReports.ReportedReason")))
              .replace("%REASON%", report.getReason()),
          ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ViewReports.ReportedTime")))
              .replace("%TIMESTAMP%", report.getTimeStamp())
      );

      loader.getGuiHandler().reportDetails(p, id, loader.getGuiHandler()
          .createSkull(report.getReported(), ChatColor.GRAY + NameFetcher.getName(report.getReported()), lore));
      return true;
    } catch (Exception ignored) {
    }

    return true;
  }
}
