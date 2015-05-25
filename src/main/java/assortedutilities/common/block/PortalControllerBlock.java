package assortedutilities.common.block;

import assortedutilities.common.tileentity.PortalControllerTile;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PortalControllerBlock extends BlockContainer {
	
	public IIcon[] icons = new IIcon[6];
	
	//DUNSWE --or is it?  May be UDSNEW
	public final int[][] rotationMatrix = {
			{1,0,2,3,4,5},
			{0,1,2,3,4,5},
			{2,3,1,0,4,5},
			{3,2,0,1,4,5},
			{4,5,2,3,1,0},
			{5,4,2,3,0,1},
	};

	public PortalControllerBlock() {
		super(Material.iron);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabTransport);
		GameRegistry.registerTileEntity(PortalControllerTile.class, "portalController");
		setBlockName("assortedutilities.portalController");
		this.setBlockTextureName("assortedutilities:portalController");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new PortalControllerTile();
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.icons[0] = register.registerIcon(this.textureName + "_face");
		this.icons[1] = register.registerIcon(this.textureName + "_back");
		for (int i = 2; i < 6; i++) {
			this.icons[i] = register.registerIcon(this.textureName + "_side");
		}
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[this.rotationMatrix[meta & 7][side]];
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof PortalControllerTile) {
			PortalControllerTile controller = (PortalControllerTile) tile;
			return controller.onActivate(player);
		}
		return false;
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
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {

		int mx=x, my=y, mz=z;
	    switch(side) {
		    case 0: my++; break;
		    case 1: my--; break;
  			case 2: mz++; break;
			case 3: mz--; break;
			case 4: mx++; break;
			case 5: mx--; break;
		}
	    
		return (this.rotationMatrix[world.getBlockMetadata(mx, my, mz) & 7][side] == 1);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z) & 7;
		
		float[][] bounds = {
			{0f, 0f, 0f, 1f, 0.45f, 1f},
			{0f, 0.55f, 0f, 1f, 1f, 1f},
			{0f, 0f, 0f, 1f, 1f, 0.45f},
			{0f, 0f, 0.55f, 1f, 1f, 1f},
			{0f, 0f, 0f, 0.45f, 1f, 1f},
			{0.55f, 0f, 0f, 1f, 1f, 1f},
		};
		
		this.setBlockBounds(bounds[meta][0], bounds[meta][1], bounds[meta][2], bounds[meta][3], bounds[meta][4], bounds[meta][5]);
	}
}
