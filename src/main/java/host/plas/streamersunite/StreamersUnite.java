package host.plas.streamersunite;

import host.plas.streamersunite.commands.CheckLiveCMD;
import host.plas.streamersunite.commands.LiveCMD;
import host.plas.streamersunite.commands.OfflineCMD;
import host.plas.streamersunite.commands.StreamerCMD;
import host.plas.streamersunite.config.StreamerConfig;
import host.plas.streamersunite.events.MainListener;
import io.streamlined.bukkit.PluginBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter @Setter
public final class StreamersUnite extends PluginBase {
    @Getter @Setter
    private static StreamersUnite instance;

    @Getter @Setter
    private static StreamerConfig streamerConfig;

    @Getter @Setter
    private static StreamerCMD streamerCMD;
    @Getter @Setter
    private static LiveCMD liveCMD;
    @Getter @Setter
    private static OfflineCMD offlineCMD;
    @Getter @Setter
    private static CheckLiveCMD checkLiveCMD;

    @Getter @Setter
    private static MainListener mainListener;

    public StreamersUnite() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setInstance(this);

        setStreamerConfig(new StreamerConfig());

        setStreamerCMD(new StreamerCMD());
        getStreamerCMD().register();
        setLiveCMD(new LiveCMD());
        getLiveCMD().register();
        setOfflineCMD(new OfflineCMD());
        getOfflineCMD().register();
        setCheckLiveCMD(new CheckLiveCMD());
        getCheckLiveCMD().register();

        setMainListener(new MainListener());
        Bukkit.getPluginManager().registerEvents(getMainListener(), StreamersUnite.getInstance());
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
    }
}
