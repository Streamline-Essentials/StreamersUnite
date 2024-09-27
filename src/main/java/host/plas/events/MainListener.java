package host.plas.events;

import host.plas.StreamersUnite;
import host.plas.data.LiveManager;

import singularity.data.players.CosmicPlayer;
import singularity.events.server.LogoutEvent;
import tv.quaint.events.BaseEventHandler;
import tv.quaint.events.BaseEventListener;
import tv.quaint.events.processing.BaseProcessor;

public class MainListener implements BaseEventListener {
    public MainListener() {
        BaseEventHandler.bake(this, StreamersUnite.getInstance());

        StreamersUnite.getInstance().logInfo("Registered MainListener!");
    }

    @BaseProcessor
    public void onPlayerLogout(LogoutEvent event) {
        CosmicPlayer player = event.getPlayer();

        if (StreamersUnite.getStreamerConfig().getSetup(player.getUuid()).isPresent()) {
            if (LiveManager.isLive(player)) {
                LiveManager.goOffline(player);
            }
        }
    }
}
