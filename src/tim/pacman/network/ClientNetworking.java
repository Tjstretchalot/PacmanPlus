/**
 * 
 */
package tim.pacman.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.lwjgl.Sys;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;
import tim.pacman.impl.ai.AStarGameMap;
import tim.pacman.impl.multiplayer.AbstractMultiplayerGameMode;
import tim.pacman.impl.multiplayer.ClientMP;
import tim.pacman.impl.multiplayer.MultiplayerData;

/**
 * @author Timothy
 *
 */
public class ClientNetworking extends PacmanNetworking {
	private SocketAddress address;
	private SocketChannel connection;
	
	private String name;
	private ByteBuffer theBuffer;
	public AbstractMultiplayerGameMode gameMode;
	public int numberOfGhosts;
	public int numberOfPlayers;
	private AStarGameMap gameMap;

	/**
	 * @param inetAddress the game mode
	 */
	public ClientNetworking(SocketAddress inetAddress, String name) {
		super();
		address = inetAddress;
		theBuffer = ByteBuffer.allocate(1028);
		running = true;
		this.name = name;
		localPlayer = new ClientMP(name, 0, 0);
		try
		{
			initializeConnection();
			initializeThreads();
			
			synchronized(connectedPlayers)
			{
				connectedPlayers.add(localPlayer);
			}
			
//			System.out.println("Finished. Players: " + getPlayers());
		}catch(IOException exc)
		{
			System.err.println("Failed to initialize client networking: " + exc.getMessage());
			exc.printStackTrace();
			
			Sys.alert("An error occurred", exc.getMessage());
			System.exit(0);
		}
		
		Thread onShutdown = new Thread(new Runnable() {
			@Override
			public void run()
			{
				if(running)
					disconnect();
			}
		});
	}

	private void initializeThreads() {
		mPacketReciever = new PacketRecieverImpl(this);
		mPacketSender = new PacketSenderImpl(this);
		mPacketReciever.start();
		mPacketSender.start();
	}

	public ClientNetworking(LANGame game) {
		this(game.getAddress(), "Player " + (PacmanApplication.getRND().nextInt(999) + 1));
	}

	protected void initializeConnection() throws IOException {
		connection = SocketChannel.open();
		connection.connect(address);
		System.out.println("Successfully connected");
		ByteBuffer buffer = ByteBuffer.allocate(1028); // Writing
		buffer.put(CLIENT_CONNECTED);
		buffer.putInt(name.length());
		for(char c : name.toCharArray())
		{
			buffer.putChar(c);
		}
		buffer.flip(); // Prepare for reading
		//System.out.println("Writing " + buffer);
		connection.write(buffer);
		
		buffer.clear(); // Prepare for writing
		connection.read(buffer);
		
		buffer.flip(); // Prepare for reading
//		System.out.println("Read " + buffer.remaining() + " bytes");
		int numPlayers = buffer.get();
		//System.out.println("Number of players: " + numPlayers);
		
		for(int i = 0; i < numPlayers; i++)
		{
			int numChars = buffer.getInt();
//			System.out.println("Length of the name of player #" + i + ": " + numChars);
			StringBuilder res = new StringBuilder("");
			for(int j = 0; j < numChars; j++)
				res.append(buffer.getChar());
			System.out.println(res.toString());
			ClientMP player = new ClientMP(res.toString(), 0, 0);
			synchronized(connectedPlayers)
			{
				connectedPlayers.add(player);
			}
		}
		
		gameMap = new AStarGameMap(GameMap.GAME_MAP);
		byte mode = buffer.get();
		numberOfPlayers = buffer.get();
		numberOfGhosts = buffer.get();
		gameMode = MultiplayerData.createInstance(mode, this, numberOfGhosts, gameMap);
		
		connection.configureBlocking(false);
		getPlayerChannels().add(connection);
	}
	
	@Override
	public void doTick(long time)
	{
		if(processQueue.size() > 0)
		{
			System.out.println("[" + getLocalPlayer().getName() + "] Processing " + processQueue.size() + " packets.");
			while(processQueue.size() > 0)
				process(processQueue.poll());
		}
	}

	/**
	 * Processes a packet
	 * @param packet the packet to process
	 */
	protected void process(Packet packet) {
		switch(packet.getType())
		{
		case CLIENT_CONNECTED:
			processClientConnected(packet);
			break;
		case CLIENT_DISCONNECTED:
			processClientDisconnected(packet);
			break;
		case PREPARE_GAME:
			processPrepareGame(packet);
			break;
		case START_GAME:
			processStartGame(packet);
			break;
		case PLAYER_UPDATE:
			processPlayerUpdate(packet);
			break;
		case PLAYER_DIRECTION_CHANGED:
			processPlayerDirectionChanged(packet);
			break;
		case PLAYER_VELOCITY_CHANGED:
			processPlayerVelocityChanged(packet);
			break;
		case PLAYER_SCORE_CHANGED:
			processPlayerScoreChanged(packet);
			break;
		case PLAYER_COLLIDED:
			processPlayerCollided(packet);
			break;
		default:
			processCustomPacket(packet);
			break;
		}
	}

	/**
	 * Queues a packet for sending
	 * @param packet the packet
	 */
	public void queueSend(PlayerPacket packet)
	{
		sendQueue.add(packet);
	}
	
	/**
	 * Queues a packet for processing
	 * @param packet the packet
	 */
	public void queueProcess(Packet packet)
	{
		processQueue.add(packet);
	}

	/**
	 * Processes a custom packet type
	 * @param packet the packet
	 */
	protected void processCustomPacket(Packet packet) {
		
	}

	/**
	 * Processes a player collided packet
	 * @param packet the packet
	 */
	protected void processPlayerCollided(Packet packet) {
		
	}

	/**
	 * Processes a player score changed packet
	 * @param packet the packet
	 */
	protected void processPlayerScoreChanged(Packet packet) {
		ByteBuffer data = packet.getData();
		int playerIndex = data.get();
		int newScore = data.getInt();
		
		getPlayers().get(playerIndex).setScore(newScore);
	}

	/**
	 * Processes a player velocity changed packet
	 * @param packet the packet
	 */
	protected void processPlayerVelocityChanged(Packet packet) {
		ByteBuffer data = packet.getData();
		int playerIndex = data.get();
		float newVX = data.getFloat();
		float newVY = data.getFloat();
		
		getPlayers().get(playerIndex).getVelocity().setLocation(newVX, newVY);
	}

	/**
	 * Processes a player direction changed packet
	 * @param packet the packet
	 */
	protected void processPlayerDirectionChanged(Packet packet) {
		ByteBuffer data = packet.getData();
		int playerIndex = data.get();
		float newRotation = data.getFloat();
		
		getPlayers().get(playerIndex).setRotation(newRotation);
	}

	/**
	 * Processes a player update packet
	 * @param packet the packet
	 */
	protected void processPlayerUpdate(Packet packet) {
		ByteBuffer data = packet.getData();
		int playerIndex = data.get();
		float newX = data.getFloat();
		float newY = data.getFloat();
		System.out.println(getLogPre() + getPlayers().get(playerIndex).getName() + " is now at " + newX + ", " + newY);
		getPlayers().get(playerIndex).getLocation().setLocation(newX, newY);
	}

	

	/**
	 * Processes a start game packet
	 * @param packet the packet
	 */
	protected void processStartGame(Packet packet) {
		gameMode.setWaiting(false);
	}

	/**
	 * Processes a prepare game packet
	 * @param packet the packet
	 */
	protected void processPrepareGame(Packet packet) {
		System.out.println(getLogPre() + "Processing prepare game packet");
		gameMode.setWaiting(true);
		PacmanApplication.application.playGame(gameMode, gameMode.getGameMap(), this, this);
		
		ByteBuffer buff = ByteBuffer.allocate(2);
		buff.put(FINISHED_PREPARING);
		
		queueSend(new PlayerPacket(localPlayer, true, FINISHED_PREPARING, buff));
	}

	/**
	 * Processes a client disconnected packet
	 * @param packet the packet
	 */
	protected void processClientDisconnected(Packet packet) {
		ByteBuffer data = packet.getData();
		int ind = data.get();
		
		if(ind == 0)
		{
			System.err.println(getLogPre() + "Host ended server!");
			running = false;
			return;
		}
		
		// Is it this guy?
		
		Player pla = getPlayers().get(ind);
		if(pla == getLocalPlayer())
		{
			System.err.println(getLogPre() + "Forcibly kicked from the server!");
			running = false;
			return;
		}
		System.err.println(pla.getName() + " disconnected.");
		getPlayers().remove(pla);
	}

	/**
	 * Processes a client connected packet
	 * @param packet the packet
	 */
	protected void processClientConnected(Packet packet) {
		System.out.println(getLogPre() + "Client Connected -- Processing");
		ByteBuffer data = packet.getData();
		int len = data.getInt();
		StringBuilder nmBuilder = new StringBuilder(len);
		
		for(int i = 0; i < len; i++)
		{
			nmBuilder.append(data.getChar());
		}
		String nm = nmBuilder.toString();
		System.out.println("  Name: " + nm);
		ClientMP player = new ClientMP(nm, 0f, 0f);
		getPlayers().add(player);
	}

	public void disconnect() {
		try {
			running = false;
			theBuffer.clear();
			theBuffer.put(CLIENT_DISCONNECTED);
			theBuffer.flip();
			connection.write(theBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
