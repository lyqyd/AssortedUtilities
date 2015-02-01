package assortedutilities.common.tileentity;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ObliteratorTile extends TileEntity {

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) {
			int xPos = this.xCoord;
			int yPos = this.yCoord;
			int zPos = this.zCoord;
			
			switch(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)) {
				case 0:
					yPos -= 1;
					break;
				case 1:
					yPos += 1;
					break;
				case 2:
					zPos -= 1;
					break;
				case 3:
					zPos += 1;
					break;
				case 4:
					xPos -= 1;
					break;
				case 5:
					xPos += 1;
					break;
			}
			
			Block block = this.worldObj.getBlock(xPos, yPos, zPos);
			if (block != null && !block.isAir(this.worldObj, xPos, yPos, zPos) && block.getBlockHardness(worldObj, xPos, yPos, zPos) >= 0) {
				BreakEvent breakEvent = new BreakEvent(xPos, yPos, zPos, this.worldObj, this.worldObj.getBlock(xPos, yPos, zPos), this.worldObj.getBlockMetadata(xPos, yPos, zPos), AssortedUtilities.proxy.getPlayer((WorldServer) this.worldObj).get());
				MinecraftForge.EVENT_BUS.post(breakEvent);
				if (breakEvent.isCanceled()) {
					return;
				}
				//worldObj.playSoundEffect(xPos + 0.5d, yPos+ 0.5d, zPos+ 0.5d, "dig.stone1", 0.25f, worldObj.rand.nextFloat() * 0.15F + 0.6F);
				this.worldObj.setBlockToAir(xPos, yPos, zPos);
			}
		}
	}
}
