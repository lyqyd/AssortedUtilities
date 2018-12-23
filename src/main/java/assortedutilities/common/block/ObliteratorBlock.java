package assortedutilities.common.block;

import assortedutilities.common.tileentity.ObliteratorTile;
import assortedutilities.common.util.AULog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ObliteratorBlock extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
	public ObliteratorBlock() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ObliteratorBlock.FACING, EnumFacing.NORTH));
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.MISC);
		setRegistryName("obliterator");
		setUnlocalizedName("obliterator");
		GameRegistry.registerTileEntity(ObliteratorTile.class, "obliterator");
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(ObliteratorBlock.FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(ObliteratorBlock.FACING).getIndex();
	}

	public TileEntity createNewTileEntity(World world, int metadata) {
		return new ObliteratorTile();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, ObliteratorBlock.FACING);
	}
}