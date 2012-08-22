/**
 * 
 */
package tim.pacman.network;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.GhostController;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;
import tim.pacman.impl.multiplayer.AbstractMultiplayerGameMode;
import tim.pacman.impl.multiplayer.ClientMP;
import tim.pacman.impl.multiplayer.MultiplayerData;

/**
 * @author Timothy
 *
 */
public class HostNetworking extends PacmanNetworking {
	public AbstractMultiplayerGameMode gameMode;
	private ServerSocketChannel servChannel;
	private ConnectionHandler mConnectionHandler;
	private ClientMP hostPlayer;
	private int maxPlayers;
	private int numGhosts;
	
	/**
	 * The controls that are actually moving
	 * the ghost.  This is just a wrapper to
	 * make sure subclasses get it.
	 */
	protected GhostController realGhostControls;

	public HostNetworking(AbstractMultiplayerGameMode gameMode, int maxPlayers, int numGhosts, String playerName) {
		this.gameMode = gameMode;
		running = true;
		this.maxPlayers = maxPlayers;
		this.numGhosts = numGhosts;
		
		hostPlayer = new ClientMP(playerName, 0, 0);
		localPlayer = hostPlayer;
		initializeChannel();
		initializeThreads();
		
		connectedPlayers.add(hostPlayer);
		
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				endGame();
			}
			
		});
		
		Runtime.getRuntime().addShutdownHook(th);
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
			process((PlayerPacket) processQueue.poll());
		}
	}

	private void process(PlayerPacket packet) {
		synchronized(this)
		{
			ByteBuffer buffer = packet.getData();
			if(buffer.position() == buffer.limit())
				buffer.position(0);
			buffer.get();
			System.out.println(getLogPre() + " Processing: " + buffer);
			switch(packet.getType())
			{
			case CLIENT_DISCONNECTED:
				ClientMP pla = packet.getPlayer();
				int ind = getPlayers().indexOf(pla);
				System.out.println("Client #" + (ind + 1) + " [" + pla.getName() + 
						"] disconnected: Left Server");
				
				buffer.clear();
				buffer.put(CLIENT_DISCONNECTED);
				buffer.put((byte) ind);
				
				PlayerPacket sendPacket = new PlayerPacket(pla, true, CLIENT_DISCONNECTED, buffer);
				sendQueue.add(sendPacket);
				getPlayers().remove(ind);
				if(ind != 0)
					getPlayerChannels().remove(ind - 1);
				break;
			case FINISHED_PREPARING:
				packet.getPlayer().setPrepared(true);
				System.out.println(packet.getPlayer() + " is prepared.");
				break;
			case PLAYER_UPDATE:
				if(packet.getPlayer() != hostPlayer)
				{
					System.err.println(packet.getPlayer().getName() + " is attempting to hack!");
					break;
				}
				int playerIndex = buffer.get();
				float newX = buffer.getFloat();
				float newY = buffer.getFloat();
				
				getPlayers().get(playerIndex).getLocation().setLocation(newX, newY);
				System.out.println("Updated " + getPlayers().get(playerIndex).getName() + "'s location.");
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
					
					ClientMP player = new ClientMP(nmBuilder.toString(), 0, 0);
					buffer.clear();
					buffer.put(CLIENT_CONNECTED);
					buffer.putInt(player.getName().length());
					for(int i = 0; i < player.getName().length(); i++)
						buffer.putChar(player.getName().charAt(i));
					
					getPlayers().add(player);
					getPlayerChannels().add(chan);
					
					PlayerPacket packet = new PlayerPacket(player, false, CLIENT_CONNECTED, buffer);
					sendQueue.add(packet);
				}catch(IOException exc)
				{
					System.err.println("An error occurred accepting connection: " + exc.getMessage());
					if(!running)
					{
						return;
					}
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

	public void kickPlayer(ClientMP player) {
		PlayerPacket kickPacket = new PlayerPacket(player, true, CLIENT_DISCONNECTED, ByteBuffer.allocate(5));
		processQueue.add(kickPacket);
		sendQueue.add(kickPacket);
	}

	/**
	 * Starts the game.
	 */
	public void startGame() {
		ByteBuffer buffer = ByteBuffer.allocate(1028);
		buffer.put(PREPARE_GAME);
		PlayerPacket packet = new PlayerPacket(hostPlayer, true, PREPARE_GAME, buffer);
		sendQueue.add(packet);
		
		// Prepare the game locally
		PacmanApplication.application.playGame(gameMode, gameMode.getGameMap(), this, this);
		hostPlayer.setPrepared(true);
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run()
			{
				while(!allPlayersPrepared())
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Done waiting! All players are ready to go");
				gameMode.setWaiting(false);
				spawnAllPlayers();
				ByteBuffer buffer = ByteBuffer.allocate(1028);
				buffer.put(START_GAME);
				
				PlayerPacket plPacket = new PlayerPacket(getLocalPlayer(), false, START_GAME, buffer);
				sendQueue.add(plPacket);
				// processQueue.add((Packet) plPacket.clone());
			}
		});
		th.start();
		
	}

	protected void spawnAllPlayers() {
		GameMap map = gameMode.getGameMap();
		for(byte i = 0; i < connectedPlayers.size(); i++)
		{
			ClientMP client = connectedPlayers.get(i);
			Point2D.Float location = map.fromGridLocation(
					map.chooseRandomUnfilledMP(connectedPlayers, GameMap.PLAYER_SPAWNER)
					);
			
			ByteBuffer buffer = ByteBuffer.allocate(1028);
			buffer.put(PLAYER_UPDATE);
			buffer.put(i);
			buffer.putFloat(location.x + 2);
			buffer.putFloat(location.y + 2);
			
			PlayerPacket packet = new PlayerPacket(hostPlayer, true, PLAYER_UPDATE, buffer);
			processQueue.add(packet);
			if(client == hostPlayer)
				packet.setSendToPlayer(false);
			sendQueue.add((PlayerPacket) packet.clone());
			buffer.clear();
		}
	}

	protected boolean allPlayersPrepared() {
		for(ClientMP pla : connectedPlayers)
		{
			if(!pla.isPrepared())
			{
				System.out.println(pla.getName() + " is not prepared.");
				return false;
			}
		}
		return true;
	}

	public Player getHost() {
		return hostPlayer;
	}

	public void endGame() {
		synchronized(this)
		{
			
			for(ClientMP client : connectedPlayers)
			{
				kickPlayer(client);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			running = false;
			try {
				servChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
