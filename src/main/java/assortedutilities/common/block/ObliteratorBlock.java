package assortedutilities.common.block;

import assortedutilities.client.renderer.ObliteratorRenderer;
import assortedutilities.common.tileentity.ObliteratorTile;
import assortedutilities.common.util.AULog;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ObliteratorBlock extends BlockContainer {
	
	//DUNSWE
	public final int[][] rotationMatrix = {
			{1,0,2,3,4,5},
			{0,1,2,3,4,5},
			{2,3,1,0,4,5},
			{3,2,0,1,4,5},
			{4,5,2,3,1,0},
			{5,4,2,3,0,1},
	};
	
	public IIcon[] icons = new IIcon[6];
	
	public ObliteratorBlock() {
		super(Material.ground);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerBlock(this, "obliterator");
		GameRegistry.registerTileEntity(ObliteratorTile.class, "obliterator");
		setBlockName("assortedutilities.obliterator");
		this.setBlockTextureName("assortedutilities:obliterator");
	}
	
	public int getRenderType() {
		return ObliteratorRenderer.model;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < 6; i++) {
			this.icons[i] = register.registerIcon(this.textureName + "_" + Math.min(i, 2));
		}
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[this.rotationMatrix[meta][side]];
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		Vec3 posVec = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3 lookVec = entity.getLookVec();
		MovingObjectPosition mop = world.rayTraceBlocks(posVec, lookVec);
		int dir = mop.sideHit; //btewns 0 - 5
		int direction = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		AULog.info("RT: %d, PO: %d", dir, direction);
		world.setBlockMetadataWithNotify(x, y, z, direction, 2);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new ObliteratorTile();
	}
}