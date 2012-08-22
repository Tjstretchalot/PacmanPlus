package tim.pacman.network;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import tim.pacman.Player;
import tim.pacman.impl.multiplayer.ClientMP;

public class PlayerPacket extends Packet implements Cloneable {
	private ClientMP player;
	private boolean sendToPlayer;
	
	public PlayerPacket(ClientMP pl, boolean sendToPlayer, byte type, ByteBuffer data) {
		super(type, data);
		this.player = pl;
		this.sendToPlayer = sendToPlayer;
	}
	
	public ClientMP getPlayer()
	{
		return player;
	}
	
	public void setPlayer(ClientMP newPlayer)
	{
		player = newPlayer;
	}

	public boolean sendToPlayer() {
		return sendToPlayer;
	}

	public void setSendToPlayer(boolean sendToPlayer) {
		this.sendToPlayer = sendToPlayer;
	}
	
	@Override
	public Object clone()
	{
		byte[] data = getData().array();
		
		ByteBuffer dataClone = ByteBuffer.allocate(getData().capacity());
		
		for(int i = 0; i < dataClone.capacity(); i++)
		{
			dataClone.put(data[i]);
		}
		
		dataClone.position(getData().position());
		dataClone.limit(getData().limit());
		return new PlayerPacket(player, sendToPlayer, getType(), dataClone);
	}
}
