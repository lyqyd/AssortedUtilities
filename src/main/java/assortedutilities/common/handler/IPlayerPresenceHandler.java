package assortedutilities.common.handler;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public interface IPlayerPresenceHandler {
	void onLogin(PlayerLoggedInEvent event);
	void onLogout(PlayerLoggedOutEvent event);
	void onWorldChange(EntityJoinWorldEvent event);
	int getHandlerDimension();
}
