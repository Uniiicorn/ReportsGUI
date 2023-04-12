package com.cfunicorn.reportsgui.utils;

import com.cfunicorn.reportsgui.main.Main;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ReportManager {

  public static class Report {

    private UUID reported, issuer;
    private String reason, timeStamp;
    private int id;

    public Report(UUID reported, UUID issuer, String reason, String timeStamp, int id) {
      setReported(reported);
      setIssuer(issuer);
      setReason(reason);
      setTimeStamp(timeStamp);
      setId(id);
    }

    public UUID getReported() {
      return reported;
    }

    private void setReported(UUID reported) {
      this.reported = reported;
    }

    public UUID getIssuer() {
      return issuer;
    }

    private void setIssuer(UUID issuer) {
      this.issuer = issuer;
    }

    public String getReason() {
      return reason;
    }

    private void setReason(String reason) {
      this.reason = reason;
    }

    public String getTimeStamp() {
      return timeStamp;
    }

    private void setTimeStamp(String timeStamp) {
      this.timeStamp = timeStamp;
    }

    public int getId() {
      return id;
    }

    private void setId(int id) {
      this.id = id;
    }
  }

  private final Loader loader;
  private List<Report> reports;
  private File reportsFile;
  private YamlConfiguration reportsConfig;

  public ReportManager(Loader loader) {
    this.loader = loader;
    checkForFile();

    loadReports();
  }

  private void checkForFile() {
    File tempFolder = new File(Main.getMain().getDataFolder() + "/Reports");
    File tempFile = new File(Main.getMain().getDataFolder() + "/Reports/", "ReportManager.yml");

    if (!(tempFolder.exists())) {
      if (!(tempFolder.mkdir())) {
        Main.getMain().getLogger().log(Level.WARNING, "An error occurred trying to create Reports-Directory.");
      } else {
        Main.getMain().getLogger().log(Level.INFO, "Successfully created directory \"Reports\"");
      }
    }

    if (!(tempFile.exists())) {

      try {
        if (tempFile.createNewFile()) {
          Main.getMain().getLogger().log(Level.INFO, "Successfully created File \"Reports/ReportsManager.yml\"");
          YamlConfiguration config = YamlConfiguration.loadConfiguration(tempFile);

          config.set("REPORTS_COUNT", 0);
          config.save(tempFile);
        }
      } catch (IOException e) {
        Main.getMain().getLogger().log(Level.WARNING, "An error occurred trying to create Reports/ReportsManager.yml.");
      }

    }

    setReportsFile(tempFile);
    setReportsConfig(YamlConfiguration.loadConfiguration(getReportsFile()));

  }

  private void loadReports() {

    int length = getReportsConfig().getInt("REPORTS_COUNT");
    List<Report> reports = new ArrayList<>();

    for (int i = 0; i <= length; i++) {
      if (getReportsConfig().getString("Reports." + i + ".Reported") == null) {
        continue;
      }
      UUID reported = UUID.fromString(
          Objects.requireNonNull(getReportsConfig().getString("Reports." + i + ".Reported")));
      UUID issuer = UUID.fromString(Objects.requireNonNull(getReportsConfig().getString("Reports." + i + ".Issuer")));
      String reason = Objects.requireNonNull(getReportsConfig().getString("Reports." + i + ".Reason"));
      String timeStamp = Objects.requireNonNull(getReportsConfig().getString("Reports." + i + ".TimeStamp"));

      Report report = new Report(reported, issuer, reason, timeStamp, i);
      reports.add(report);

    }

    setReports(reports);

  }

  /**
   * @param s1 The Reported's Name
   * @param s2 The Reason
   * @return true if the Report was successful, will return false if there was an error
   */
  public boolean createReport(Player p, String s1, String s2) {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(
        Objects.requireNonNull(loader.getConfig().getString("Settings.DateTimeFormat")));
    LocalDateTime now = LocalDateTime.now();
    String timeStamp = dtf.format(now);

    UUID reported = UUIDFetcher.getUUID(s1);
    if (reported == null) {
      return false;
    }

    int id = getReportsConfig().getInt("REPORTS_COUNT") + 1;

    Report report = new Report(reported, p.getUniqueId(), s2, timeStamp, id);
    getReports().add(report);

    getReportsConfig().set("Reports." + id + ".Reported", reported.toString());
    getReportsConfig().set("Reports." + id + ".Issuer", p.getUniqueId().toString());
    getReportsConfig().set("Reports." + id + ".Reason", s2);
    getReportsConfig().set("Reports." + id + ".TimeStamp", timeStamp);
    getReportsConfig().set("REPORTS_COUNT", id);

    saveConfig();

    sendHoverTextMessage(id);

    return true;

  }

  private void sendHoverTextMessage(int id) {
    BaseComponent[] message = new ComponentBuilder(
        ChatColor.GRAY + "[" + ChatColor.DARK_RED + "TeamChat" + ChatColor.GRAY + "] " + ChatColor.RESET + ""
            + ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(loader.getConfig().getString("Messages.ReportNotification.MainMessage")))).create();

    BaseComponent[] message1 = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(loader.getConfig().getString("Messages.ReportNotification.ClickEvent")))).event(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewreport " + id)).event(
        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder(ChatColor.GRAY + "Report-ID: " + ChatColor.RED + id).create())).create();

    BaseComponent[] combinedMessage = new ComponentBuilder().append(message).append(message1).create();

    for (Player all : loader.getTeamChat()) {
      if (all.hasPermission(Objects.requireNonNull(loader.getConfig().getString("Settings.Permissions.ViewReports")))) {
        all.sendMessage(" ");
        all.spigot().sendMessage(combinedMessage);
        all.sendMessage(" ");
        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
      }
    }
  }

  public void deleteReport(Report report) {

    getReportsConfig().set("Reports." + report.getId(), "");
    getReportsConfig().set("REPORTS_COUNT", getReportsConfig().getInt("REPORTS_COUNT") - 1);
    saveConfig();

    getReports().remove(report.getId() - 1);

  }

  /**
   * @param s1 The Reported's Name
   * @return false if the player has already been reported
   */
  public boolean canReport(String s1) {

    for (Report report : getReports()) {
      if (report.getReported().equals(UUIDFetcher.getUUID(s1))) {
        return false;
      }
    }

    return true;

  }

  public long parseDuration(String durationString) {
    long duration;
    try {
      duration = Long.parseLong(durationString) * 1000;
    } catch (NumberFormatException e) {
      duration = -1;
    }

    if (duration <= 0) {
      String[] durationParts = durationString.split("(?<=\\d)(?=\\D)");
      if (durationParts.length == 2) {
        int amount;
        try {
          amount = Integer.parseInt(durationParts[0]);
        } catch (NumberFormatException e) {
          return -1;
        }
        String unit = durationParts[1].toLowerCase();
        duration = switch (unit) {
          case "s" -> amount * 1000L;
          case "m" -> (long) amount * 60 * 1000;
          case "h" -> (long) amount * 60 * 60 * 1000;
          case "d" -> (long) amount * 24 * 60 * 60 * 1000;
          default -> -1;
        };
      }
    }

    return duration;
  }

  private void saveConfig() {
    try {

      getReportsConfig().save(getReportsFile());

    } catch (IOException ignored) {

    }
  }

  public List<Report> getReports() {
    return reports;
  }

  private void setReports(List<Report> reports) {
    this.reports = reports;
  }

  public File getReportsFile() {
    return reportsFile;
  }

  private void setReportsFile(File reportsFile) {
    this.reportsFile = reportsFile;
  }

  public YamlConfiguration getReportsConfig() {
    return reportsConfig;
  }

  private void setReportsConfig(YamlConfiguration reportsConfig) {
    this.reportsConfig = reportsConfig;
  }
}
