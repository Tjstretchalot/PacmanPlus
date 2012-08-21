package tim.pacman.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PacketSenderImpl extends PacketSender {

	public PacketSenderImpl(PacmanNetworking networking) {
		super(networking);
	}

	@Override
	protected void sendPacket(PlayerPacket packet) {
		ByteBuffer buffer = packet.getData();
		buffer.flip(); // presumably we just wrote into this, flip it
		synchronized(networking)
		{
			for(int i = 0; i < networking.getPlayerChannels().size(); i++)
			{
				if(!networking.running)
					return;
				if(packet.getPlayer() == networking.getPlayers().get(i + 1) && !packet.sendToPlayer())
					continue;
				SocketChannel conn = networking.getPlayerChannels().get(i);
				
				try {
					System.out.println("Writing " + buffer + " to " + networking.getPlayerNames().get(i + 1));
					conn.write(buffer);
				} catch (IOException e) {
					System.err.println("Problem writing to channel #" + i + ": " + e.getCause());
					networking.getPlayerChannels().remove(i);
					networking.getPlayers().remove(i);
					e.printStackTrace();
				}
				buffer.rewind();
			}
		}
	}

}
