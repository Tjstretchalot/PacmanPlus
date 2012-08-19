package tim.pacman.network;

import java.nio.ByteBuffer;

import tim.pacman.Player;

public class PlayerPacket extends Packet {
	private Player player;
	
	public PlayerPacket(Player pl, byte type, ByteBuffer data) {
		super(type, data);
		this.player = pl;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player newPlayer)
	{
		player = newPlayer;
	}

}
