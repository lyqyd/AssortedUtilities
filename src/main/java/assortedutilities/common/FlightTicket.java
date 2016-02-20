package assortedutilities.common;

import assortedutilities.common.util.AULog;
import net.minecraft.util.ChunkCoordinates;

public class FlightTicket {
	
	private ChunkCoordinates flightBlock;
	private int flightState;
	private String playerID;

	public FlightTicket(int x, int y, int z, int dimension, String playerID) {
		this.flightBlock = new ChunkCoordinates(x, y, z);
		this.flightState = 1;
		this.playerID = playerID;
	}
	
	public ChunkCoordinates getLocation() {
		return this.flightBlock;
	}
	
	public boolean isFlying() {
		return this.flightState == 1;
	}
	
	public boolean isFalling() {
		return this.flightState == 2;
	}
	
	public void setFlying() {
		this.flightState = 1;
	}
	
	public void setFalling() {
		AULog.debug("Set to falling, ticket %x", this.hashCode());
		this.flightState = 2;
	}
}
