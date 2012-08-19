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

/**
 * @author Timothy
 *
 */
public class ClientNetworking extends PacmanNetworking {

	private SocketAddress address;
	private SocketChannel connection;
	
	private String name;
	private ByteBuffer theBuffer;

	/**
	 * @param inetAddress the game mode
	 */
	public ClientNetworking(SocketAddress inetAddress, String name) {
		address = inetAddress;
		theBuffer = ByteBuffer.allocate(1028);
		this.name = name;
		
		try
		{
			initializeConnection();
		}catch(IOException exc)
		{
			System.err.println("Failed to initialize client networking: " + exc.getMessage());
			exc.printStackTrace();
			
			Sys.alert("An error occurred", exc.getMessage());
			System.exit(0);
		}
	}

	protected void initializeConnection() throws IOException {
		connection = SocketChannel.open();
		connection.connect(address);
		ByteBuffer buffer = ByteBuffer.allocate(1028);
		buffer.putInt(name.length());
		for(char c : name.toCharArray())
		{
			buffer.putChar(c);
		}
		buffer.flip();
		connection.write(buffer);
		
		connection.configureBlocking(false);
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
	public void queueSend(Packet packet)
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
	}

	/**
	 * Processes a player velocity changed packet
	 * @param packet the packet
	 */
	protected void processPlayerVelocityChanged(Packet packet) {
	}

	/**
	 * Processes a player direction changed packet
	 * @param packet the packet
	 */
	protected void processPlayerDirectionChanged(Packet packet) {
	}

	/**
	 * Processes a player update packet
	 * @param packet the packet
	 */
	protected void processPlayerUpdate(Packet packet) {
	}

	/**
	 * Processes a start game packet
	 * @param packet the packet
	 */
	protected void processStartGame(Packet packet) {
	}

	/**
	 * Processes a prepare game packet
	 * @param packet the packet
	 */
	protected void processPrepareGame(Packet packet) {
	}

	/**
	 * Processes a client disconnected packet
	 * @param packet the packet
	 */
	protected void processClientDisconnected(Packet packet) {
	}

	/**
	 * Processes a client connected packet
	 * @param packet the packet
	 */
	protected void processClientConnected(Packet packet) {
	}

	public void disconnect() {
		try {
			theBuffer.clear();
			theBuffer.put(CLIENT_DISCONNECTED);
			theBuffer.flip();
			connection.write(theBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
