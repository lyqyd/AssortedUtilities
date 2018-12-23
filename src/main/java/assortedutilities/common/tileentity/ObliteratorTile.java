package assortedutilities.common.tileentity;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.block.ObliteratorBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ObliteratorTile extends TileEntity implements ITickable {

	public void update() {
		if(!world.isRemote) {
			BlockPos target = pos.offset(this.world.getBlockState(pos).getValue(ObliteratorBlock.FACING));
			IBlockState blockState = this.world.getBlockState(target);
			if (blockState != null && !this.world.isAirBlock(target) && blockState.getBlockHardness(world, target) >= 0) {
				BreakEvent breakEvent = new BreakEvent(this.world, target, blockState, AssortedUtilities.proxy.getPlayer((WorldServer) this.world).get());
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if (breakEvent.isCanceled()) {
					return;
				}
			}
			this.world.setBlockToAir(target);
		}
	}
}
