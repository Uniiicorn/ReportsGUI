package com.cfunicorn.reportsgui.utils;

import com.cfunicorn.reportsgui.utils.ReportManager.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GUIHandler {

  private final Loader loader;
  private final ReportManager reportManager;

  public GUIHandler(Loader loader) {
    this.loader = loader;
    this.reportManager = loader.getReportManager();
  }

  public void viewReports(Player p) {
    Inventory inv = Bukkit.createInventory(null, 54, ChatColor.RED + "Reports");

    if (!(reportManager.getReports().isEmpty())) {

      int count = 0;
      for (Report report : reportManager.getReports()) {

        UUID reported = report.getReported();
        String reportedName = NameFetcher.getName(report.getReported());
        String issuerName = NameFetcher.getName(report.getIssuer());
        String reason = report.getReason();
        String timeStamp = report.getTimeStamp();
        int id = report.getId();

        List<String> lore = List.of(ChatColor.GRAY + "ID: " + ChatColor.RED + id,
            ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ViewReports.ReportedBy")))
                .replace("%PLAYER%", issuerName), ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ViewReports.ReportedReason")))
                .replace("%REASON%", reason), ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ViewReports.ReportedTime")))
                .replace("%TIMESTAMP%", timeStamp));

        count++;

        inv.addItem(createSkull(reported, ChatColor.GRAY + reportedName, lore));

        if (count == 54) {
          break;
        }
      }

    }

    p.openInventory(inv);

  }

  public void reportDetails(Player p, int reportId, ItemStack playerHead) {

    Inventory inv = Bukkit.createInventory(null, 27,
        ChatColor.RED + "Reports" + ChatColor.GRAY + " >> " + ChatColor.RED + reportId);

    for (int i = 0; i < 27; i++) {
      inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null));
    }

    Report report = loader.getReportManager().getReports().get(reportId - 1);

    String banTime = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ReportDetails.BanPlayer")));
    String mutePlayer = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ReportDetails.MutePlayer")));
    String kickPlayer = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ReportDetails.KickPlayer")));
    String freezePlayer = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ReportDetails.FreezePlayer")));
    String deleteReport = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ReportDetails.DeleteReport")));
    String teleportPlayer = ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.ReportDetails.TeleportPlayer")))
        .replace("%PLAYER%", NameFetcher.getName(report.getReported()));

    List<String> warnLore = new ArrayList<>();
    for (String s : loader.getPlayerHandler().getWarns(report.getReported())) {
      warnLore.add(ChatColor.translateAlternateColorCodes('&', s));
    }

    inv.setItem(0, playerHead);
    inv.setItem(1, createItem(Material.OAK_SIGN, ChatColor.GRAY + "Warns", warnLore));

    inv.setItem(10, createItem(Material.RED_STAINED_GLASS_PANE, banTime.replace("%DURATION%", "1d"), null));
    inv.setItem(11, createItem(Material.RED_STAINED_GLASS_PANE, banTime.replace("%DURATION%", "7d"), null));
    inv.setItem(12, createItem(Material.RED_STAINED_GLASS_PANE, banTime.replace("%DURATION%", "30d"), null));
    inv.setItem(13, createItem(Material.RED_STAINED_GLASS_PANE, banTime.replace("%DURATION%", "Permanent"), null));
    inv.setItem(14, createItem(Material.ORANGE_STAINED_GLASS_PANE, kickPlayer, null));
    inv.setItem(15, createItem(Material.ORANGE_STAINED_GLASS_PANE, freezePlayer, null));
    inv.setItem(16, createItem(Material.ORANGE_STAINED_GLASS_PANE, mutePlayer, null));

    inv.setItem(18, createItem(Material.ENDER_PEARL, teleportPlayer, null));

    inv.setItem(26, createItem(Material.BARRIER, deleteReport, null));

    p.openInventory(inv);
  }

  public void playerProfile(Player p, UUID uuid) {

    String name = NameFetcher.getName(uuid);

    Inventory inv = Bukkit.createInventory(null, 27,
        ChatColor.RED + "Profile" + ChatColor.GRAY + " >> " + ChatColor.RED + name);

    for (int i = 0; i < 27; i++) {
      inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null));
    }

    String active = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.Profile.Active")));
    String notActive = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.Profile.NotActive")));
    String teleportPlayer = ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.Profile.TeleportPlayer")))
        .replace("%PLAYER%", name);
    String banned = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.Profile.BanStatus.Title")));
    List<String> bannedLore = new ArrayList<>();
    bannedLore.add(" ");
    for (String s : loader.getConfig().getStringList("Messages.GUIs.Profile.BanStatus.Description")) {
      bannedLore.add(ChatColor.translateAlternateColorCodes('&', s));
    }
    if (loader.getPlayerHandler().isBanned(uuid)) {
      bannedLore.add(active);
      bannedLore.add(ChatColor.GRAY + loader.getPlayerHandler().getBannedReason(uuid));
    } else {
      bannedLore.add(notActive);
    }
    String muted = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.Profile.MuteStatus.Title")));
    List<String> mutedLore = new ArrayList<>();
    mutedLore.add(" ");
    for (String s : loader.getConfig().getStringList("Messages.GUIs.Profile.MuteStatus.Description")) {
      mutedLore.add(ChatColor.translateAlternateColorCodes('&', s));
    }
    if (loader.getPlayerHandler().isMuted(uuid)) {
      mutedLore.add(active);
      mutedLore.add(ChatColor.GRAY + loader.getPlayerHandler().getMutedReason(uuid));
    } else {
      mutedLore.add(notActive);
    }
    String frozen = ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.GUIs.Profile.FreezeStatus.Title")));
    List<String> frozenLore = new ArrayList<>();
    frozenLore.add(" ");
    for (String s : loader.getConfig().getStringList("Messages.GUIs.Profile.FreezeStatus.Description")) {
      frozenLore.add(ChatColor.translateAlternateColorCodes('&', s));
    }
    if (loader.getPlayerHandler().isFrozen(uuid)) {
      frozenLore.add(active);
      frozenLore.add(ChatColor.GRAY + loader.getPlayerHandler().getFrozenReason(uuid));
    } else {
      frozenLore.add(notActive);
    }
    List<String> warnLore = new ArrayList<>();
    warnLore.add(" ");
    for (String s : loader.getPlayerHandler().getWarns(uuid)) {
      warnLore.add(ChatColor.translateAlternateColorCodes('&', s));
    }
    inv.setItem(0, createSkull(uuid, ChatColor.GRAY + name, warnLore));

    inv.setItem(10, createItem(Material.PAPER, banned, bannedLore));
    inv.setItem(12, createItem(Material.PAPER, muted, mutedLore));
    inv.setItem(14, createItem(Material.PAPER, frozen, frozenLore));
    inv.setItem(16, createItem(Material.ENDER_PEARL, teleportPlayer, null));

    p.openInventory(inv);
  }

  public void selectReason(Player p, String target) {

    Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Report >> " + ChatColor.GRAY + target);
    int reasons = loader.getReasons().size();

    if (reasons <= 5) {
      inv = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.RED + "Report >> " + ChatColor.GRAY + target);
    }
    if (reasons <= 9 && reasons > 5) {
      inv = Bukkit.createInventory(null, 9, ChatColor.RED + "Report >> " + ChatColor.GRAY + target);
    }
    if (reasons <= 18 && reasons > 9) {
      inv = Bukkit.createInventory(null, 18, ChatColor.RED + "Report >> " + ChatColor.GRAY + target);
    }
    if (reasons <= 27 && reasons > 18) {
      inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Report >> " + ChatColor.GRAY + target);
    }

    System.out.println(loader.getReasons());
    for (String s : loader.getReasons()) {
      System.out.println(s);
      inv.addItem(createItem(Material.PAPER, ChatColor.RED + s, new ArrayList<>()));
    }

    p.openInventory(inv);
  }

  public ItemStack createItem(Material material, String displayName, List<String> lore) {
    ItemStack itemStack = new ItemStack(material);
    ItemMeta itemMeta = itemStack.getItemMeta();
    assert itemMeta != null;
    itemMeta.setDisplayName(displayName);
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);

    return itemStack;
  }

  public ItemStack createSkull(UUID owner, String displayName, List<String> lore) {
    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
    assert itemMeta != null;
    itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
    itemMeta.setDisplayName(displayName);
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);

    return itemStack;
  }

}
