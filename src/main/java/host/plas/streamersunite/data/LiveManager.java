package host.plas.streamersunite.data;

import host.plas.streamersunite.StreamersUnite;
import host.plas.streamersunite.managers.TimedEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class LiveManager {
    @Getter @Setter
    private static List<OfflinePlayer> currentlyLive = new ArrayList<>();

    public static void goLive(OfflinePlayer player) {
        currentlyLive.add(player);
    }

    public static void goLive(UUID player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer != null) {
            goLive(offlinePlayer);
        }
    }

    public static void goOffline(OfflinePlayer player) {
        currentlyLive.remove(player);
    }

    public static void goOffline(UUID player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer != null) {
            goOffline(offlinePlayer);
        }
    }

    public static boolean isLive(OfflinePlayer player) {
        return currentlyLive.contains(player);
    }

    public static boolean isLive(UUID player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer != null) {
            return isLive(offlinePlayer);
        }

        return false;
    }

    public static ConcurrentSkipListSet<StreamerSetup> getCurrentlyLiveSetups() {
        ConcurrentSkipListSet<StreamerSetup> r = new ConcurrentSkipListSet<>();

        for (OfflinePlayer player : currentlyLive) {
            Optional<StreamerSetup> setup = StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString());

            setup.ifPresent(r::add);
        }

        return r;
    }

    public static boolean initiateLive(OfflinePlayer player) {
        if (TimedEntry.hasEntry("go-live", player.getUniqueId().toString())) {
            return false;
        }

        AtomicBoolean r = new AtomicBoolean(false);

        StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString()).ifPresent(setup -> {
            if (! player.isOnline()) {
                goLive(player);
                return;
            }
            Player p = player.getPlayer();
            if (p == null) {
                goLive(player);
                return;
            }

            if (StreamersUnite.getMainConfig().announceGoLive()) {
                if (! TimedEntry.hasEntry("broadcast", player.getUniqueId().toString())) {
                    setup.tellStreamLinkGoingLive(Bukkit.getOnlinePlayers().toArray(Player[]::new));

                    new TimedEntry<>(20 * 60 * 30, "broadcast", player.getUniqueId().toString(), player);
                }
            }

            setup.getGoLiveCommands().forEach(command -> {
                if (command == null) return;

                String c = command
                        .replace("%player_name%", p.getName())
                        .replace("%player_uuid%", p.getUniqueId().toString())
                        .replace("%player_display_name%", p.getDisplayName())
                        .replace("%player_world%", p.getWorld().getName())
                        ;

                if (c.startsWith("/")) c = c.substring(1);

                boolean asConsole = false;
                if (c.startsWith("!C!")) {
                    c = c.substring("!C!".length());
                    asConsole = true;
                }

                while (c.startsWith(" ")) c = c.substring(1);

                if (asConsole) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                } else {
                    Bukkit.dispatchCommand(p, c);
                }
            });
            goLive(player);

            new TimedEntry<>(20 * 60 * 30, "go-live", player.getUniqueId().toString(), player);

            r.set(true);
        });

        return r.get();
    }

    public static boolean initiateOffline(OfflinePlayer player) {
        if (TimedEntry.hasEntry("go-offline", player.getUniqueId().toString())) {
            return false;
        }

        AtomicBoolean r = new AtomicBoolean(false);

        StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString()).ifPresent(setup -> {
            if (! player.isOnline()) {
                goOffline(player);
                return;
            }
            Player p = player.getPlayer();
            if (p == null) {
                goOffline(player);
                return;
            }

            if (StreamersUnite.getMainConfig().announceGoOffline()) {
                if (! TimedEntry.hasEntry("broadcast", player.getUniqueId().toString())) {
                    setup.tellStreamLinkGoingOffline(Bukkit.getOnlinePlayers().toArray(Player[]::new));

                    new TimedEntry<>(20 * 60 * 30, "broadcast", player.getUniqueId().toString(), player);
                }
            }

            setup.getGoOfflineCommands().forEach(command -> {
                if (command == null) return;

                String c = command
                        .replace("%player_name%", p.getName())
                        .replace("%player_uuid%", p.getUniqueId().toString())
                        .replace("%player_display_name%", p.getDisplayName())
                        .replace("%player_world%", p.getWorld().getName())
                        ;

                if (c.startsWith("/")) c = c.substring(1);

                boolean asConsole = false;
                if (c.startsWith("!C!")) {
                    c = c.substring("!C!".length());
                    asConsole = true;
                }

                while (c.startsWith(" ")) c = c.substring(1);

                if (asConsole) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                } else {
                    Bukkit.dispatchCommand(p, c);
                }
            });
            goOffline(player);

            new TimedEntry<>(20 * 60 * 30, "go-offline", player.getUniqueId().toString(), player);

            r.set(true);
        });

        return r.get();
    }

    public static boolean broadcastLive(OfflinePlayer player) {
        if (TimedEntry.hasEntry("broadcast", player.getUniqueId().toString())) {
            return false;
        }

        AtomicBoolean r = new AtomicBoolean(false);

        StreamersUnite.getStreamerConfig().getSetup(player.getUniqueId().toString()).ifPresent(setup -> {
            if (! player.isOnline()) {
                return;
            }
            Player p = player.getPlayer();
            if (p == null) {
                return;
            }

            setup.tellStreamLinkCurrentlyLive(Bukkit.getOnlinePlayers().toArray(Player[]::new));

            new TimedEntry<>(20 * 60 * 30, "broadcast", player.getUniqueId().toString(), player);

            r.set(true);
        });

        return r.get();
    }
}
