package host.plas.streamersunite.commands;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.LiveManager;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class OfflineCMD extends SimplifiedCommand {
    public OfflineCMD() {
        super("offline", StreamersUnite.getInstance());
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
                    LiveManager.initiateOffline(player);

                    commandContext.getSender().sendMessage("&eYou have gone &coffline&8!");
                } else {
                    LiveManager.initiateLive(player);

                    commandContext.getSender().sendMessage("&eYou have gone &alive&8!");
                }

                return true;
            } else {
                commandContext.getSender().sendMessage("&cYou must be a streamer to use this command!");
            }
        }

        return setOtherOffline(commandContext);
    }

    public static boolean setOtherOffline(CommandContext commandContext) {
        Optional<CommandSender> senderOptional = commandContext.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            return false;
        }
        if (! senderOptional.get().hasPermission("streamersunite.command.live.others")) {
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

        LiveManager.initiateOffline(offlinePlayer);

        commandContext.getSender().sendMessage("&eYou have set &r" + offlinePlayer.getName() + " &eto &coffline&8!");

        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        Optional<CommandSender> senderOptional = commandContext.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            return new ConcurrentSkipListSet<>();
        }
        if (! senderOptional.get().hasPermission("streamersunite.command.live.others")) {
            commandContext.getSender().sendMessage("&cYou do not have permission to set other players' live status!");
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
