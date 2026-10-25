package rip.alpha.blocklogging.record.listener;

import lombok.experimental.UtilityClass;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.ContainerTracker;
import rip.alpha.blocklogging.record.state.PositionOwners;

import java.util.List;

@UtilityClass
public class RecordListeners {

    public void register(Plugin plugin, RecordService records) {
        PositionOwners owners = new PositionOwners();
        PositionOwners tnt = new PositionOwners();

        PluginManager manager = plugin.getServer().getPluginManager();
        List<Listener> listeners = List.of(
                new BlockBreakListener(records),
                new BlockPlaceListener(records, tnt),
                new ExplosionListener(records, tnt),
                new FireListener(records),
                new BucketListener(records, owners),
                new LiquidFlowListener(records, owners),
                new PistonListener(records),
                new EntityBlockListener(records),
                new NatureListener(records),
                new HangingListener(records),
                new ItemFrameListener(records),
                new ContainerListener(records, new ContainerTracker(plugin, records)),
                new ItemListener(records),
                new SignListener(records),
                new KillListener(records),
                new ChatListener(records),
                new SessionListener(records),
                new ClickListener(records)
        );

        listeners.forEach(listener -> manager.registerEvents(listener, plugin));
    }
}
