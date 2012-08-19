/**
 * 
 */
package tim.pacman.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import tim.pacman.GameMode;
import tim.pacman.Player;

/**
 * @author Timothy
 *
 */
public class HostNetworking extends PacmanNetworking {
	private GameMode gameMode;
	private ServerSocketChannel servChannel;
	private ConnectionHandler mConnectionHandler;

	public HostNetworking(GameMode gameMode) {
		this.gameMode = gameMode;
		initializeChannel();
		initializeThreads();
	}

	private void initializeThreads() {
		mPacketReciever = new PacketRecieverImpl(this);
		mPacketSender = new PacketSenderImpl(this);
		mConnectionHandler = new ConnectionHandler();
		
		mConnectionHandler.start();
		mPacketReciever.start();
		mPacketSender.start();
	}
	
	@Override
	public void doTick(long time)
	{
		super.doTick(time);
		System.out.println("Ticking... " + processQueue.size() + " Packets need to be evaluated");
		
		while(processQueue.size() > 0 && running)
		{
			process(processQueue.poll());
		}
	}

	private void process(Packet packet) {
		synchronized(this)
		{
			switch(packet.getType())
			{
			case CLIENT_DISCONNECTED:
				Player pla = ((PlayerPacket) packet).getPlayer();
				int ind = getPlayers().indexOf(pla);
				getPlayers().remove(ind);
				getPlayerChannels().remove(ind);

				System.out.println("Client #" + (ind + 1) + " [" + pla.getName() + 
						"] disconnected: Left Server");
				break;
			default:
				System.err.println("Odd type retrieved: " + packet.getType());
			}
		}
	}

	private void initializeChannel() {
		try {
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(true);
			SocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), PORT);
			servChannel.bind(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class ConnectionHandler extends Thread {
		@Override
		public void run()
		{
			while(running)
			{
				try
				{
					SocketChannel chan = servChannel.accept();
					if(chan == null)
					{
						System.err.println("Channel is null in connection handler");
					}
					chan.configureBlocking(true);
					System.out.println("Client connected: " + chan);
					ByteBuffer buffer = ByteBuffer.allocate(1028);
					chan.read(buffer);
					buffer.flip(); // Read the number of bytes
					int numChars = buffer.getInt(); // Number of chars in the players name
					StringBuilder nmBuilder = new StringBuilder("");
					
					for(int i = 0; i < numChars; i++)
					{
						nmBuilder.append(buffer.getChar());
					}
					
					System.out.println("Name-Length: " + numChars +"; Name: " + nmBuilder);
					
					// Other data would be read here
					
					chan.configureBlocking(false);
					
					Player player = new Player(nmBuilder.toString(), 0, 0);
					getPlayers().add(player);
					getPlayerChannels().add(chan);
				}catch(IOException exc)
				{
					System.err.println("An error occurred accepting connection: " + exc.getMessage());
				}
			}
		}
	}

	/**
	 * Closes the socket and cleans up resources
	 */
	public void cleanup() {
		running = false;
		
		try {
			servChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
