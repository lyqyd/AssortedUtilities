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
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "AssortedUtilities", name = "Assorted Utilities", version = "0.0.3")
public class AssortedUtilities {
	
	public static class Items {
		public static PortalLocationItem locationCard;
	}

	public static class Blocks {
		public static ObliteratorBlock obliteratorBlock;
		public static PortalFrameBlock portalFrameBlock;
		public static PortalControllerBlock portalControllerBlock;
		public static PortalBlock portalBlock;
	}
	
	public static class Config {
		public static boolean obliteratorEnabled;
		public static boolean obliteratorRecipeEnabled;
		public static boolean portalsEnabled;
		public static boolean portalFrameRecipeEnabled;
		public static boolean portalControllerRecipeEnabled;
		public static boolean portalLocationCardRecipeEnabled;
		public static boolean portalLocationCardResetRecipeEnabled;
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
		
		//obliterator config
		Property prop = configFile.get("obliterator", "enableObliterator", true);
		prop.comment = "Set to false to disable Obliterator blocks.";
		Config.obliteratorEnabled = prop.getBoolean();
		
		prop = configFile.get("obliterator", "enableObliteratorRecipe", true);
		prop.comment = "Set to false to disable the crafting recipe for Obliterator blocks.";
		Config.obliteratorRecipeEnabled = prop.getBoolean();
		
		//portal system config
		prop = configFile.get("portal", "enablePortals", true);
		prop.comment = "Set to false to disable the portal system (disables all portal system blocks/items).";
		Config.portalsEnabled = prop.getBoolean();
		
		prop = configFile.get("portal", "enablePortalFrameRecipe", true);
		prop.comment = "Set to false to disable the crafting recipe for Portal Frame blocks.";
		Config.portalFrameRecipeEnabled = prop.getBoolean();
		prop = configFile.get("portal", "enablePortalControllerRecipe", true);
		prop.comment = "Set to false to disable the crafting recipe for Portal Controller blocks.";
		Config.portalControllerRecipeEnabled = prop.getBoolean();
		prop = configFile.get("portal", "enableLocationCardRecipe", true);
		prop.comment = "Set to false to disable the crafting recipe for Location Card items.";
		Config.portalLocationCardRecipeEnabled = prop.getBoolean();
		prop = configFile.get("portal", "enableLocationCardResetRecipe", true);
		prop.comment = "Set to false to disable the crafting recipe to reset Location Card items.";
		Config.portalLocationCardResetRecipeEnabled = prop.getBoolean();
		
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
