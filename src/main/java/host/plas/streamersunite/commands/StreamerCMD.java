package host.plas.streamersunite.commands;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.data.StreamerSetup;
import io.streamlined.bukkit.commands.CommandArgument;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class StreamerCMD extends SimplifiedCommand {
    public StreamerCMD() {
        super("sustreamer", StreamersUnite.getInstance());
    }

    @Override
    public boolean command(CommandContext commandContext) {
        if (commandContext.getArgs().isEmpty()) {
            commandContext.sendMessage("&cUsage: /sustreamer <add|remove|set|list> [args]");
            return true;
        }

        String action = commandContext.getStringArg(0);

        switch (action) {
            case "add":
                if (commandContext.getArgs().size() != 3) {
                    commandContext.sendMessage("&cUsage: /sustreamer add <player> <link>");
                    return true;
                }

                String aplayer = commandContext.getStringArg(1);
                String alink = commandContext.getStringArg(2);

                OfflinePlayer aofflinePlayer = Bukkit.getOfflinePlayer(aplayer);
                if (aofflinePlayer == null) {
                    commandContext.sendMessage("&cPlayer not found.");
                    return true;
                }

                StreamerSetup setup = new StreamerSetup(aofflinePlayer.getUniqueId(), new ArrayList<>(), new ArrayList<>(), alink);

                StreamersUnite.getStreamerConfig().saveSetup(setup);

                commandContext.sendMessage("&eAdded &d" + aplayer + " &eto the streamer list.");
                return true;
            case "remove":
                if (commandContext.getArgs().size() != 2) {
                    commandContext.sendMessage("&cUsage: /sustreamer remove <player>");
                    return true;
                }

                String rplayer = commandContext.getStringArg(1);

                OfflinePlayer rofflinePlayer = Bukkit.getOfflinePlayer(rplayer);
                if (rofflinePlayer == null) {
                    commandContext.sendMessage("&cPlayer not found.");
                    return true;
                }

                StreamersUnite.getStreamerConfig().deleteSetup(rofflinePlayer.getUniqueId().toString());

                commandContext.sendMessage("&eRemoved &d" + rplayer + " &efrom the streamer list.");
                return true;
            case "commands":
                if (commandContext.getArgs().size() < 5) {
                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                    return true;
                }

                String cplayer = commandContext.getStringArg(1);
                String ccommand = commandContext.getStringArg(2);
                String caction = commandContext.getStringArg(3);

                OfflinePlayer cofflinePlayer = Bukkit.getOfflinePlayer(cplayer);
                if (cofflinePlayer == null) {
                    commandContext.sendMessage("&cPlayer not found.");
                    return true;
                }

                StreamerSetup csetup = StreamersUnite.getStreamerConfig().getSetup(cofflinePlayer.getUniqueId().toString()).orElse(null);
                if (csetup == null) {
                    commandContext.sendMessage("&cPlayer not found.");
                    return true;
                }

                switch (ccommand) {
                    case "live":
                        switch (caction) {
                            case "add":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                StringBuilder caBuilder = new StringBuilder();

                                for (CommandArgument argument : commandContext.getArgs()) {
                                    if (argument.getIndex() < 5) continue;

                                    caBuilder.append(argument.getContent()).append(" ");
                                }

                                String caCmd = caBuilder.toString().trim();

                                if (caCmd.isBlank() || caCmd.isEmpty()) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                csetup.addCommand(StreamerSetup.CommandType.LIVE, caCmd);

                                commandContext.sendMessage("&eAdded command to the live commands.");
                                break;
                            case "insert":
                                if (commandContext.getArgs().size() < 6) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int ciindex = 0;
                                try {
                                    ciindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                StringBuilder ciBuilder = new StringBuilder();

                                for (CommandArgument argument : commandContext.getArgs()) {
                                    if (argument.getIndex() < 6) continue;

                                    ciBuilder.append(argument.getContent()).append(" ");
                                }

                                String ciCmd = ciBuilder.toString().trim();

                                if (ciCmd.isBlank() || ciCmd.isEmpty()) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                csetup.insertCommand(StreamerSetup.CommandType.LIVE, ciindex, ciCmd);

                                commandContext.sendMessage("&eInserted command into the live commands at index &d" + ciindex + "&e.");
                                break;
                            case "remove":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int crindex = 0;

                                try {
                                    crindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                csetup.removeCommand(StreamerSetup.CommandType.LIVE, crindex);

                                commandContext.sendMessage("&eRemoved command from the live commands at index &d" + crindex + "&e.");
                                break;
                            case "up":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int cuindex = 0;

                                try {
                                    cuindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                csetup.upCommand(StreamerSetup.CommandType.LIVE, cuindex);

                                commandContext.sendMessage("&eMoved command up in the live commands at index &d" + cuindex + " &eto index &d" + (cuindex - 1) + "&e.");
                                break;
                            case "down":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int cdindex = 0;

                                try {
                                    cdindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                csetup.downCommand(StreamerSetup.CommandType.LIVE, cdindex);

                                commandContext.sendMessage("&eMoved command down in the live commands at index &d" + cdindex + " &eto index &d" + (cdindex + 1) + "&e.");
                                break;
                            default:
                                commandContext.sendMessage("&cInvalid action!");
                                break;
                        }
                        break;
                    case "offline":
                        switch (caction) {
                            case "add":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                StringBuilder caBuilder = new StringBuilder();

                                for (CommandArgument argument : commandContext.getArgs()) {
                                    if (argument.getIndex() < 5) continue;

                                    caBuilder.append(argument.getContent()).append(" ");
                                }

                                String caCmd = caBuilder.toString().trim();

                                if (caCmd.isBlank() || caCmd.isEmpty()) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                csetup.addCommand(StreamerSetup.CommandType.OFFLINE, caCmd);

                                commandContext.sendMessage("&eAdded command to the offline commands.");
                                break;
                            case "insert":
                                if (commandContext.getArgs().size() < 6) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int ciindex = 0;

                                try {
                                    ciindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                StringBuilder ciBuilder = new StringBuilder();

                                for (CommandArgument argument : commandContext.getArgs()) {
                                    if (argument.getIndex() < 6) continue;

                                    ciBuilder.append(argument.getContent()).append(" ");
                                }

                                String ciCmd = ciBuilder.toString().trim();

                                if (ciCmd.isBlank() || ciCmd.isEmpty()) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                csetup.insertCommand(StreamerSetup.CommandType.OFFLINE, ciindex, ciCmd);

                                commandContext.sendMessage("&eInserted command into the offline commands at index &d" + ciindex + "&e.");
                                break;
                            case "remove":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int crindex = 0;

                                try {
                                    crindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                csetup.removeCommand(StreamerSetup.CommandType.OFFLINE, crindex);

                                commandContext.sendMessage("&eRemoved command from the offline commands at index &d" + crindex + "&e.");
                                break;
                            case "up":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int cuindex = 0;

                                try {
                                    cuindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                csetup.upCommand(StreamerSetup.CommandType.OFFLINE, cuindex);

                                commandContext.sendMessage("&eMoved command up in the offline commands at index &d" + cuindex + " &eto index &d" + (cuindex - 1) + "&e.");
                                break;
                            case "down":
                                if (commandContext.getArgs().size() < 5) {
                                    commandContext.sendMessage("&cUsage: /sustreamer commands <player> <live|offline> <add|remove|insert|up|down> <cmd|line>");
                                    return true;
                                }

                                int cdindex = 0;

                                try {
                                    cdindex = commandContext.getIntArg(4).get();
                                } catch (Exception e) {
                                    commandContext.sendMessage("&cInvalid index.");
                                    return true;
                                }

                                csetup.downCommand(StreamerSetup.CommandType.OFFLINE, cdindex);

                                commandContext.sendMessage("&eMoved command down in the offline commands at index &d" + cdindex + " &eto index &d" + (cdindex + 1) + "&e.");
                                break;
                            default:
                                commandContext.sendMessage("&cInvalid action!");
                                break;
                        }
                        break;
                    default:
                        commandContext.sendMessage("&cInvalid type. Should be 'live' or 'offline'.");
                        return true;
                }
                return true;
            case "list":
                List<StreamerSetup> setups = StreamersUnite.getStreamerConfig().getSetups();

                if (setups.isEmpty()) {
                    commandContext.sendMessage("&cNo streamers found.");
                    return true;
                }

                commandContext.sendMessage("&eStreamer List:");
                for (StreamerSetup lsetup : setups) {
                    OfflinePlayer lplayer = Bukkit.getOfflinePlayer(lsetup.getStreamerUuid());
                    if (lplayer == null) continue;

                    commandContext.sendMessage("&e- &d" + lsetup.getStreamerUuid() + " &7(&c" + lplayer.getName() + "&7)");
                }
                return true;
            default:
                commandContext.sendMessage("&cUsage: /sustreamer <add|remove|set|list> [args]");
                return true;
        }
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        ConcurrentSkipListSet<String> tab = new ConcurrentSkipListSet<>();

        if (commandContext.getArgs().size() == 1) {
            tab.add("add");
            tab.add("remove");
            tab.add("commands");
            tab.add("list");
        }
        if (commandContext.getArgs().size() == 2) {
            switch (commandContext.getStringArg(0)) {
                case "add":
                    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                        tab.add(player.getName());
                    }
                    break;
                case "remove":
                case "commands":
                    for (StreamerSetup setup : StreamersUnite.getStreamerConfig().getSetups()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(setup.getStreamerUuid());
                        if (player == null) continue;

                        tab.add(player.getName());
                    }
                    break;
                case "list":
                    break;
            }
        }
        if (commandContext.getArgs().size() == 3) {
            switch (commandContext.getStringArg(0)) {
                case "add":
                    tab.add("live");
                    tab.add("offline");
                    break;
                case "remove":
                    break;
                case "commands":
                    for (StreamerSetup setup : StreamersUnite.getStreamerConfig().getSetups()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(setup.getStreamerUuid());
                        if (player == null) continue;

                        tab.add(player.getName());
                    }
                    break;
                case "list":
                    break;
            }
        }
        if (commandContext.getArgs().size() == 4) {
            switch (commandContext.getStringArg(0)) {
                case "add":
                    break;
                case "remove":
                    break;
                case "commands":
                    tab.add("add");
                    tab.add("remove");
                    tab.add("insert");
                    tab.add("up");
                    tab.add("down");
                    break;
                case "list":
                    break;
            }
        }
        if (commandContext.getArgs().size() == 5) {
            switch (commandContext.getStringArg(0)) {
                case "add":
                    break;
                case "remove":
                    break;
                case "commands":
                    switch (commandContext.getStringArg(3)) {
                        case "add":
                            break;
                        case "remove":
                            break;
                        case "insert":
                            break;
                        case "up":
                            break;
                        case "down":
                            break;
                    }
                    break;
                case "list":
                    break;
            }
        }

        return tab;
    }
}
