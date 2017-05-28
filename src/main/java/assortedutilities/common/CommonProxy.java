package assortedutilities.common;

import java.lang.ref.WeakReference;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
		ServerTicketManager.instance.init();
		MinecraftForge.EVENT_BUS.register(ServerTicketManager.instance);
		registerRenderInformation();
	}
	
	public void init() {
		registerRecipes();
	}
	
	public void registerRenderInformation() {
		
	}
	
	private void registerRecipes() {
		int count = 0;
		if (AssortedUtilities.Config.obliteratorRecipeEnabled) {
			ItemStack obliterator = new ItemStack(AssortedUtilities.Blocks.obliteratorBlock, 1);
			ItemStack cobblestone = new ItemStack(Block.getBlockFromName("minecraft:cobblestone"));
			ItemStack obsidian = new ItemStack(Block.getBlockFromName("minecraft:obsidian"));
			ItemStack bucket = new ItemStack(Item.getByNameOrId("minecraft:lava_bucket"));
			GameRegistry.addRecipe(obliterator,
					"o o",
					"clc",
					"ccc",
					'o', obsidian, 'c', cobblestone, 'l', bucket);
			count++;
		}

		ItemStack locationCard = new ItemStack(AssortedUtilities.Items.locationCard, 1);
		ItemStack portalFrame = new ItemStack(AssortedUtilities.Blocks.portalFrameBlock);
		ItemStack ironIngot = new ItemStack(Item.getByNameOrId("minecraft:iron_ingot"));
		if (AssortedUtilities.Config.portalLocationCardRecipeEnabled) {
			ItemStack enderEye = new ItemStack(Item.getByNameOrId("minecraft:ender_eye"));
			ItemStack redstone = new ItemStack(Item.getByNameOrId("minecraft:redstone"));
			GameRegistry.addShapelessRecipe(locationCard, new Object[] {ironIngot, enderEye, redstone});
			count++;
		}
		if (AssortedUtilities.Config.portalLocationCardResetRecipeEnabled) {
			GameRegistry.addShapelessRecipe(locationCard, new Object[] {locationCard});
			count++;
		}
		if (AssortedUtilities.Config.portalFrameRecipeEnabled) {
			ItemStack enderPearl = new ItemStack(Item.getByNameOrId("minecraft:ender_pearl"));
			GameRegistry.addRecipe(portalFrame,
					"i i",
					" p ",
					"i i",
					'i', ironIngot, 'p', enderPearl);
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
		
		ItemStack feather = new ItemStack(Item.getByNameOrId("minecraft:feather"));
		if (AssortedUtilities.Config.enableAdvancedRecipe) {
			ItemStack flightBlockAdv = new ItemStack(AssortedUtilities.Blocks.flightBlockAdv);
			ItemStack diamond = new ItemStack(Block.getBlockFromName("minecraft:diamond_block"));
			ItemStack emerald = new ItemStack(Item.getByNameOrId("minecraft:emerald"));
			ItemStack gold = new ItemStack(Block.getBlockFromName("minecraft:gold_block"));
			ItemStack diamond_item = new ItemStack(Item.getByNameOrId("minecraft:diamond"));
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
		if (AssortedUtilities.Config.enableBasicRecipe) {
			ItemStack flightBlockBsc = new ItemStack(AssortedUtilities.Blocks.flightBlockBsc);
			ItemStack ironBlock = new ItemStack(Block.getBlockFromName("minecraft:iron_block"));
			GameRegistry.addRecipe(flightBlockBsc,
					" i ",
					" f ",
					" i ",
					'i', ironBlock, 'f', feather);
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
