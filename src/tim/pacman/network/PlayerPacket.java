package tim.pacman.network;

import java.nio.ByteBuffer;

import tim.pacman.Player;

public class PlayerPacket extends Packet {
	private Player player;
	private boolean sendToPlayer;
	
	public PlayerPacket(Player pl, boolean sendToPlayer, byte type, ByteBuffer data) {
		super(type, data);
		this.player = pl;
		this.sendToPlayer = sendToPlayer;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player newPlayer)
	{
		player = newPlayer;
	}

	public boolean sendToPlayer() {
		return sendToPlayer;
	}

	public void setSendToPlayer(boolean sendToPlayer) {
		this.sendToPlayer = sendToPlayer;
	}

}
