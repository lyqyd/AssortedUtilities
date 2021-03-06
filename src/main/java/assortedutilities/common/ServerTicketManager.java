package assortedutilities.common;

import java.util.HashMap;
import assortedutilities.common.handler.FallDamageHandler;
import assortedutilities.common.handler.IFallDamageHandler;
import assortedutilities.common.handler.IPlayerPresenceHandler;
import assortedutilities.common.handler.PlayerPresenceHandler;
import assortedutilities.common.util.AULog;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerTicketManager implements IPlayerPresenceHandler, IFallDamageHandler {

	private HashMap<String, PlayerTicketManager> ticketManagers = new HashMap<String, PlayerTicketManager>();
	
	public static final ServerTicketManager instance = new ServerTicketManager();
	
	void init() {
		PlayerPresenceHandler.instance.addListener(this);
		FallDamageHandler.instance.addListener(this);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		for (PlayerTicketManager manager : ticketManagers.values()) {
			manager.onTick();
		}
	}
	
	public PlayerTicketManager getManagerForPlayer(EntityPlayerMP player) {
		String id = player.getUniqueID().toString();
		if (ticketManagers.containsKey(id)) {
			return ticketManagers.get(id);
		}
		return null;
	}
	
	public PlayerTicketManager getManagerForPlayer(String id) {
		if (ticketManagers.containsKey(id)) {
			return ticketManagers.get(id);
		}
		return null;
	}

	@Override
	public void onLogin(PlayerLoggedInEvent event) {
		// TODO Auto-generated method stub
		String id = event.player.getUniqueID().toString();
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			ticketManagers.put(id, new PlayerTicketManager(player));
		} else {
			AULog.debug("Event player isn't EntityPlayerMP onLogin");
		}
		
	}

	@Override
	public void onLogout(PlayerLoggedOutEvent event) {
		// TODO Make this save the flying/falling state.
		String id = event.player.getUniqueID().toString();
		ticketManagers.remove(id);
	}

	@Override
	public void onWorldChange(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
			String id = player.getUniqueID().toString();
			PlayerTicketManager manager = ticketManagers.get(id);
			if (manager != null) {
				manager.updatePlayerInstance(player);
			}
		}
	}

	@Override
	public int getHandlerDimension() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onFall(LivingFallEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
			String id = player.getUniqueID().toString();
			PlayerTicketManager manager = ticketManagers.get(id);
			if (manager.getTicketCount() > manager.getFlightTicketCount()) {
				// There is at least one falling-mode ticket, so cancel event damage.
				event.setDistance(0f);
				// And then clear all falling-mode tickets.
				manager.removeFallingModeTickets();
			}
		}
	}
}
