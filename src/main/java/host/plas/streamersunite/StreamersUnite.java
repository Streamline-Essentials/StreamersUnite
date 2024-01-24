package host.plas.streamersunite;

import host.plas.streamersunite.commands.*;
import host.plas.streamersunite.config.MainConfig;
import host.plas.streamersunite.config.StreamerConfig;
import host.plas.streamersunite.data.LiveManager;
import host.plas.streamersunite.events.MainListener;
import io.streamlined.bukkit.PluginBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentSkipListMap;

@Getter @Setter
public final class StreamersUnite extends PluginBase {
    @Getter @Setter
    private static StreamersUnite instance;

    @Getter @Setter
    private static MainConfig mainConfig;
    @Getter @Setter
    private static StreamerConfig streamerConfig;

    @Getter @Setter
    private static SetUpStreamerCMD streamerCMD;
    @Getter @Setter
    private static GoLiveCMD goLiveCMD;
    @Getter @Setter
    private static GoOfflineCMD offlineCMD;
    @Getter @Setter
    private static CheckLiveCMD checkLiveCMD;
    @Getter @Setter
    private static BroadcastLiveCMD broadcastLiveCMD;

    @Getter @Setter
    private static MainListener mainListener;

    public StreamersUnite() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setInstance(this);

        setMainConfig(new MainConfig());
        setStreamerConfig(new StreamerConfig());

        setStreamerCMD(new SetUpStreamerCMD());
        getStreamerCMD().register();
        setGoLiveCMD(new GoLiveCMD());
        getGoLiveCMD().register();
        setOfflineCMD(new GoOfflineCMD());
        getOfflineCMD().register();
        setCheckLiveCMD(new CheckLiveCMD());
        getCheckLiveCMD().register();
        setBroadcastLiveCMD(new BroadcastLiveCMD());
        getBroadcastLiveCMD().register();

        setMainListener(new MainListener());
        Bukkit.getPluginManager().registerEvents(getMainListener(), StreamersUnite.getInstance());
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
        LiveManager.getCurrentlyLive().clear();
    }

    /**
     * Get a map of online players.
     * Sorted by player name.
     * @return A map of online players sorted by player name.
     */
    public static ConcurrentSkipListMap<String, Player> getOnlinePlayers() {
        ConcurrentSkipListMap<String, Player> onlinePlayers = new ConcurrentSkipListMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.put(player.getName(), player);
        }

        return onlinePlayers;
    }
}
