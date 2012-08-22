package tim.pacman.network;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tim.pacman.impl.multiplayer.ClientMP;

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
					int from = networking instanceof HostNetworking ? i + 1 : 0;
					try
					{
						int amount = conn.read(buffer);
						if(amount == 0)
							continue;
						else if(amount == -1)
							throw new BufferUnderflowException();
						
						ClientMP player = networking.getPlayers().get(from);
						System.out.println("[" + networking.getLocalPlayer().getName() + "] Read " + amount + " bytes from " + player.getName());
						
						byte desiredLen = buffer.array()[0];
						if(buffer.position() < desiredLen)
						{
							buffer.limit(desiredLen);
							int counter = 0;
							while(buffer.position() < desiredLen && counter < 5)
							{
								System.err.println(networking.getLogPre() + " (" + counter + ") Didn't get the full message! Expected " + desiredLen + " but have " + buffer.position());
								System.err.println(networking.getLogPre() + "   Buffer: " + buffer);
								System.err.print(networking.getLogPre() + "   Array: [" + buffer.array()[0]);
								for(int j = 1; j < buffer.position(); j++)
								{
									System.err.print(", " + buffer.array()[j]);
								}
								System.err.println("]");
								conn.read(buffer);
								counter++;
							}
							if(counter == 5)
							{
								System.err.println(networking.getLogPre() + "Failed to get the full message, discarding it");
								buffer.clear();
								continue;
							}else
							{
								System.err.println(networking.getLogPre() + " Successfully found the rest of the message");
							}
						}
						buffer.flip(); // flip for reading
						ByteBuffer packetBuffer = ByteBuffer.allocate(64); // considerably larger then the largest full packet
						List<ByteBuffer> allBuffers = new ArrayList<ByteBuffer>();
						byte b;
						boolean foundMarker = false;
						System.out.print("           [ ");
						while(buffer.hasRemaining())
						{
							b = buffer.get();
							System.out.print(b + " ");
							if(b == PacmanNetworking.MARKER)
							{
								foundMarker = true;
								packetBuffer.rewind();
								allBuffers.add(packetBuffer);
								packetBuffer = ByteBuffer.allocate(64);
							}else
								packetBuffer.put(b);
						}
						System.out.println("]");
						if(!foundMarker)
							throw new AssertionError("Never found marker!");
						
						buffer.rewind();
						
						System.out.println("[" + networking.getLocalPlayer().getName() + "]   Recieved packet from " + player.getName() + ": " + buffer);
						PlayerPacket packet;
						
						for(ByteBuffer buff : allBuffers)
						{
							buff.get();
							byte type = buff.get();
							System.out.println("[" + networking.getLocalPlayer().getName() + "] Type found: " + type);
							packet = new PlayerPacket(player, true, type, buff);
							networking.processQueue.add(packet);
						}
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
