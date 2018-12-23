package assortedutilities.common;

import java.util.ArrayList;
import java.util.function.Predicate;

import assortedutilities.common.util.AULog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class PlayerTicketManager {

	private ArrayList<FlightTicket> ticketList = new ArrayList<FlightTicket>();
	private EntityPlayerMP player;

	public PlayerTicketManager (EntityPlayerMP player) {
		this.player = player;
	}

	public void updatePlayerInstance(EntityPlayerMP player) {
		this.player = player;
	}

	public void update() {
		//AULog.debug("Update for player %s: %d/%d, %b", this.player.getName(), this.getFlightTicketCount(), this.getTicketCount(), this.player.capabilities.allowFlying);
		if (this.getFlightTicketCount() > 0 && !this.player.capabilities.allowFlying) {
			//AULog.debug("Player %s granted flight, %d/%d tickets", this.player.getName(), this.getFlightTicketCount(), this.getTicketCount());
			this.player.capabilities.allowFlying = true;
		} else if (this.getFlightTicketCount() == 0 && this.player.capabilities.allowFlying) {
			//AULog.debug("Player %s flight removed, %d/%d tickets", this.player.getName(), this.getFlightTicketCount(), this.getTicketCount());
			if(!this.player.capabilities.isCreativeMode) {
				this.player.capabilities.allowFlying = false;
				this.player.capabilities.isFlying = false;
			}
		}
		this.player.sendPlayerAbilities();
		if (this.player.onGround || this.getFlightTicketCount() > 0) {
			// Player is on ground or still has flying tickets, remove all falling-mode tickets
			this.removeFallingModeTickets(false);
		}
		//AULog.debug("Finalize update for player %s: %d/%d, %b", this.player.getName(), this.getFlightTicketCount(), this.getTicketCount(), this.player.capabilities.allowFlying);
	}

	public void onTick() {
		boolean flying = this.player.capabilities.allowFlying;
		boolean update = false;
		int flyingCount = 0;
		for (FlightTicket t : this.ticketList) {
			if (t.isDropping()) {
				t.ageTicket();
				if (t.isFalling()) {
					update = true;
				}
			}
			if (t.isFlying()) {
				flyingCount++;
			}
		}
		if ((flyingCount > 0 && !flying) || (flyingCount == 0 && flying)) {
			update = true;
		}
		if (update) {
			this.update();
		}
	}

	public void addTicket(FlightTicket ticket) {
		boolean ticketExists = false;
		for (FlightTicket t : this.ticketList) {
			if (t.getLocation().compareTo(ticket.getLocation()) == 0) {
				AULog.debug("Discarding old ticket %x from %s as duplicate", t.hashCode(), t.getLocation().toString());
				ticketList.remove(t);
				break;
			}
		}
		ticketList.add(ticket);
		AULog.debug("Adding ticket %x from %s", ticket.hashCode(), ticket.getLocation().toString());
		this.update();
	}

	public void removeTicket(BlockPos location) {
		for (FlightTicket t : this.ticketList) {
			if (t.getLocation().compareTo(location) == 0) {
				this.ticketList.remove(t);
				AULog.debug("Removing ticket from %s", location.toString());
				break;
			}
		}
		this.update();
	}

	public void removeTicket(FlightTicket ticket) {
		for (FlightTicket t : this.ticketList) {
			if (t == ticket) {
				this.ticketList.remove(t);
				AULog.debug("Removing ticket from %s", ticket.getLocation().toString());
				break;
			}
		}
		this.update();
	}

	private class FallingTickets<T> implements Predicate<FlightTicket> {
		@Override
		public boolean test(FlightTicket t) {
			if (t.isFalling()) {
				t.setLanded();
				return true;
			}
			return false;
		}
	}

	protected void removeFallingModeTickets() {
		this.removeFallingModeTickets(true);
	}

	private void removeFallingModeTickets(boolean update) {
		ticketList.removeIf(new FallingTickets<FlightTicket>());
		if (update) {
			this.update();
		}
	}

	public int getTicketCount() {
		return ticketList.size();
	}

	public int getFlightTicketCount() {
		int count = 0;
		for (FlightTicket t : this.ticketList) {
			if (t.isFlying()) {
				count++;
			}
		}
		return count;
	}
}
