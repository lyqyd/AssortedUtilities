package assortedutilities.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public interface IPortalLocation {
	Vec3d getLocation(ItemStack stack);
	Integer getDimension(ItemStack stack);
	Float getYaw(ItemStack stack);
}
