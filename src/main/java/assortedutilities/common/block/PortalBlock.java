package assortedutilities.common.block;

import assortedutilities.common.tileentity.PortalControllerTile;
import assortedutilities.common.tileentity.PortalTile;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class PortalBlock extends BlockContainer {
	
	public PortalBlock() {
		super(Material.portal);
		setHardness(-1.0F);
		GameRegistry.registerBlock(this, "portal");
		GameRegistry.registerTileEntity(PortalTile.class, "portal");
		setBlockName("assortedutilities.portal");
		this.setBlockTextureName("assortedutilities:portal");
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new PortalTile();
	}
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof PortalTile) {
			((PortalTile)tile).onCollide(entity);
		}
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
        return false;
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		
		float xAdj = 0.5f;
		float yAdj = 0.5f;
		float zAdj = 0.5f;
		
		switch(meta) {
			case 1:
				xAdj = 0.125f;
				break;
			case 2:
				yAdj = 0.125f;
				break;
			case 3:
				zAdj = 0.125f;
				break;
		}
		
		this.setBlockBounds(0.5f - xAdj, 0.5f - yAdj, 0.5f - zAdj, 0.5f + xAdj, 0.5f + yAdj, 0.5f + zAdj);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

	

}
