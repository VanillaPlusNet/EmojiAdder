package dev.goldenedit.emojiadder;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        // Safely load and cast each emoji mapping
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
        if (!event.getPlayer().hasPermission("emojichat.use")) {
            for (String emoji : emojis.values()) {
                if (event.getMessage().contains(emoji)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use \"" + emoji + "\" in chat.");
                    return;
                }
            }
        } else {
            String message = event.getMessage();
            for (Map.Entry<String, String> entry : emojis.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
            event.setMessage(message);
        }
    }
}
