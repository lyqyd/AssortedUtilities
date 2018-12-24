package assortedutilities.common;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import assortedutilities.AssortedUtilities;
import assortedutilities.common.handler.FallDamageHandler;
import assortedutilities.common.handler.PlayerPresenceHandler;
import assortedutilities.common.util.AULog;

public class CommonProxy {
	
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(FallDamageHandler.instance);
		MinecraftForge.EVENT_BUS.register(PlayerPresenceHandler.instance);
		ServerTicketManager.instance.init();
		MinecraftForge.EVENT_BUS.register(ServerTicketManager.instance);
	}

	public void registerRenderInformation() {

	}
	
	public void init() {
		RegisterEventHandler.removeRecipes();
	}
	
	public static WeakReference<EntityPlayer> assortedUtilitiesPlayer = new WeakReference<EntityPlayer>(null);
	
	private WeakReference<EntityPlayer> createNewPlayer(WorldServer world) {
		EntityPlayer player = FakePlayerFactory.get(world, AssortedUtilities.gameProfile);
		return new WeakReference<EntityPlayer>(player);
	}
	
	private WeakReference<EntityPlayer> createNewPlayer(WorldServer world, int x, int y, int z) {
		EntityPlayer player = FakePlayerFactory.get(world, AssortedUtilities.gameProfile);
		player.posX = x;
		player.posY = y;
		player.posZ = z;
		return new WeakReference<EntityPlayer>(player);
	}
	
	public final WeakReference<EntityPlayer> getPlayer(WorldServer world) {
		if (CommonProxy.assortedUtilitiesPlayer.get() == null) {
			CommonProxy.assortedUtilitiesPlayer = createNewPlayer(world);
		} else {
			CommonProxy.assortedUtilitiesPlayer.get().world = world;
		}
			return CommonProxy.assortedUtilitiesPlayer;
	}
	
	public final WeakReference<EntityPlayer> getPlayer(WorldServer world, int x, int y, int z) {
		if (CommonProxy.assortedUtilitiesPlayer.get() == null) {
			CommonProxy.assortedUtilitiesPlayer = createNewPlayer(world, x, y, z);
		} else {
			CommonProxy.assortedUtilitiesPlayer.get().world = world;
			CommonProxy.assortedUtilitiesPlayer.get().posX = x;
			CommonProxy.assortedUtilitiesPlayer.get().posY = y;
			CommonProxy.assortedUtilitiesPlayer.get().posZ = z;
		}
			return CommonProxy.assortedUtilitiesPlayer;
	}
}
