package com.cfunicorn.reportsgui.listeners;

import com.cfunicorn.reportsgui.main.Main;
import com.cfunicorn.reportsgui.utils.Loader;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SelectReason implements Listener {

  private final Loader loader;
  private final String prefix;

  public SelectReason(Main main) {
    Bukkit.getPluginManager().registerEvents(this, main);
    this.loader = main.getLoader();
    this.prefix = loader.getPrefix();
  }

  @EventHandler
  public void on(InventoryClickEvent e) {
    try {
      if (e.getView().getTitle().contains(ChatColor.RED + "Report >> ")) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        String reason = ChatColor.stripColor(
            Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getDisplayName());
        String target = ChatColor.stripColor(e.getView().getTitle()).replace("Report >> ", "");

        if (loader.getReportManager().createReport(p, target, reason)) {
          p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Report.ReportSuccessful")))
              .replace("%PLAYER%", target).replace("%REASON%", reason));
        } else {
          p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                  Objects.requireNonNull(loader.getConfig().getString("Messages.CMD_Report.NoValidPlayer")))
              .replace("%PLAYER%", target));
        }

        p.closeInventory();

      }
    } catch (NullPointerException ignored) {
      ignored.printStackTrace();
    }
  }

}
