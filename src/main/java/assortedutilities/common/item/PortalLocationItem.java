package assortedutilities.common.item;

import assortedutilities.AssortedUtilities;
import assortedutilities.common.util.AULog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PortalLocationItem extends Item {
	
	public PortalLocationItem() {
		super();
		setMaxDamage(0);
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("assortedutilities:locationCard");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.assortedutilities.locationCard";
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (stack.stackTagCompound != null) {return stack;}
		if (stack.stackSize == 1 || player.inventory.getFirstEmptyStack() > -1) {
			ItemStack card = stack.splitStack(1);
			if (!world.isRemote) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("dim", player.worldObj.provider.dimensionId);
				tag.setDouble("x", player.posX);
				tag.setDouble("y", player.posY);
				tag.setDouble("z", player.posZ);
				tag.setFloat("yaw", player.rotationYaw);
				card.stackTagCompound = tag;
				AULog.debug("Card created with attributes %f %f %f in dim %d", player.posX, player.posY, player.posZ, player.worldObj.provider.dimensionId);
				EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, card);
				dropItem.delayBeforeCanPickup = 0;
				world.spawnEntityInWorld(dropItem);
			}
		}
		return stack;
	}
}

