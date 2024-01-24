package host.plas.streamersunite.config;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.StreamerSetup;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

import java.util.*;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", StreamersUnite.getInstance(), false);
    }

    @Override
    public void init() {
        // Nothing to do here
        announceGoLive();
        announceGoOffline();
    }

    public boolean announceGoLive() {
        reloadResource();

        return getOrSetDefault("announce.go-live", true);
    }

    public boolean announceGoOffline() {
        reloadResource();

        return getOrSetDefault("announce.go-offline", false);
    }
}
