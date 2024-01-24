package host.plas.streamersunite.commands;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.LiveManager;
import host.plas.streamersunite.managers.NotificationTimer;
import host.plas.streamersunite.managers.TimedEntry;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class GoLiveCMD extends SimplifiedCommand {
    public GoLiveCMD() {
        super("go-live", StreamersUnite.getInstance());
    }

    @Override
    public boolean command(CommandContext commandContext) {
        if (! commandContext.isPlayer()) {
            return other(commandContext);
        }

        Optional<Player> optionalPlayer = commandContext.getSender().getPlayer();
        if (optionalPlayer.isEmpty()) {
            return other(commandContext);
        }

        Player player = optionalPlayer.get();

        if (commandContext.getArgs().isEmpty()) {
            if (StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString()).isPresent()) {
                if (LiveManager.isLive(player)) {
                    commandContext.getSender().sendMessage("&cYou are already live.");
                    return false;
                } else {
                    commandContext.getSender().sendMessage("&eAttempting to set status to live&8...");

                    if (! LiveManager.initiateLive(player)) {
                        commandContext.getSender().sendMessage("&cYou have already set your status as live recently!");
                        return false;
                    }

                    return true;
                }
            } else {
                commandContext.getSender().sendMessage("&cYou must be a streamer to use this command!");

                return false;
            }
        }

        return other(commandContext);
    }

    public static boolean other(CommandContext commandContext) {
        Optional<CommandSender> senderOptional = commandContext.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            return false;
        }
        if (! senderOptional.get().hasPermission("streamersunite.command.go-live.others")) {
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

        if (LiveManager.isLive(offlinePlayer)) {
            commandContext.getSender().sendMessage("&cThat player is already live!");
            return false;
        }

        commandContext.getSender().sendMessage("&eAttempting to set &r" + offlinePlayer.getName() + " &eas live&8...");

        if (! LiveManager.initiateLive(offlinePlayer)) {
            commandContext.getSender().sendMessage("&cThat player has already set their status as live recently!");
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
        if (! senderOptional.get().hasPermission("streamersunite.command.go-live.others")) {
            if (NotificationTimer.hasNotification("go-live", senderOptional.get())) {
                return new ConcurrentSkipListSet<>();
            }

            commandContext.getSender().sendMessage("&cYou do not have permission to set other players' live status to live!");

            NotificationTimer.addNotification("go-live", senderOptional.get());
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
