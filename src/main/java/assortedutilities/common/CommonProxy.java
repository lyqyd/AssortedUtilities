package assortedutilities.common;

import java.lang.ref.WeakReference;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.ObliteratorBlock;
import assortedutilities.common.util.AULog;

public class CommonProxy {
	
	public void preInit() {
		registerBlocks();
	}
	
	public void init() {
		registerRecipes();
		registerRenderInformation();
	}
	
	public void registerRenderInformation() {
		
	}
	
	private void registerBlocks() {
		int count = 0;
		if (AssortedUtilities.Config.obliteratorEnabled) {
			AssortedUtilities.Blocks.obliteratorBlock = new ObliteratorBlock();
			count++;
		}
		AULog.info("Registered %d blocks.", count);
	}
	
	private void registerRecipes() {
		int count = 0;
		if (AssortedUtilities.Config.obliteratorEnabled) {
			ItemStack fourOblits = new ItemStack(AssortedUtilities.Blocks.obliteratorBlock, 4);
			ItemStack cobblestone = new ItemStack((Block)Block.blockRegistry.getObject("cobblestone"));
			ItemStack obsidian = new ItemStack((Block)Block.blockRegistry.getObject("obsidian"));
			ItemStack bucket = new ItemStack((Item)Item.itemRegistry.getObject("lava_bucket"));
			GameRegistry.addRecipe(fourOblits,
					"o o",
					"clc",
					"ccc",
					'o', obsidian, 'c', cobblestone, 'l', bucket);
			count++;
		}
		AULog.info("Registered %d recipes.", count);
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
