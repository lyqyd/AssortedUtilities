package assortedutilities.common.handler;

import java.util.ArrayList;

import assortedutilities.common.util.AULog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class PlayerPresenceHandler {
	
	public static final PlayerPresenceHandler instance = new PlayerPresenceHandler();
	
	public final ArrayList<IPlayerPresenceHandler> listeners = new ArrayList<IPlayerPresenceHandler>();

	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent event) {
		synchronized(listeners) {
			for (IPlayerPresenceHandler listener : listeners) {
				listener.onLogin(event);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeft(PlayerLoggedOutEvent event) {
		synchronized(listeners) {
			for (IPlayerPresenceHandler listener : listeners) {
				listener.onLogout(event);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity != null && entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			AULog.debug("PPH EJW %s %d", player.getName(), event.getWorld().provider.getDimension());
			synchronized(listeners) {
				for(IPlayerPresenceHandler listener : listeners) {
					listener.onWorldChange(event);
				}
			}
		}
	}
	
	public void addListener(IPlayerPresenceHandler listener) {
		synchronized(listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	public void removeListener(IPlayerPresenceHandler listener) {
		synchronized(listeners) {
			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
		}
	}
}
