package assortedutilities.common;

import java.lang.ref.WeakReference;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.AdvancedFlightBlock;
import assortedutilities.common.block.BasicFlightBlock;
import assortedutilities.common.block.ObliteratorBlock;
import assortedutilities.common.block.PortalBlock;
import assortedutilities.common.block.PortalControllerBlock;
import assortedutilities.common.block.PortalFrameBlock;
import assortedutilities.common.handler.FallDamageHandler;
import assortedutilities.common.handler.PlayerPresenceHandler;
import assortedutilities.common.item.PortalControllerItem;
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.util.AULog;

public class CommonProxy {
	
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(FallDamageHandler.instance);
		MinecraftForge.EVENT_BUS.register(PlayerPresenceHandler.instance);
		FMLCommonHandler.instance().bus().register(PlayerPresenceHandler.instance);
		ServerTicketManager.instance.init();
		
		registerItems();
		registerBlocks();
	}
	
	public void init() {
		registerRecipes();
		registerRenderInformation();
	}
	
	public void registerRenderInformation() {
		
	}
	
	private void registerItems() {
		int count = 0;
		if (AssortedUtilities.Config.portalsEnabled) {
			AssortedUtilities.Items.locationCard = new PortalLocationItem();
			GameRegistry.registerItem(AssortedUtilities.Items.locationCard, "locationCard", "assortedutilities");
			count++;
		}
		
		AULog.debug("Registered %d items.", count);
	}
	
	private void registerBlocks() {
		int count = 0;
		if (AssortedUtilities.Config.obliteratorEnabled) {
			AssortedUtilities.Blocks.obliteratorBlock = new ObliteratorBlock();
			count++;
		}
		if (AssortedUtilities.Config.portalsEnabled) {
			AssortedUtilities.Blocks.portalFrameBlock = new PortalFrameBlock();
			count++;
			AssortedUtilities.Blocks.portalControllerBlock = new PortalControllerBlock();
			GameRegistry.registerBlock(AssortedUtilities.Blocks.portalControllerBlock, PortalControllerItem.class, "portalController");
			count++;
			AssortedUtilities.Blocks.portalBlock = new PortalBlock();
			count++;
		}
		if (AssortedUtilities.Config.enableAdvanced) {
			AssortedUtilities.Blocks.flightBlockAdv = new AdvancedFlightBlock();
			count++;
		}
		if (AssortedUtilities.Config.enableBasic) {
			AssortedUtilities.Blocks.flightBlockBsc = new BasicFlightBlock();
			count++;
		}		
		AULog.debug("Registered %d blocks.", count);
	}
	
	private void registerRecipes() {
		int count = 0;
		if (AssortedUtilities.Config.obliteratorEnabled && AssortedUtilities.Config.obliteratorRecipeEnabled) {
			ItemStack obliterator = new ItemStack(AssortedUtilities.Blocks.obliteratorBlock, 1);
			ItemStack cobblestone = new ItemStack((Block)Block.blockRegistry.getObject("cobblestone"));
			ItemStack obsidian = new ItemStack((Block)Block.blockRegistry.getObject("obsidian"));
			ItemStack bucket = new ItemStack((Item)Item.itemRegistry.getObject("lava_bucket"));
			GameRegistry.addRecipe(obliterator,
					"o o",
					"clc",
					"ccc",
					'o', obsidian, 'c', cobblestone, 'l', bucket);
			count++;
		}
		
		if (AssortedUtilities.Config.portalsEnabled) {
			ItemStack locationCard = new ItemStack(AssortedUtilities.Items.locationCard, 1);
			ItemStack portalFrame = new ItemStack(AssortedUtilities.Blocks.portalFrameBlock);
			ItemStack iron = new ItemStack((Item)Item.itemRegistry.getObject("iron_ingot"));
			if (AssortedUtilities.Config.portalLocationCardRecipeEnabled) {
				ItemStack enderEye = new ItemStack((Item)Item.itemRegistry.getObject("ender_eye"));
				ItemStack redstone = new ItemStack((Item)Item.itemRegistry.getObject("redstone"));
				GameRegistry.addShapelessRecipe(locationCard, new Object[] {iron, enderEye, redstone});
				count++;
			}
			if (AssortedUtilities.Config.portalLocationCardResetRecipeEnabled) {
				GameRegistry.addShapelessRecipe(locationCard, new Object[] {locationCard});
				count++;
			}
			if (AssortedUtilities.Config.portalFrameRecipeEnabled) {
				ItemStack enderPearl = new ItemStack((Item)Item.itemRegistry.getObject("ender_pearl"));
				GameRegistry.addRecipe(portalFrame,
						"i i",
						" p ",
						"i i",
						'i', iron, 'p', enderPearl);
				count++;
			}
			if (AssortedUtilities.Config.portalControllerRecipeEnabled) {
				ItemStack portalController = new ItemStack(AssortedUtilities.Blocks.portalControllerBlock);
				GameRegistry.addRecipe(portalController,
						"fff",
						"f f",
						"fff",
						'f', portalFrame);
				count++;
			}
		}
		
		ItemStack feather = new ItemStack((Item)Item.itemRegistry.getObject("feather"));
		if (AssortedUtilities.Config.enableAdvanced && AssortedUtilities.Config.enableAdvancedRecipe) { 
			ItemStack flightBlockAdv = new ItemStack(AssortedUtilities.Blocks.flightBlockAdv);
			ItemStack diamond = new ItemStack((Block)Block.blockRegistry.getObject("diamond_block"));
			ItemStack emerald = new ItemStack((Item)Item.itemRegistry.getObject("emerald"));
			ItemStack gold = new ItemStack((Block)Block.blockRegistry.getObject("gold_block"));
			ItemStack diamond_item = new ItemStack((Item)Item.itemRegistry.getObject("diamond"));
			if (AssortedUtilities.Config.cheapRecipe) {
				GameRegistry.addRecipe(flightBlockAdv,
						" g ",
						"ifi",
						" e ",
				        'e', emerald, 'f', feather, 'g', gold, 'i', diamond_item);
				count++;
			} else {
				GameRegistry.addRecipe(flightBlockAdv,
						"gig",
						"dfd",
						"eie",
				        'd', diamond, 'e', emerald, 'f', feather, 'g', gold, 'i', diamond_item);
				count++;
			}
		}
		if (AssortedUtilities.Config.enableBasic && AssortedUtilities.Config.enableBasicRecipe) {
			ItemStack flightBlockBsc = new ItemStack(AssortedUtilities.Blocks.flightBlockBsc);
			ItemStack iron = new ItemStack((Block)Block.blockRegistry.getObject("iron_block"));
			GameRegistry.addRecipe(flightBlockBsc,
					" i ",
					" f ",
					" i ",
					'i', iron, 'f', feather);
			count++;
		}
		
		AULog.debug("Registered %d recipes.", count);
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
			CommonProxy.assortedUtilitiesPlayer.get().worldObj = world;
		}
			return CommonProxy.assortedUtilitiesPlayer;
	}
	
	public final WeakReference<EntityPlayer> getPlayer(WorldServer world, int x, int y, int z) {
		if (CommonProxy.assortedUtilitiesPlayer.get() == null) {
			CommonProxy.assortedUtilitiesPlayer = createNewPlayer(world, x, y, z);
		} else {
			CommonProxy.assortedUtilitiesPlayer.get().worldObj = world;
			CommonProxy.assortedUtilitiesPlayer.get().posX = x;
			CommonProxy.assortedUtilitiesPlayer.get().posY = y;
			CommonProxy.assortedUtilitiesPlayer.get().posZ = z;
		}
			return CommonProxy.assortedUtilitiesPlayer;
	}
}
