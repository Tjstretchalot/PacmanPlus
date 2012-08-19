package tim.pacman.network;

import java.util.Queue;

/**
 * This class sends packets on a regular interval, recieved from
 * the PacmanNetworking class.
 * 
 * @author Timothy
 */
public abstract class PacketSender extends Thread {
	protected PacmanNetworking networking;

	/**
	 * Creates a packet sender attached to the specified
	 * queue.
	 * @param networking the networking
	 */
	public PacketSender(PacmanNetworking networking)
	{
		this.networking = networking;
	}
	
	/**
	 * Sends all the queued packets
	 */
	public void sendAllQueued()
	{
		while(networking.getSendQueue().size() > 0)
			sendPacket(networking.getSendQueue().poll());
	}

	protected abstract void sendPacket(Packet packet);
}
