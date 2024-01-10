package host.plas.streamersunite.events;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.LiveManager;
import host.plas.streamersunite.managers.LogoutTimer;
import host.plas.streamersunite.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MainListener implements Listener {
    public MainListener() {
        Bukkit.getPluginManager().registerEvents(this, StreamersUnite.getInstance());

        MessageUtils.logInfo("Registered MainListener!");
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString()).isPresent()) {
            if (LiveManager.isLive(player)) {
                LogoutTimer.putTimedNow(player);
            }
        }
    }
}
