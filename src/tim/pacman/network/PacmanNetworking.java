package tim.pacman.network;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import tim.pacman.GameControls;
import tim.pacman.Player;

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
	private static final int QUEUE_SIZE = 25;
	
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
	
	protected Queue<Packet> processQueue;
	protected Queue<Packet> sendQueue;
	
	protected PacketReciever mPacketReciever;
	protected PacketSender mPacketSender;
	
	protected List<Player> connectedPlayers; 
	protected List<SocketChannel> playerChannels;

	public volatile boolean running;
	
	
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

	public static void doConnect(LANGame game) {
		
	}

}
