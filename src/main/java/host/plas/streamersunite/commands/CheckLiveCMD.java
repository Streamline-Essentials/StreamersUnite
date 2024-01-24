package host.plas.streamersunite.commands;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.LiveManager;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class CheckLiveCMD extends SimplifiedCommand {
    public CheckLiveCMD() {
        super("livenow", StreamersUnite.getInstance());
    }

    @Override
    public boolean command(CommandContext commandContext) {
        Optional<CommandSender> senderOptional = commandContext.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            return false;
        }

        CommandSender sender = senderOptional.get();

        if (commandContext.getArg(0).isUsable()) {
            String target = commandContext.getStringArg(0);
            Player player = Bukkit.getPlayer(target);
            if (player != null) {
                sender = player;
            } else {
                commandContext.sendMessage("&cThat player is not online!");

                return true;
            }
        }

        commandContext.sendMessage("&5&m     &r &6&lCURRENTLY LIVE &5&m     &r");

        CommandSender finalSender = sender;
        LiveManager.getCurrentlyLiveSetups().forEach(setup -> setup.tellStreamLinkCurrentlyLive(finalSender));

        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        return new ConcurrentSkipListSet<>();
    }
}
