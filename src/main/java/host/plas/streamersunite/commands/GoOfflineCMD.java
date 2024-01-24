package host.plas.streamersunite.commands;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.LiveManager;
import host.plas.streamersunite.managers.NotificationTimer;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class GoOfflineCMD extends SimplifiedCommand {
    public GoOfflineCMD() {
        super("go-offline", StreamersUnite.getInstance());
    }

    @Override
    public boolean command(CommandContext commandContext) {
        if (! commandContext.isPlayer()) {
            return setOtherOffline(commandContext);
        }

        Optional<Player> optionalPlayer = commandContext.getSender().getPlayer();
        if (optionalPlayer.isEmpty()) {
            return setOtherOffline(commandContext);
        }

        Player player = optionalPlayer.get();

        if (commandContext.getArgs().isEmpty()) {
            if (StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString()).isPresent()) {
                if (LiveManager.isLive(player)) {
                    commandContext.getSender().sendMessage("&eAttempting to set status to offline&8...");

                    if (! LiveManager.initiateOffline(player)) {
                        commandContext.getSender().sendMessage("&cYou have already set your status as offline recently!");
                        return false;
                    }

                    return true;
                } else {
                    commandContext.getSender().sendMessage("&cYou are already offline.");
                    return false;
                }
            } else {
                commandContext.getSender().sendMessage("&cYou must be a streamer to use this command!");

                return false;
            }
        }

        return setOtherOffline(commandContext);
    }

    public static boolean setOtherOffline(CommandContext commandContext) {
        Optional<CommandSender> senderOptional = commandContext.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            return false;
        }
        if (! senderOptional.get().hasPermission("streamersunite.command.go-offline.others")) {
            commandContext.getSender().sendMessage("&cYou do not have permission to set other players' live status!");
            return false;
        }

        if (commandContext.getArgs().isEmpty()) {
            commandContext.getSender().sendMessage("&cYou must specify a player!");
            return false;
        }

        String player = commandContext.getStringArg(0);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        if (offlinePlayer == null) {
            commandContext.getSender().sendMessage("&cThat player does not exist!");
            return false;
        }

        if (StreamersUnite.getStreamerConfig().getSetup(offlinePlayer.getUniqueId().toString()).isEmpty()) {
            commandContext.getSender().sendMessage("&cThat player is not a streamer!");
            return false;
        }

        if (! LiveManager.isLive(offlinePlayer)) {
            commandContext.getSender().sendMessage("&cThat player is already offline!");
            return false;
        }

        commandContext.getSender().sendMessage("&eAttempting to set &f" + offlinePlayer.getName() + " &eas no longer live&8...");

        if (! LiveManager.initiateOffline(offlinePlayer)) {
            commandContext.getSender().sendMessage("&cThat player has already set their status as offline recently!");
            return false;
        }

        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        Optional<CommandSender> senderOptional = commandContext.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            return new ConcurrentSkipListSet<>();
        }
        if (! senderOptional.get().hasPermission("streamersunite.command.go-offline.others")) {
            if (NotificationTimer.hasNotification("go-offline", senderOptional.get())) {
                return new ConcurrentSkipListSet<>();
            }

            commandContext.getSender().sendMessage("&cYou do not have permission to set other players' live status!");

            NotificationTimer.addNotification("go-offline", senderOptional.get());
            return new ConcurrentSkipListSet<>();
        }

        ConcurrentSkipListSet<String> r = new ConcurrentSkipListSet<>();

        StreamersUnite.getStreamerConfig().getSetups().forEach(setup -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(setup.getStreamerUuid());

            if (player != null) {
                r.add(player.getName());
            }
        });

        return r;
    }
}
