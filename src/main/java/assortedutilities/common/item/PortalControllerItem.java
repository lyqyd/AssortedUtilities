package assortedutilities.common.item;

import assortedutilities.common.block.PortalControllerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalControllerItem extends ItemBlock {

	public PortalControllerItem(Block block) {
		super(block);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state) {
		return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state.withProperty(PortalControllerBlock.FACING, side.getOpposite()));
	}

}
