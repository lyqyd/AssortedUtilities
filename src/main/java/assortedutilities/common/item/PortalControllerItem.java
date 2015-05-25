package assortedutilities.common.item;

import assortedutilities.common.util.AULog;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PortalControllerItem extends ItemBlock {

	public PortalControllerItem(Block block) {
		super(block);
	}
	
	int[] sideTranslation = {1, 0, 3, 2, 5, 4};
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, sideTranslation[side]);
	}

}
