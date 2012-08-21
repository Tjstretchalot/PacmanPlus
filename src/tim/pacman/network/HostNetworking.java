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
import tim.pacman.impl.multiplayer.ClientMP;
import tim.pacman.impl.multiplayer.MultiplayerData;

/**
 * @author Timothy
 *
 */
public class HostNetworking extends PacmanNetworking {
	private GameMode gameMode;
	private ServerSocketChannel servChannel;
	private ConnectionHandler mConnectionHandler;
	private Player hostPlayer;
	private int maxPlayers;
	private int numGhosts;

	public HostNetworking(GameMode gameMode, int maxPlayers, int numGhosts, String playerName) {
		this.gameMode = gameMode;
		running = true;
		this.maxPlayers = maxPlayers;
		this.numGhosts = numGhosts;
		
		hostPlayer = new ClientMP(playerName, 0, 0);
		localPlayer = hostPlayer;
		
		System.out.println("Host created: " + gameMode.getClass().getSimpleName() + ", " + maxPlayers +
				", " + numGhosts + ", " + playerName);
		initializeChannel();
		initializeThreads();
		
		connectedPlayers.add(hostPlayer);
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
		if(processQueue.size() > 100)
			System.out.println("System Overloaded! " + processQueue.size() + " Packets need to be evaluated");
		
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
				System.out.println("Client #" + (ind + 1) + " [" + pla.getName() + 
						"] disconnected: Left Server");
				
				ByteBuffer buffer = ByteBuffer.allocate(1028);
				buffer.put(CLIENT_DISCONNECTED);
				buffer.put((byte) ind);
				
				Packet sendPacket = new PlayerPacket(pla, true, CLIENT_DISCONNECTED, buffer);
				sendQueue.add(sendPacket);
				getPlayers().remove(ind);
				getPlayerChannels().remove(ind - 1);
				
				
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
			
			int counter = 0;
			while(true)
			{
				try
				{
					servChannel.bind(address);
					break;
				}catch(IOException ex)
				{
					counter++;
					address = new InetSocketAddress(InetAddress.getLocalHost(), PORT + counter);
					
				}
			}
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
					ByteBuffer buffer = ByteBuffer.allocate(1028); // starts ready for writing
					buffer.clear();
					
					chan.read(buffer); // write into the buffer
					
					buffer.flip(); // Prepare for reading
					
					byte type = buffer.get(); // read a byte
					
					if(type == PORT_SCAN)
					{
						System.out.println("It is a port scan, sending name of the game");
						buffer.clear(); // prepare for writing
						buffer.putInt(hostPlayer.getName().length()); // Put an int
						
						for(char c : hostPlayer.getName().toCharArray())
							buffer.putChar(c);
						buffer.flip(); // Prepare for reading
						chan.write(buffer);
						System.out.println("Done");
						continue;
					}
					int numChars = buffer.getInt(); // Number of chars in the players name
					StringBuilder nmBuilder = new StringBuilder("");
					
					for(int i = 0; i < numChars; i++)
					{
						nmBuilder.append(buffer.getChar());
					}
					
					System.out.println("Name: " + nmBuilder);
					
					int numPlayers = connectedPlayers.size();
					
					buffer.clear(); // Prepare for writing
					buffer.put((byte) numPlayers); // Tell them how many players to expect
//					System.out.println("Number of players: " + numPlayers);
					int counter = 0;
					for(Player pl : connectedPlayers)
					{
						buffer.putInt(pl.getName().length()); // Length of name
//						System.out.println("Length of the name of player #" + counter + ": " + pl.getName().length());
						numChars = pl.getName().length();
						for(int i = 0; i < numChars; i++)
						{
							buffer.putChar(pl.getName().charAt(i));
						}
//						System.out.println("Name: " + pl.getName());
						counter++;
					}
					
					// GameMode, max players and number of ghosts
					buffer.put((byte) MultiplayerData.indexOf(gameMode));
					buffer.put((byte) maxPlayers);
					buffer.put((byte) numGhosts);
					
					buffer.flip(); // Prepare for reading
//					System.out.println("Buffer size: " + buffer);
					chan.write(buffer); // Write the buffer
					// Other data would be read here
					
					chan.configureBlocking(false);
					
					Player player = new Player(nmBuilder.toString(), 0, 0);
					buffer.clear();
					buffer.put(CLIENT_CONNECTED);
					buffer.putInt(player.getName().length());
					for(int i = 0; i < player.getName().length(); i++)
						buffer.putChar(player.getName().charAt(i));
					
					getPlayers().add(player);
					getPlayerChannels().add(chan);
					
					Packet packet = new PlayerPacket(player, false, CLIENT_CONNECTED, buffer);
					sendQueue.add(packet);
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

	public void kickPlayer(Player player) {
		Packet kickPacket = new PlayerPacket(player, true, CLIENT_DISCONNECTED, ByteBuffer.allocate(5));
		processQueue.add(kickPacket);
		sendQueue.add(kickPacket);
	}

	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	public Player getHost() {
		return hostPlayer;
	}
}
