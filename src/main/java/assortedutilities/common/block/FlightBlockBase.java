package assortedutilities.common.block;

import java.util.ArrayList;
import java.util.List;
import assortedutilities.common.tileentity.FlightTileBase;
import assortedutilities.common.util.AULog;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class FlightBlockBase extends BlockContainer {

	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	private boolean requiresSilkTouch = false;
	private ItemStack drop;
	
	public FlightBlockBase() {
		super(Material.IRON);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FlightBlockBase.ACTIVE, false));
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState getStateFromMeta (int meta) {
		return this.getDefaultState().withProperty(FlightBlockBase.ACTIVE, meta == 1);
	}

	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(FlightBlockBase.ACTIVE) ? 1 : 0;
	}
	
	public FlightBlockBase(boolean silk, ItemStack droppedIfNotSilky) {
		this();
		this.requiresSilkTouch = silk;
		this.drop = droppedIfNotSilky;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof FlightTileBase) {
			((FlightTileBase)tile).dropAllFlyers();
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return true;
    }

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		boolean active = state.getValue(FlightBlockBase.ACTIVE);
		AULog.debug("Getting drops; silk required: %b, active: %b", this.requiresSilkTouch, active);
		if (this.requiresSilkTouch && active) {
			if (this.drop != null) {
				drops.add(this.drop.copy());
			}
		} else {
			drops.add(new ItemStack(Item.getItemFromBlock(this), 1));
		}
		return drops;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FlightBlockBase.ACTIVE);
	}
}
