package tim.pacman.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import tim.pacman.GameControls;
import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;
import tim.pacman.impl.LobbyGui;
import tim.pacman.impl.multiplayer.MultiplayerData;

/**
 * Handles networking via TCP, where one player is inherently
 * the host, determined by the calling class.  The host works
 * the same as the client, except it determines the ghost location
 * and collision detection.<br>
 * <br>
 * Summary of the run:<br>
 * <ol>
 * <li>Player 1 Hosts Game</li>
 * <li>Player 2 Connects</li>
 * <li>Player 3 Connects</li>
 * <li>Player 1 Begins Game</li>
 * <li>Player 2 + 3 recieves the gamemode, gamemap*, starting x and starting y</li>
 * <li>Each player waits until another start packet is sent from the host</li>
 * <li>Player 1 sends start packet</li>
 * <li>Player 2 moves up and adds the packet to the queue</li>
 * <li>Player 1 recieves and broadcasts the packet</li>
 * <li>....</li>
 * <li>Player 1 broadcasts end game packet and the scores of each player, in highest-least order</li>
 * <li>Player 1 closes the connection</li>
 * </ol>
 * <br>*Gamemap is either a byte representing a static map, or a byte
 * representing a custom map and then the map is sent completely
 * 
 * <br>
 * Also, a full player update is sent approximately once every 2 seconds.
 * @author Timothy
 */
public abstract class PacmanNetworking implements GameControls {
	private static final int QUEUE_SIZE = 2000;

	public static final int PORT = 56642;

	public static final byte CLIENT_CONNECTED = 0;
	public static final byte CLIENT_DISCONNECTED = 1;
	public static final byte PREPARE_GAME = 2;
	public static final byte START_GAME = 3;

	/**
	 * A full player update packet, where the players location,
	 * name, velocity, direction and score are sent.
	 */
	public static final byte PLAYER_UPDATE = 4;
	public static final byte PLAYER_DIRECTION_CHANGED = 5;
	public static final byte PLAYER_VELOCITY_CHANGED = 6;
	public static final byte PLAYER_SCORE_CHANGED = 7;

	public static final byte PLAYER_COLLIDED = 8;

	public static final byte PORT_SCAN = 100;

	protected Queue<Packet> processQueue;
	protected Queue<Packet> sendQueue;

	protected PacketReciever mPacketReciever;
	protected PacketSender mPacketSender;

	protected List<Player> connectedPlayers; 
	protected List<SocketChannel> playerChannels;

	public volatile boolean running;

	protected Player localPlayer;


	public PacmanNetworking() {
		processQueue = new ArrayBlockingQueue<Packet>(QUEUE_SIZE);
		sendQueue = new ArrayBlockingQueue<Packet>(QUEUE_SIZE);
		connectedPlayers = new ArrayList<Player>(5);
		playerChannels = new ArrayList<SocketChannel>(5);
	}

	@Override
	public void doTick(long time) {

	}

	public List<SocketChannel> getPlayerChannels() {
		return playerChannels;
	}

	public void setPlayerChannels(List<SocketChannel> playerChannels) {
		this.playerChannels = playerChannels;
	}

	Queue<Packet> getSendQueue() {
		return sendQueue;
	}

	Queue<Packet> getProcessQueue() {
		return processQueue;
	}

	public List<Player> getPlayers() {
		return connectedPlayers;
	}
	
	public List<String> getPlayerNames() {
		List<String> res = new ArrayList<String>(connectedPlayers.size());
		
		for(Player pl : connectedPlayers)
			res.add(pl.getName());
		return res;
	}

	public static ClientNetworking doConnect(LANGame game, String playerName, Gui gui) {
		System.out.println("Connecting to " + game);
		ClientNetworking clientNetworking = new ClientNetworking(game.getAddress(), playerName);
		System.out.println("Success");
		
		LobbyGui lobby = new LobbyGui(clientNetworking, clientNetworking.gameMode, false, 
				MultiplayerData.nameOf(clientNetworking.gameMode), MultiplayerData.infoOf(clientNetworking.gameMode),
				clientNetworking.numberOfGhosts, clientNetworking.numberOfPlayers, "Player " + (PacmanApplication.getRND().nextInt(999) + 1), gui);
		PacmanApplication.application.setGUI(lobby);
		return clientNetworking;
	}

	public static List<LANGame> scanForLocalGames(List<LANGame> result) {
		System.out.println("Scanning for local games");
		SocketAddress addr;
		ByteBuffer buff = ByteBuffer.allocate(1028);

		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
			// System.out.println(address);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		// this code assumes IPv4 is used  
		byte[] ip = address.getAddress();  
		for (int i = 2; i <= 254; i++)  
		{  
			try
			{
				ip[3] = (byte)i;  
				InetAddress address2 = InetAddress.getByAddress(ip);  
				LANGame game = ping(buff, address2);
				if(game == null)
					continue;

				result.add(game); // Add that to the available games
			}catch(Exception exc)
			{
				// System.out.println("Not available");
			}
		}  


		return result;
	}

	private static LANGame ping(ByteBuffer buff, InetAddress address2) throws IOException {
		//System.out.println("Scanning " + address2);
		SocketAddress addr = new InetSocketAddress(address2, PORT);

		//System.out.println("Checking " + addr);
		SocketChannel chan = SocketChannel.open();
		//chan.configureBlocking(false);
		chan.socket().connect(addr, 50); // Attempt to connect 
		if(!chan.isConnected())
		{
			//System.out.println("Connection could not be made");
			return null;
		}
		System.out.println("Success!");
		//chan.configureBlocking(true);
		//chan.finishConnect();
		
		System.out.println("Writing a byte (" + PORT_SCAN + ")");
		buff.clear(); // Clear the buffer in case something is left over
		buff.put(PORT_SCAN); // Tell them you are scanning for games
		buff.flip(); // Prepare for reading
		chan.write(buff); // Send it

		buff.clear(); // Clear the buffer and prepare for writing

		System.out.println("Waiting for a channel read");
		chan.read(buff); // Write into the buffer whatever is left in the channel
		buff.flip(); // Prepare for reading

		System.out.println("Reading an int");
		int numChars = buff.getInt(); // read one int
		System.out.println("Reading " + numChars + " characters");

		StringBuffer name = new StringBuffer(""); 
		for(int j = 0; j < numChars; j++)
			name.append(buff.getChar()); // Read that ints number of chars

		LANGame game = new LANGame(name.toString(), chan.getRemoteAddress());
		System.out.println("Severing connection");
		chan.close(); // Close the channel
		return game;
	}

	public Player getLocalPlayer() {
		return localPlayer;
	}
}
