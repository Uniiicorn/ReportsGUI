package com.cfunicorn.reportsgui.utils;

import com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands.CMD_Profile;
import com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands.CMD_Report;
import com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands.CMD_Reports;
import com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands.CMD_TeamChat;
import com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands.CMD_ViewReport;
import com.cfunicorn.reportsgui.com.cfunicorn.reportsgui.commands.CMD_Warn;
import com.cfunicorn.reportsgui.listeners.ChatListeners;
import com.cfunicorn.reportsgui.listeners.ReportListeners;
import com.cfunicorn.reportsgui.listeners.SelectReason;
import com.cfunicorn.reportsgui.main.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Loader {

  private String prefix, noPerm;
  private File configFile;
  private FileConfiguration config;
  private List<String> reasons;
  private List<Player> teamChat;
  private ReportManager reportManager;
  private GUIHandler guiHandler;
  private PlayerHandler playerHandler;

  public Loader(Main main) {
    setConfigFile(new File(main.getDataFolder(), "config.yml"));
    setConfig(YamlConfiguration.loadConfiguration(getConfigFile()));

    setPrefix(
        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("Settings.Prefix")))
            + ChatColor.RESET + " ");

    setNoPerm(getPrefix() + ChatColor.translateAlternateColorCodes('&',
        Objects.requireNonNull(getConfig().getString("Settings.NoPerm"))));

    setReasons(getConfig().getStringList("ReportReasons"));
    setTeamChat(new ArrayList<>());

    setReportManager(new ReportManager(this));
    setGuiHandler(new GUIHandler(this));
    setPlayerHandler(new PlayerHandler(this));
  }

  public void registerListeners(Main main) {
    new SelectReason(main);
    new ReportListeners(main);
    new ChatListeners(main);
  }

  public void registerCommands(Main main) {
    new CMD_Report(main);
    new CMD_Reports(main);
    new CMD_ViewReport(main);
    new CMD_TeamChat(main);
    new CMD_Warn(main);
    new CMD_Profile(main);
  }


  public void saveConfig() {
    try {

      getConfig().save(getConfigFile());

    } catch (IOException ex) {
      throw new RuntimeException();
    }
  }

  public String getPrefix() {
    return prefix;
  }

  private void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getNoPerm() {
    return noPerm;
  }

  private void setNoPerm(String noPerm) {
    this.noPerm = noPerm;
  }

  public File getConfigFile() {
    return configFile;
  }

  private void setConfigFile(File configFile) {
    this.configFile = configFile;
  }

  public FileConfiguration getConfig() {
    return config;
  }

  private void setConfig(FileConfiguration config) {
    this.config = config;
  }

  public List<String> getReasons() {
    return reasons;
  }

  private void setReasons(List<String> reasons) {
    this.reasons = reasons;
  }

  public ReportManager getReportManager() {
    return reportManager;
  }

  private void setReportManager(ReportManager reportManager) {
    this.reportManager = reportManager;
  }

  public GUIHandler getGuiHandler() {
    return guiHandler;
  }

  private void setGuiHandler(GUIHandler guiHandler) {
    this.guiHandler = guiHandler;
  }

  public PlayerHandler getPlayerHandler() {
    return playerHandler;
  }

  private void setPlayerHandler(PlayerHandler playerHandler) {
    this.playerHandler = playerHandler;
  }

  public List<Player> getTeamChat() {
    return teamChat;
  }

  private void setTeamChat(List<Player> teamChat) {
    this.teamChat = teamChat;
  }
}
