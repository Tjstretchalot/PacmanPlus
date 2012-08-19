package tim.pacman.network;

import java.util.Queue;

/**
 * This handles recieving arbitrary packets, reading the data into a byte
 * buffer and attaching it to the appropriate queue to be
 * handled by the PacmanNetworking class.
 */
public abstract class PacketReciever extends Thread {
	protected PacmanNetworking networking;

	/**
	 * Creates a packet reciever
	 * @param toSend the queue to put in packets that need sending
	 * @param toProcess the queue to put in packets that need processing
	 */
	public PacketReciever(PacmanNetworking networking) {
		super();
		this.networking = networking;
	}
	
	@Override
	public void run()
	{
		
	}
}
