package assortedutilities.common.block;

import assortedutilities.common.tileentity.PortalControllerTile;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class PortalControllerBlock extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool CARD_PRESENT = PropertyBool.create("card");

	public PortalControllerBlock() {
		super(Material.IRON);
		setHardness(0.5F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PortalControllerBlock.FACING, EnumFacing.NORTH).withProperty(PortalControllerBlock.CARD_PRESENT, false));
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setRegistryName("portal-controller");
		setUnlocalizedName("portal-controller");
		GameRegistry.registerTileEntity(PortalControllerTile.class, new ResourceLocation("assortedutilities", "portalController"));
	}

	static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
	static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
	static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
	static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return state.getValue(PortalControllerBlock.FACING).equals(face.getOpposite());
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		switch(state.getValue(PortalControllerBlock.FACING)) {
			case UP:
				return UP_AABB;
			case DOWN:
				return DOWN_AABB;
			case NORTH:
				return NORTH_AABB;
			case SOUTH:
				return SOUTH_AABB;
			case WEST:
				return WEST_AABB;
			case EAST:
				return EAST_AABB;
		}
		return Block.FULL_BLOCK_AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new PortalControllerTile();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing = meta & 7; boolean card = (meta & 8) == 8;
		return this.getDefaultState().withProperty(PortalControllerBlock.FACING, EnumFacing.getFront(facing)).withProperty(PortalControllerBlock.CARD_PRESENT, card);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(PortalControllerBlock.FACING).getIndex() | (state.getValue(PortalControllerBlock.CARD_PRESENT) ? 8 : 0);
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof PortalControllerTile) {
			PortalControllerTile controller = (PortalControllerTile) tile;
			return controller.onActivate(player, hand);
		}
		return false;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof PortalControllerTile) {
			PortalControllerTile controller = (PortalControllerTile) tile;
			controller.onPlacement();
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof PortalControllerTile) {
			PortalControllerTile controller = (PortalControllerTile) tile;
			controller.dropAll();
			controller.onBreak(state.getValue(PortalControllerBlock.FACING));
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PortalControllerBlock.FACING, PortalControllerBlock.CARD_PRESENT);
	}
}
