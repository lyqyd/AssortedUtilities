package assortedutilities;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import assortedutilities.common.CommonProxy;
import assortedutilities.common.block.PortalBlock;
import assortedutilities.common.block.PortalControllerBlock;
import assortedutilities.common.block.PortalFrameBlock;
import assortedutilities.common.block.ObliteratorBlock;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "AssortedUtilities", name = "Assorted Utilities", version = "0.0.1")
public class AssortedUtilities {

	public static class Blocks {
		public static ObliteratorBlock obliteratorBlock;
		public static PortalFrameBlock portalFrameBlock;
		public static PortalControllerBlock portalControllerBlock;
		public static PortalBlock portalBlock;
	}
	
	public static class Config {
		public static boolean obliteratorEnabled;
		public static boolean obliteratorRecipeEnabled;
	}
	
	@Instance(value = "AssortedUtilities")
	public static AssortedUtilities instance;
	
	@SidedProxy(clientSide = "assortedutilities.client.ClientProxy", serverSide = "assortedutilities.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("assortedutilities".getBytes()), "[AssortedUtilities]");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		long time = System.nanoTime();
		AULog.init();
		AULog.debug("Starting pre-init");
		
		Configuration configFile = new Configuration(event.getSuggestedConfigurationFile());
		
		Property prop = configFile.get("blocks", "enableObliterator", true);
		prop.comment = "Set to false to disable Obliterator blocks.";
		Config.obliteratorEnabled = prop.getBoolean();
		
		prop = configFile.get("recipes", "enableObliteratorRecipe", true);
		prop.comment = "Set to false to disable the crafting recipe for Obliterator blocks.";
		Config.obliteratorRecipeEnabled = prop.getBoolean();
		
		configFile.save();
		
		proxy.preInit();
		
		AULog.debug("Finished pre-init in %d ms", (System.nanoTime() - time) / 1000000);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		long time = System.nanoTime();
		AULog.debug("Starting init");
		
		proxy.init();
		
		AULog.debug("Finished init in %d ms", (System.nanoTime() - time) / 1000000);
	}
}
