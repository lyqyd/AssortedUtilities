package assortedutilities.common;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.*;
import assortedutilities.common.item.PortalLocationItem;
import assortedutilities.common.util.AULog;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
}
