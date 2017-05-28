package assortedutilities.common;

import assortedutilities.common.util.AULog;
import net.minecraft.util.math.BlockPos;

public class FlightTicket {

	public enum State {FLYING, DROPPING, FALLING, LANDED};
	
	private BlockPos flightBlock;
	private State flightState;
	private int dimension;
	//age is used to wait five ticks after a ticket becomes invalid before dropping the player.
	private int age;
	private String playerID;

	public FlightTicket(BlockPos pos, int dimension, String playerID) {
		this.flightBlock = pos;
		this.flightState = State.FLYING;
		this.dimension = dimension;
		this.playerID = playerID;
	}
	
	public BlockPos getLocation() {
		return this.flightBlock;
	}

	public int getDimension() { return this.dimension; }
	
	public boolean isFlying() {
		return this.flightState == State.FLYING || this.flightState == State.DROPPING;
	}

	public boolean isDropping() {
		return this.flightState == State.DROPPING;
	}

	public boolean isDroppingOrFalling() {
		return this.flightState == State.DROPPING || this.flightState == State.FALLING;
	}
	
	public boolean isFalling() {
		return this.flightState == State.FALLING;
	}

	public boolean isLanded() {
		return this.flightState == State.LANDED;
	}

	public State getFlightState() {
		return this.flightState;
	}
	
	public void setFlying() {
		this.flightState = State.FLYING;
		this.age = 0;
	}

	public void setDropping() {
		this.flightState = State.DROPPING;
	}

	public void ageTicket() {
		if (this.isDropping()) {
			this.age++;
			if (this.age > 5) {this.setFalling();}
		}
	}
	
	public void setFalling() {
		AULog.debug("Set to falling, ticket %x", this.hashCode());
		this.flightState = State.FALLING;
	}

	public void setLanded() {
		if (this.flightState == State.FALLING) {
			this.flightState = State.LANDED;
		}
	}
}
