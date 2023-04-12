package com.cfunicorn.reportsgui.listeners;

import com.cfunicorn.reportsgui.main.Main;
import com.cfunicorn.reportsgui.utils.Loader;
import com.cfunicorn.reportsgui.utils.NameFetcher;
import com.cfunicorn.reportsgui.utils.ReportManager.Report;
import com.cfunicorn.reportsgui.utils.UUIDFetcher;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ReportListeners implements Listener {

  private final Loader loader;
  private final String prefix;
  private final String noPerm;

  public ReportListeners(Main main) {
    Bukkit.getPluginManager().registerEvents(this, main);
    this.loader = main.getLoader();
    this.prefix = loader.getPrefix();
    this.noPerm = loader.getNoPerm();
  }

  @EventHandler
  public void on(InventoryClickEvent e) {
    try {

      Player p = (Player) e.getWhoClicked();

      if (e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "Reports")) {
        e.setCancelled(true);

        int id = Integer.parseInt(Objects.requireNonNull(
                Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getLore())
            .get(0).replace(ChatColor.GRAY + "ID: " + ChatColor.RED, ""));

        loader.getGuiHandler().reportDetails(p, id, e.getCurrentItem());

      }

      if (e.getView().getTitle().contains(ChatColor.RED + "Reports" + ChatColor.GRAY + " >> ")) {
        e.setCancelled(true);

        int id = Integer.parseInt(
            e.getView().getTitle().replace(ChatColor.RED + "Reports" + ChatColor.GRAY + " >> " + ChatColor.RED, ""));

        Report report = loader.getReportManager().getReports().get(id - 1);

        switch (e.getSlot()) {
          case 10 -> banPlayer(p, report, 1, false);
          case 11 -> banPlayer(p, report, 7, false);
          case 12 -> banPlayer(p, report, 30, false);
          case 13 -> banPlayer(p, report, 0, true);
          case 14 -> {
            Player target = Bukkit.getPlayer(report.getReported());
            if (!(target == null)) {
              target.kickPlayer(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(
                      loader.getConfig().getString("Messages.Kicked"))
                  .replace("%REASON%", report.getReason())));
            }
            loader.getReportManager().deleteReport(report);
            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(loader.getConfig().getString("Messages.ReportHandled"))
                    .replace("%ID%", String.valueOf(report.getId()))));
            p.closeInventory();
          }
          case 15 -> {
            Player target = Bukkit.getPlayer(report.getReported());
            if (!(target == null)) {
              p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(
                      loader.getConfig().getString("Messages.Frozen"))
                  .replace("%REASON%", report.getReason())));
            }
            loader.getReportManager().deleteReport(report);
            loader.getPlayerHandler().setFrozen(report.getReported(), report.getReason(), true);
            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(loader.getConfig().getString("Messages.ReportHandled"))
                    .replace("%ID%", String.valueOf(report.getId()))));
            p.closeInventory();
          }
          case 16 -> {
            loader.getPlayerHandler().setMuted(report.getReported(), report.getReason(), true);
            loader.getReportManager().deleteReport(report);
            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(loader.getConfig().getString("Messages.ReportHandled"))
                    .replace("%ID%", String.valueOf(report.getId()))));
            p.closeInventory();
          }
          case 18 -> {
            Player target = Bukkit.getPlayer(report.getReported());
            if (!(target == null)) {
              p.teleport(target.getLocation());
              p.closeInventory();
            }
          }
          case 26 -> {
            loader.getReportManager().deleteReport(report);
            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(loader.getConfig().getString("Messages.ReportHandled"))
                    .replace("%ID%", String.valueOf(report.getId()))));

            p.closeInventory();
          }
        }
      } else if (e.getView().getTitle().contains(ChatColor.RED + "Profile" + ChatColor.GRAY + " >> ")) {
        e.setCancelled(true);

        String name = e.getView().getTitle()
            .replace(ChatColor.RED + "Profile" + ChatColor.GRAY + " >> " + ChatColor.RED, "");
        UUID uuid = UUIDFetcher.getUUID(name);
        assert uuid != null;

        switch (e.getSlot()) {
          case 10 -> {
            if (!(loader.getPlayerHandler().isBanned(uuid))) {
              long duration = loader.getReportManager().parseDuration("3650d");
              Date expiration = new Date(System.currentTimeMillis() + duration);
              BanList banList = Bukkit.getBanList(BanList.Type.NAME);
              banList.addBan(name, ChatColor.RED + "Administrative Decision", expiration, p.getName());
              Player target = Bukkit.getPlayer(uuid);
              if (target != null) {
                target.kickPlayer(ChatColor.RED + "Administrative Decision");
              }
              loader.getPlayerHandler()
                  .setBanned(uuid, "Administrative Decision", !loader.getPlayerHandler().isBanned(uuid));
              p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.PlayerPunished"))
                      .replace("%PLAYER%", name)));
              loader.getGuiHandler().playerProfile(p, uuid);
            } else {
              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pardon " + name);
              loader.getPlayerHandler()
                  .setBanned(uuid, "", !loader.getPlayerHandler().isBanned(uuid));
              loader.getGuiHandler().playerProfile(p, uuid);
            }
          }
          case 12 -> {
            loader.getPlayerHandler()
                .setMuted(uuid, "Administrative Decision", !loader.getPlayerHandler().isMuted(uuid));
            if (loader.getPlayerHandler().isMuted(uuid)) {
              p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.PlayerPunished"))
                      .replace("%PLAYER%", name)));
            }
            loader.getGuiHandler().playerProfile(p, uuid);
          }
          case 14 -> {
            loader.getPlayerHandler()
                .setFrozen(uuid, "Administrative Decision", !loader.getPlayerHandler().isFrozen(uuid));
            if (loader.getPlayerHandler().isFrozen(uuid)) {
              p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.PlayerPunished"))
                      .replace("%PLAYER%", name)));
            }
            loader.getGuiHandler().playerProfile(p, uuid);
          }
          case 16 -> {
            Player target = Bukkit.getPlayer(uuid);
            if (target != null) {
              p.teleport(target);
              p.closeInventory();
            }
          }
        }

      }


    } catch (NullPointerException ignored) {

    }
  }

  @EventHandler
  public void on(AsyncPlayerChatEvent e) {
    if (loader.getPlayerHandler().isMuted(e.getPlayer().getUniqueId())) {
      e.setCancelled(true);

      String msg = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(
              loader.getConfig().getString("Messages.Muted"))
          .replace("%REASON%", loader.getPlayerHandler().getMutedReason(e.getPlayer()
              .getUniqueId())));

      e.getPlayer().sendMessage(prefix + msg);

    }
  }

  @EventHandler
  public void on(PlayerMoveEvent e) {
    if (loader.getPlayerHandler().isFrozen(e.getPlayer().getUniqueId())) {
      e.setCancelled(true);
    }
  }

  private void banPlayer(Player p, Report report, int i, boolean isPermanent) {
    long duration;
    String msg;
    Date expiration;

    Player target = Bukkit.getPlayer(report.getReported());

    if (isPermanent) {
      if (!(p.hasPermission(
          Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.HandleReports.Ban.Permanent"))))) {
        p.sendMessage(noPerm);
        return;
      }
      duration = loader.getReportManager().parseDuration("3650d");
      expiration = new Date(System.currentTimeMillis() + duration);
      msg = ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.Banned"))
              .replace("%REASON%", report.getReason())
              .replace("%DATE%", "Permanent"));
    } else {
      if (!(p.hasPermission(
          Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.HandleReports.Ban.Temporary"))))) {
        p.sendMessage(noPerm);
        return;
      }
      duration = loader.getReportManager().parseDuration(i + "d");
      expiration = new Date(System.currentTimeMillis() + duration);
      msg = ChatColor.translateAlternateColorCodes('&',
          Objects.requireNonNull(loader.getConfig().getString("Messages.Banned"))
              .replace("%REASON%", report.getReason())
              .replace("%DATE%", expiration.toString()));

    }

    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
    banList.addBan(NameFetcher.getName(report.getReported()), msg, expiration, p.getName());

    loader.getReportManager().deleteReport(report);
    loader.getPlayerHandler()
        .setBanned(report.getReported(), report.getReason(), !loader.getPlayerHandler().isBanned(report.getReported()));

    p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.ReportHandled"))
            .replace("%ID%", String.valueOf(report.getId()))));
    if (!(target == null)) {
      target.kickPlayer(msg);
    }

    p.closeInventory();
  }

}
