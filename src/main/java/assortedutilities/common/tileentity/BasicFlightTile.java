package assortedutilities.common.tileentity;

import assortedutilities.AssortedUtilities;

public class BasicFlightTile extends FlightTileBase {

	public BasicFlightTile() {
		super(AssortedUtilities.Config.radiusBsc, AssortedUtilities.Config.chargeTimeBsc * 20);
	}
}
