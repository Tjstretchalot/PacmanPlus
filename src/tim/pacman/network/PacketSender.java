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
	
	@Override
	public void run()
	{
		while(networking.running)
		{
			if(networking.getSendQueue().size() > 0)
			{
				int numPlayers = networking.getPlayers().size() - 1;
				System.out.println("Sending " + networking.getSendQueue().size() + " packet(s) to ~" +
						numPlayers + " players.");
				sendAllQueued();
			}
			try {
				Thread.sleep(35);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Sends all the queued packets
	 */
	public void sendAllQueued()
	{
		while(networking.getSendQueue().size() > 0)
			sendPacket((PlayerPacket) networking.getSendQueue().poll());
	}

	protected abstract void sendPacket(PlayerPacket packet);
}
