package host.plas.streamersunite.managers;

import io.streamlined.bukkit.instances.BaseRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

@Getter @Setter
public class NotificationTimer extends BaseRunnable {
    @Getter @Setter
    private static ConcurrentSkipListSet<NotificationTimer> notifications = new ConcurrentSkipListSet<>();

    public static Optional<NotificationTimer> addNotification(String identifier, CommandSender sender) {
        if (hasNotification(identifier, sender)) return Optional.empty();

        NotificationTimer notificationTimer = new NotificationTimer(identifier, sender);
        notifications.add(notificationTimer);

        return Optional.of(notificationTimer);
    }

    public static void removeNotification(String identifier, CommandSender sender) {
        if (! hasNotification(identifier, sender)) return;

        getNotificationTimer(identifier, sender).ifPresent(notification -> notifications.remove(notification));
    }

    public static Optional<NotificationTimer> getNotificationTimer(String identifier, CommandSender sender) {
        AtomicReference<NotificationTimer> notificationTimer = new AtomicReference<>();

        notifications.forEach(notification -> {
            if (notification.getIdentifier().equals(identifier) && notification.getSender().equals(sender)) {
                notificationTimer.set(notification);
            }
        });

        return Optional.ofNullable(notificationTimer.get());
    }

    public static boolean hasNotification(String identifier, CommandSender sender) {
        return getNotificationTimer(identifier, sender).isPresent();
    }

    private String identifier;
    private CommandSender sender;

    private NotificationTimer(String identifier, CommandSender sender) {
        super(5 * 20, 1, true); // 5 second delayed then cancels. Asynchronous.

        this.identifier = identifier;
        this.sender = sender;
    }

    @Override
    public void execute() {
        removeNotification(identifier, sender);

        cancel();
    }
}
