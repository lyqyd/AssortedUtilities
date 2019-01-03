package assortedutilities.common;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.*;
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.tileentity.*;
import assortedutilities.common.util.AULog;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.ArrayList;

public class RegisterEventHandler {

	private ItemBlock[] blockItems;

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		int count = 0;

		AssortedUtilities.Items.locationCard = new PortalLocationItem();
		event.getRegistry().register(AssortedUtilities.Items.locationCard);
		count++;

		event.getRegistry().registerAll(blockItems);
		count += blockItems.length;

		blockItems = null;

		AULog.debug("Registered %d items.", count);
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ArrayList<Block> itemBlocks = new ArrayList<Block>();
		ArrayList<Block> otherBlocks = new ArrayList<Block>();

		AssortedUtilities.Blocks.obliteratorBlock = new ObliteratorBlock();
		itemBlocks.add(AssortedUtilities.Blocks.obliteratorBlock);

		AssortedUtilities.Blocks.portalFrameBlock = new PortalFrameBlock();
		itemBlocks.add(AssortedUtilities.Blocks.portalFrameBlock);
		AssortedUtilities.Blocks.portalControllerBlock = new PortalControllerBlock();
		itemBlocks.add(AssortedUtilities.Blocks.portalControllerBlock);
		AssortedUtilities.Blocks.portalBlock = new PortalBlock();
		otherBlocks.add(AssortedUtilities.Blocks.portalBlock);

		AssortedUtilities.Blocks.flightBlockAdv = new AdvancedFlightBlock();
		itemBlocks.add(AssortedUtilities.Blocks.flightBlockAdv);

		AssortedUtilities.Blocks.flightBlockBsc = new BasicFlightBlock();
		itemBlocks.add(AssortedUtilities.Blocks.flightBlockBsc);

		AULog.debug("Registered %d blocks.", (itemBlocks.size() + otherBlocks.size()));

		event.getRegistry().registerAll(itemBlocks.toArray(new Block[itemBlocks.size()]));
		event.getRegistry().registerAll(otherBlocks.toArray(new Block[otherBlocks.size()]));

		prepareBlockItems(itemBlocks);

		GameRegistry.registerTileEntity(AdvancedFlightTile.class, new ResourceLocation("assortedutilities","flightBlockAdvanced"));
		GameRegistry.registerTileEntity(BasicFlightTile.class, new ResourceLocation("assortedutilities","flightBlockBasic"));
		GameRegistry.registerTileEntity(ObliteratorTile.class, new ResourceLocation("assortedutilities","obliterator"));
		GameRegistry.registerTileEntity(PortalTile.class, new ResourceLocation("assortedutilities", "portal"));
		GameRegistry.registerTileEntity(PortalControllerTile.class, new ResourceLocation("assortedutilities", "portalController"));
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		AssortedUtilities.proxy.registerRenderInformation();
	}

	private void prepareBlockItems(ArrayList<Block> blocks) {
		blockItems = new ItemBlock[blocks.size()];
		int i = 0;
		for (Block block : blocks) {
			blockItems[i] = new ItemBlock(block);
			blockItems[i].setRegistryName(block.getRegistryName());
			i++;
		}
	}

	protected static void removeRecipes() {
		ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
		ArrayList<IRecipe> recipes = Lists.newArrayList(recipeRegistry.getValuesCollection());

		for (IRecipe r : recipes)
		{
			ItemStack output = r.getRecipeOutput();
			if (!AssortedUtilities.Config.obliteratorRecipeEnabled) {
				if (output.getItem() == Item.getItemFromBlock(AssortedUtilities.Blocks.obliteratorBlock)) {
					recipeRegistry.remove(r.getRegistryName());
				}
			}
			if (!AssortedUtilities.Config.portalFrameRecipeEnabled) {
				if (output.getItem() == Item.getItemFromBlock(AssortedUtilities.Blocks.portalFrameBlock)) {
					recipeRegistry.remove(r.getRegistryName());
				}
			}
			if (!AssortedUtilities.Config.portalControllerRecipeEnabled) {
				if (output.getItem() == Item.getItemFromBlock(AssortedUtilities.Blocks.portalControllerBlock)) {
					recipeRegistry.remove(r.getRegistryName());
				}
			}
			if (!AssortedUtilities.Config.portalLocationCardRecipeEnabled) {
				if (output.getItem() == AssortedUtilities.Items.locationCard) {
					recipeRegistry.remove(r.getRegistryName());
				}
			}
			if (!AssortedUtilities.Config.portalLocationCardResetRecipeEnabled) {
				if (output.getItem() == AssortedUtilities.Items.locationCard) {
					for (Ingredient i : r.getIngredients()) {
						for (ItemStack stack : i.getMatchingStacks()) {
							if (stack.equals(new ItemStack(AssortedUtilities.Items.locationCard, 1))) {
								recipeRegistry.remove(r.getRegistryName());
							}
						}
					}
				}
			}
			if (!AssortedUtilities.Config.enableAdvancedRecipe) {
				if (output.getItem() == Item.getItemFromBlock(AssortedUtilities.Blocks.flightBlockAdv)) {
					recipeRegistry.remove(r.getRegistryName());
				}
			}
			if (!AssortedUtilities.Config.enableBasicRecipe) {
				if (output.getItem() == Item.getItemFromBlock(AssortedUtilities.Blocks.flightBlockBsc)) {
					recipeRegistry.remove(r.getRegistryName());
				}
			}
		}
	}
}
