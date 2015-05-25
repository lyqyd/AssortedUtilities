package assortedutilities.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import assortedutilities.AssortedUtilities;
import assortedutilities.client.renderer.ObliteratorRenderer;
import assortedutilities.client.renderer.PortalControllerRenderer;
import assortedutilities.common.CommonProxy;
import assortedutilities.common.tileentity.PortalControllerTile;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderInformation() {
		RenderingRegistry.registerBlockHandler(new ObliteratorRenderer());
		PortalControllerRenderer controlRender = new PortalControllerRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(PortalControllerTile.class, controlRender);
		MinecraftForgeClient.registerItemRenderer(new ItemStack(AssortedUtilities.Blocks.portalControllerBlock).getItem(), controlRender);
	}
}
