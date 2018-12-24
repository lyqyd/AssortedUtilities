package assortedutilities.client;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.CommonProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderInformation() {
		ModelResourceLocation loc = new ModelResourceLocation("assortedutilities:portal-location-item", "inventory");
		ModelLoader.setCustomModelResourceLocation(AssortedUtilities.Items.locationCard, 0, loc);
		loc = new ModelResourceLocation("assortedutilities:obliterator", "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(AssortedUtilities.Blocks.obliteratorBlock), 0, loc);
		loc = new ModelResourceLocation("assortedutilities:flight-block-advanced", "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(AssortedUtilities.Blocks.flightBlockAdv.getRegistryName()), 0, loc);
		loc = new ModelResourceLocation("assortedutilities:flight-block-basic", "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(AssortedUtilities.Blocks.flightBlockBsc.getRegistryName()), 0, loc);
		loc = new ModelResourceLocation("assortedutilities:portal-controller", "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(AssortedUtilities.Blocks.portalControllerBlock.getRegistryName()), 0, loc);
		loc = new ModelResourceLocation("assortedutilities:portal-frame", "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(AssortedUtilities.Blocks.portalFrameBlock.getRegistryName()), 0, loc);
	}
}
