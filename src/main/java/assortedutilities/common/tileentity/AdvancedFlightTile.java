package assortedutilities.common.tileentity;

import assortedutilities.AssortedUtilities;

public class AdvancedFlightTile extends FlightTileBase {

	public AdvancedFlightTile() {
		super(AssortedUtilities.Config.radiusAdv, AssortedUtilities.Config.chargeTimeAdv * 20);
	}
}
