package assortedutilities;

import java.util.UUID;

import assortedutilities.common.RegisterEventHandler;
import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import assortedutilities.common.CommonProxy;
import assortedutilities.common.block.AdvancedFlightBlock;
import assortedutilities.common.block.BasicFlightBlock;
import assortedutilities.common.block.PortalBlock;
import assortedutilities.common.block.PortalControllerBlock;
import assortedutilities.common.block.PortalFrameBlock;
import assortedutilities.common.block.ObliteratorBlock;
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.util.AULog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "assortedutilities", name = "Assorted Utilities", version = "0.0.6")
public class AssortedUtilities {

	public AssortedUtilities() {
		AULog.init();
		MinecraftForge.EVENT_BUS.register(new RegisterEventHandler());
	}
	
	public static class Items {
		public static PortalLocationItem locationCard;
	}

	public static class Blocks {
		public static ObliteratorBlock obliteratorBlock;
		public static PortalFrameBlock portalFrameBlock;
		public static PortalControllerBlock portalControllerBlock;
		public static PortalBlock portalBlock;
		public static AdvancedFlightBlock flightBlockAdv;
		public static BasicFlightBlock flightBlockBsc;
	}
	
	public static class Config {
		public static boolean obliteratorRecipeEnabled;

		public static boolean portalFrameRecipeEnabled;
		public static boolean portalControllerRecipeEnabled;
		public static boolean portalLocationCardRecipeEnabled;
		public static boolean portalLocationCardResetRecipeEnabled;
		
		//wingless flight configuration options
		public static boolean enableAdvancedRecipe;
		public static boolean silkTouchRequiredAdv;
		public static int chargeTimeAdv;
		public static int radiusAdv;
		public static boolean cheapRecipe;

		public static boolean enableBasicRecipe;
		public static boolean silkTouchRequiredBsc;
		public static int chargeTimeBsc;
		public static int radiusBsc;
	}
	
	@Instance(value = "AssortedUtilities")
	public static AssortedUtilities instance;
	
	@SidedProxy(clientSide = "assortedutilities.client.ClientProxy", serverSide = "assortedutilities.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("assortedutilities".getBytes()), "[AssortedUtilities]");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		long time = System.nanoTime();
		AULog.debug("Starting pre-init");
		
		Configuration configFile = new Configuration(event.getSuggestedConfigurationFile());
		
		//obliterator config
		Property prop = configFile.get("obliterator", "enableObliteratorRecipe", true);
		prop.setComment("Set to false to disable the crafting recipe for Obliterator blocks.");
		Config.obliteratorRecipeEnabled = prop.getBoolean();
		
		//portal system config
		prop = configFile.get("portal", "enablePortalFrameRecipe", true);
		prop.setComment("Set to false to disable the crafting recipe for Portal Frame blocks.");
		Config.portalFrameRecipeEnabled = prop.getBoolean();
		prop = configFile.get("portal", "enablePortalControllerRecipe", true);
		prop.setComment("Set to false to disable the crafting recipe for Portal Controller blocks.");
		Config.portalControllerRecipeEnabled = prop.getBoolean();
		prop = configFile.get("portal", "enableLocationCardRecipe", true);
		prop.setComment("Set to false to disable the crafting recipe for Location Card items.");
		Config.portalLocationCardRecipeEnabled = prop.getBoolean();
		prop = configFile.get("portal", "enableLocationCardResetRecipe", true);
		prop.setComment("Set to false to disable the crafting recipe to reset Location Card items.");
		Config.portalLocationCardResetRecipeEnabled = prop.getBoolean();
		
		//advanced flight blocks configuration
		prop = configFile.get("balance", "silkTouchRequiredAdvanced", true);
		prop.setComment("Advanced Flight Blocks require silk touch to pick up intact");
		Config.silkTouchRequiredAdv = prop.getBoolean();
		prop = configFile.get("balance", "chargeTimeAdvanced", 10);
		prop.setComment("Time Advanced Flight Blocks require to charge before working, in seconds");
		Config.chargeTimeAdv = prop.getInt();
		prop = configFile.get("balance", "radiusAdvanced", 32);
		prop.setComment("Distance in blocks for Advanced Flight Blocks to enable flight");
		Config.radiusAdv = prop.getInt();
		prop = configFile.get("balance", "cheapRecipeAdvanced", false);
		prop.setComment("Use a significantly cheaper crafting recipe for the Advanced Flight Blocks.");
		Config.cheapRecipe = prop.getBoolean();
		prop = configFile.get("flight", "enableAdvancedRecipe", true);
		prop.setComment("Enable Advanced Flight Block Recipe");
		Config.enableAdvancedRecipe = prop.getBoolean();
		
		//basic flight blocks configuration
		prop = configFile.get("balance", "silkTouchRequiredBasic", false);
		prop.setComment("Basic Flight Blocks require silk touch to pick up intact");
		Config.silkTouchRequiredBsc = prop.getBoolean();
		prop = configFile.get("balance", "chargeTimeBasic", 30);
		prop.setComment("Time Basic Flight Blocks require to charge before working, in seconds");
		Config.chargeTimeBsc = prop.getInt();
		prop = configFile.get("balance", "radiusBasic", 6);
		prop.setComment("Distance in blocks for Basic Flight Blocks to enable flight");
		Config.radiusBsc = prop.getInt();
		prop = configFile.get("flight", "enableBasicRecipe", true);
		prop.setComment("Enable Basic Flight Block Recipe");
		Config.enableBasicRecipe = prop.getBoolean();

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
