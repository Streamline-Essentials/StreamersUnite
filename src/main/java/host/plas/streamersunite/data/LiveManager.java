package host.plas.streamersunite.data;

import host.plas.streamersunite.StreamersUnite;
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

    public static void initiateLive(OfflinePlayer player) {
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

            setup.getGoLiveCommands().forEach(command -> {
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

                if (asConsole) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                } else {
                    Bukkit.dispatchCommand(p, c);
                }
            });
            goLive(player);
        });
    }

    public static void initiateOffline(OfflinePlayer player) {
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

            setup.getGoOfflineCommands().forEach(command -> {
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

                if (asConsole) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                } else {
                    Bukkit.dispatchCommand(p, c);
                }
            });
            goOffline(player);
        });
    }

    public static void initiateLive(UUID player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer != null) {
            initiateLive(offlinePlayer);
        }
    }

    public static void initiateOffline(UUID player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

        if (offlinePlayer != null) {
            initiateOffline(offlinePlayer);
        }
    }
}
