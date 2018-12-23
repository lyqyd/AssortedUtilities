package assortedutilities.common.item;

import assortedutilities.common.util.AULog;
import assortedutilities.common.util.IPortalLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;

public class PortalLocationItem extends Item implements IPortalLocation {
	
	public PortalLocationItem() {
		super();
		setMaxDamage(0);
		setMaxStackSize(64);
		setRegistryName("portal-location-item");
		setUnlocalizedName("portal-location-item");
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.portal-location-item";
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getTagCompound() != null) {return new ActionResult(EnumActionResult.PASS, stack);}
		if (stack.getCount() == 1 || player.inventory.getFirstEmptyStack() > -1) {
			ItemStack card = stack.splitStack(1);
			if (!world.isRemote) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("dim", player.world.provider.getDimension());
				tag.setDouble("x", player.posX);
				tag.setDouble("y", player.posY);
				tag.setDouble("z", player.posZ);
				tag.setFloat("yaw", player.rotationYaw);
				card.setTagCompound(tag);
				AULog.debug("Card created with attributes %f %f %f in dim %d", player.posX, player.posY, player.posZ, player.world.provider.getDimension());
				EntityItem dropItem = new EntityItem(world, player.posX, player.posY, player.posZ, card);
				dropItem.setNoPickupDelay();
				world.spawnEntity(dropItem);
			}
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}

	@Override
	public Vec3d getLocation(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof PortalLocationItem)) {return null;}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {return null;}
		return new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
	}

	@Override
	public Integer getDimension(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof PortalLocationItem)) {return null;}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {return null;}
		return tag.getInteger("dim");
	}

	@Override
	public Float getYaw(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof PortalLocationItem)) {return null;}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {return null;}
		return tag.getFloat("yaw");
	}
}

