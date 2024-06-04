package dev.goldenedit.emojiadder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class EmojiAdder extends JavaPlugin implements Listener {
    private Map<String, String> emojis = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Ensure the config.yml is created and loaded.
        loadEmojiMappings(); // Load emoji mappings from the configuration.
        Bukkit.getPluginManager().registerEvents(this, this); // Register this class as an event listener.
    }

    private void loadEmojiMappings() {
        // Clear the current emoji mappings
        emojis.clear();

        // Load emoji mappings from the config
        if (getConfig().isConfigurationSection("emojis")) {
            for (String key : getConfig().getConfigurationSection("emojis").getKeys(false)) {
                String value = getConfig().getString("emojis." + key);
                if (value != null) {
                    emojis.put(key, value);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (event.getPlayer().hasPermission("emojichat.use")) {
            for (Map.Entry<String, String> entry : emojis.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
            event.setMessage(message);
        } else {
            // Check if any part of the message contains emojis from the list
            for (Map.Entry<String, String> entry : emojis.entrySet()) {
                if (message.contains(entry.getKey())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Emojis in chat messages is a " + ChatColor.GOLD + "Legend Rank " + ChatColor.RED +  "perk.");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/msg ") || command.startsWith("/tell ") || command.startsWith("/w ") || command.startsWith("/r ")) {
            String[] parts = command.split(" ", 3);
            if (parts.length > 2) {
                String modifiedMessage = parts[2];
                boolean modified = false;
                for (Map.Entry<String, String> entry : emojis.entrySet()) {
                    if (parts[2].contains(entry.getKey())) {
                        if (!event.getPlayer().hasPermission("emojichat.use")) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Emojis in direct messages is a " + ChatColor.GOLD + "Legend Rank " + ChatColor.RED +  "perk.");
                            return;
                        } else {
                            modifiedMessage = modifiedMessage.replace(entry.getKey(), entry.getValue());
                            modified = true;
                        }
                    }
                }
                if (modified) {
                    // Reassemble the command with the modified message
                    String newCommand = parts[0] + " " + parts[1] + " " + modifiedMessage;
                    event.setMessage(newCommand);
                }
            }
        }
    }
}
