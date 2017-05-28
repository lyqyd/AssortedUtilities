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
		if(!worldObj.isRemote) {
			BlockPos target = pos.offset(this.worldObj.getBlockState(pos).getValue(ObliteratorBlock.FACING));
			IBlockState blockState = this.worldObj.getBlockState(target);
			if (blockState != null && !this.worldObj.isAirBlock(target) && blockState.getBlockHardness(worldObj, target) >= 0) {
				BreakEvent breakEvent = new BreakEvent(this.worldObj, target, blockState, AssortedUtilities.proxy.getPlayer((WorldServer) this.worldObj).get());
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if (breakEvent.isCanceled()) {
					return;
				}
			}
			this.worldObj.setBlockToAir(target);
		}
	}
}
