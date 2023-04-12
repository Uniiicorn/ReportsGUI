package com.cfunicorn.reportsgui.listeners;

import com.cfunicorn.reportsgui.main.Main;
import com.cfunicorn.reportsgui.utils.Loader;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListeners implements Listener {

  private final Loader loader;

  public ChatListeners(Main main) {
    Bukkit.getPluginManager().registerEvents(this, main);
    this.loader = main.getLoader();
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    String[] message = e.getMessage().split(" ");
    Player p = e.getPlayer();
    if (message.length >= 1) {
      String prefix = message[0];
      if (prefix.equalsIgnoreCase("@tc") || prefix.equalsIgnoreCase("@teamchat")) {
        e.setCancelled(true);

        if (!(loader.getTeamChat().contains(p))) {
          p.sendMessage(loader.getPrefix() + ChatColor.translateAlternateColorCodes('&',
              Objects.requireNonNull(loader.getConfig().getString("Messages.TeamChat.NotLoggedIn"))));
          return;
        }

        for (Player all : loader.getTeamChat()) {
          all.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "TeamChat" + ChatColor.GRAY + "] " + p.getName() + ":"
             + ChatColor.RESET +  e.getMessage().replace(prefix, ""));
        }

      }
    }
  }

}
