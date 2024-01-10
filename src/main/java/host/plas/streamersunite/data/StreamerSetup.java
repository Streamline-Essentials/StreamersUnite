package host.plas.streamersunite.data;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class StreamerSetup implements Comparable<StreamerSetup> {
    public enum CommandType {
        LIVE,
        OFFLINE,
        EMPTY,
        ;
    }

    private UUID streamerUuid;

    private List<String> goLiveCommands;
    private List<String> goOfflineCommands;
    private String streamLink;

    public StreamerSetup(UUID streamerUuid, List<String> goLiveCommands, List<String> goOfflineCommands, String streamLink) {
        this.streamerUuid = streamerUuid;
        this.goLiveCommands = goLiveCommands;
        this.goOfflineCommands = goOfflineCommands;
        this.streamLink = streamLink;
    }

    public void save() {
        StreamersUnite.getStreamerConfig().saveSetup(this);
    }

    public void addCommand(CommandType type, String command) {
        if (type == CommandType.LIVE) addCommandLive(command);
        else if (type == CommandType.OFFLINE) addCommandOffline(command);
    }

    public void addCommandLive(String command) {
        goLiveCommands.add(command);
    }

    public void addCommandOffline(String command) {
        goOfflineCommands.add(command);
    }

    public void removeCommand(CommandType type, int index) {
        if (type == CommandType.LIVE) removeCommandLive(index);
        else if (type == CommandType.OFFLINE) removeCommandOffline(index);
    }

    public void removeCommandLive(int index) {
        try {
            goLiveCommands.remove(index);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void removeCommandOffline(int index) {
        try {
            goOfflineCommands.remove(index);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void insertCommand(CommandType type, int index, String command) {
        if (type == CommandType.LIVE) insertCommandLive(index, command);
        else if (type == CommandType.OFFLINE) insertCommandOffline(index, command);
    }

    public void insertCommandLive(int index, String command) {
        try {
            if (index > goLiveCommands.size()) index = goLiveCommands.size();

            goLiveCommands.add(index, command);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void insertCommandOffline(int index, String command) {
        try {
            if (index > goOfflineCommands.size()) index = goOfflineCommands.size();

            goOfflineCommands.add(index, command);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void upCommand(CommandType type, int index) {
        if (type == CommandType.LIVE) upCommandLive(index);
        else if (type == CommandType.OFFLINE) upCommandOffline(index);
    }

    public void upCommandLive(int index) {
        try {
            if (index == 0) return;

            String command = goLiveCommands.get(index);
            goLiveCommands.remove(index);
            goLiveCommands.add(index - 1, command);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void upCommandOffline(int index) {
        try {
            if (index == 0) return;

            String command = goOfflineCommands.get(index);
            goOfflineCommands.remove(index);
            goOfflineCommands.add(index - 1, command);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void downCommand(CommandType type, int index) {
        if (type == CommandType.LIVE) downCommandLive(index);
        else if (type == CommandType.OFFLINE) downCommandOffline(index);
    }

    public void downCommandLive(int index) {
        try {
            if (index == goLiveCommands.size() - 1) return;

            String command = goLiveCommands.get(index);
            goLiveCommands.remove(index);
            goLiveCommands.add(index + 1, command);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    public void downCommandOffline(int index) {
        try {
            if (index == goOfflineCommands.size() - 1) return;

            String command = goOfflineCommands.get(index);
            goOfflineCommands.remove(index);
            goOfflineCommands.add(index + 1, command);
        } catch (Exception e) {
            MessageUtils.logDebug(e);
        }
    }

    @Override
    public int compareTo(@NotNull StreamerSetup o) {
        return streamerUuid.compareTo(o.getStreamerUuid());
    }

    public void tellStreamLink(CommandSender... to) {
        StringBuilder stringBuilder = new StringBuilder();

        OfflinePlayer player = Bukkit.getOfflinePlayer(getStreamerUuid());
        Player onlinePlayer = null;

        String playerName = player.getName();
        if (player.isOnline()) {
            onlinePlayer = player.getPlayer();
        }
        if (onlinePlayer != null) {
            playerName = onlinePlayer.getDisplayName();
        }

        stringBuilder.append("&c").append(player.getName()).append(" &eis &alive &eat &b").append(streamLink).append("&8!");

        BaseComponent textComponent = new ComponentBuilder(MessageUtils.colorize(stringBuilder.toString())).create()[0];

        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, streamLink);
        textComponent.setClickEvent(clickEvent);

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.colorize("&eClick to open stream!")).create());

        textComponent.setHoverEvent(hoverEvent);

        for (CommandSender sender : to) {
            sender.spigot().sendMessage(textComponent);
        }
    }
}
