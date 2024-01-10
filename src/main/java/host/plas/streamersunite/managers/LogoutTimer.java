package host.plas.streamersunite.managers;

import host.plas.streamersunite.data.LiveManager;
import io.streamlined.bukkit.instances.BaseRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter @Setter
public class LogoutTimer extends BaseRunnable {
    private OfflinePlayer player;

    public LogoutTimer(OfflinePlayer player) {
        super(20 * 60 * 5, 1, true);
    }

    @Override
    public void execute() {
        if (! player.isOnline()) LiveManager.goOffline(player);

        cancel();
    }

    @Getter @Setter
    private static ConcurrentSkipListSet<LogoutTimer> timed = new ConcurrentSkipListSet<>();

    public static void addTimer(OfflinePlayer player) {
        LogoutTimer logoutTimer = new LogoutTimer(player);
        timed.add(logoutTimer);
    }

    public static void removeTimer(OfflinePlayer player) {
        timed.forEach(logoutTimer -> {
            if (logoutTimer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                timed.remove(logoutTimer);
            }
        });
    }

    public static Optional<LogoutTimer> getTimed(OfflinePlayer player) {
        return timed.stream().filter(logoutTimer -> logoutTimer.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst();
    }

    public static boolean isTimed(OfflinePlayer player) {
        return getTimed(player).isPresent();
    }

    public static boolean isTimed(UUID uuid) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);

        if (p != null) {
            return isTimed(p);
        }

        return false;
    }

    public static void putTimedNow(OfflinePlayer player) {
        if (isTimed(player)) {
            getTimed(player).ifPresent(BaseRunnable::cancel);
        }

        addTimer(player);
    }
}
