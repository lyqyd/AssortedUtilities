package assortedutilities.common.block;

import assortedutilities.common.tileentity.PortalControllerTile;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PortalControllerBlock extends BlockContainer {

	public PortalControllerBlock() {
		super(Material.iron);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabTransport);
		GameRegistry.registerBlock(this, "portalController");
		GameRegistry.registerTileEntity(PortalControllerTile.class, "portalController");
		setBlockName("assortedutilities.portalController");
		this.setBlockTextureName("assortedutilities:portalController");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new PortalControllerTile();
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof PortalControllerTile) {
			PortalControllerTile controller = (PortalControllerTile) tile;
			controller.onPlacement();
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof PortalControllerTile) {
			PortalControllerTile controller = (PortalControllerTile) tile;
			controller.onBreak();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}
	
}
