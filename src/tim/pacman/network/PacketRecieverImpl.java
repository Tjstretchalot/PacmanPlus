package tim.pacman.network;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PacketRecieverImpl extends PacketReciever {
	
	public PacketRecieverImpl(PacmanNetworking networking) {
		super(networking);
	}
	
	@Override
	public void run()
	{
		ByteBuffer buffer = ByteBuffer.allocate(1028);
		while(networking.running)
		{
			synchronized(networking)
			{
				//System.out.println("[" + networking.getLocalPlayer().getName() + "] Checking channels for new messages...");
				for(int i = 0; i < networking.getPlayerChannels().size(); i++)
				{
					if(!networking.running)
						return;
					SocketChannel conn = networking.getPlayerChannels().get(i);
					try
					{
						int amount = conn.read(buffer);
						if(amount == 0)
							continue;
						else if(amount == -1)
							throw new BufferUnderflowException();
						buffer.flip(); // flip for reading
						System.out.println(buffer);
						byte type = buffer.get(); // read the type.
						System.out.println(type);
						Packet packet = new PlayerPacket(networking.getPlayers().get(i + 1), false, type, buffer);
						int from = networking instanceof HostNetworking ? i + 1 : i;
						System.out.println("Recieved packet from " + networking.getPlayers().get(from).getName() + ": " + packet);
						buffer = ByteBuffer.allocate(1028);
						networking.processQueue.add(packet);
					}catch(Exception exc)
					{
						if(!networking.running)
							return;
						String msg = exc.getMessage();
						if(exc instanceof BufferUnderflowException)
							msg = "Time Out / Application Crash";
						System.err.println("Client #" + (i + 1) + " [" + networking.getPlayers().get(i).getName() + 
								"] disconnected: "  + msg);
						networking.getPlayerChannels().remove(i);
						networking.getPlayers().remove(i);
						
						exc.printStackTrace();
					}
				}
			}
			
			try {
				Thread.sleep(35);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
