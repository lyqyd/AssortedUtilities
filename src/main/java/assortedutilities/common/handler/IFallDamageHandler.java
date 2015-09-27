package assortedutilities.common.handler;

import net.minecraftforge.event.entity.living.LivingFallEvent;

public interface IFallDamageHandler {
	void onFall(LivingFallEvent event);
}
