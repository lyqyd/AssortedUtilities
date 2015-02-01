package assortedutilities.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import assortedutilities.client.renderer.ObliteratorRenderer;
import assortedutilities.common.CommonProxy;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderInformation() {
		RenderingRegistry.registerBlockHandler(new ObliteratorRenderer());
	}
}
