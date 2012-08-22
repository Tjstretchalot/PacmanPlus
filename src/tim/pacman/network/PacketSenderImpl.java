package tim.pacman.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import tim.pacman.PacmanApplication;

public class PacketSenderImpl extends PacketSender {

	public PacketSenderImpl(PacmanNetworking networking) {
		super(networking);
	}

	@Override
	protected void sendPacket(PlayerPacket packet) {
		ByteBuffer buffer = packet.getData();
		if(buffer.position() == 0)
			buffer.position(buffer.limit());
		buffer.limit(buffer.position());
		System.out.println("Initially:"  + buffer);
		buffer = insertMarkers(buffer);
		buffer.flip(); // presumably we just wrote into this, flip it
		System.out.println("After: " + buffer);
		System.out.print("        [ ");
		while(buffer.hasRemaining())
		{
			System.out.print(buffer.get() + " ");
		}
		System.out.println("]");
		buffer.rewind();
		synchronized(networking)
		{
			for(int i = 0; i < networking.getPlayerChannels().size(); i++)
			{
				if(!networking.running)
					return;
				int from = networking instanceof HostNetworking ? i + 1 : 0;
				if(from >= networking.getPlayers().size())
					from = -1;
				
				if(i < networking.getPlayers().size() - 1 && packet.getPlayer() == networking.getPlayers().get(i + 1) && !packet.sendToPlayer())
				{
					if(from != -1)
						System.out.println("[" + networking.getLocalPlayer().getName() + "] NOT Writing " + packet.getType() + " to " + networking.getPlayerNames().get(from));
					continue;
				}
				SocketChannel conn = networking.getPlayerChannels().get(i);
				
				try {
					if(from != -1)
					{
						System.out.println("[" + networking.getLocalPlayer().getName() + "] Writing " + packet.getType() + " to " + networking.getPlayerNames().get(from));
						System.out.println("[" + networking.getLocalPlayer().getName() + "]   Buffer: " + buffer);
					}
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

	private ByteBuffer insertMarkers(ByteBuffer buffer) {
		if(buffer.limit() != buffer.position())
			throw new IllegalArgumentException("Must be called immediately after a read.");
		int newCapacity = buffer.limit() + 2;

		ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
		newBuffer.put((byte) newCapacity);
		buffer.flip();
		while(buffer.hasRemaining())
			newBuffer.put(buffer.get());
		
		newBuffer.put(PacmanNetworking.MARKER);
		
		return newBuffer;
	}
	
	@Test
	public void testInsertMarkers()
	{
		System.out.println("Testing a one byte buffer.");
		ByteBuffer test = ByteBuffer.allocate(1);
		test.put((byte) 5);
		
		ByteBuffer res = insertMarkers(test);
		res.flip();
		System.out.println("Buffer: " + res);
		System.out.print("Expected: [3, 5, 127], actual: [" + res.get());
		while(res.hasRemaining())
			System.out.print(", " + res.get());
		System.out.println("]");
		
		System.out.println("Testing a 5-byte buffer");
		test = ByteBuffer.allocate(5);
		
		Random gen = new Random();
		gen.nextBytes(test.array());
		
		test.limit(5);
		test.position(5);
		res = insertMarkers(test);
		test.rewind();
		System.out.println("Expected: [7, " + test.get() + ", " + test.get() + ", " + test.get() + ", " + test.get() + ", " + test.get() + ", 127]" + ", actual: " + Arrays.toString(res.array()));
	
		test = ByteBuffer.allocate(10);
		System.out.println("Lastly, a 3-byte buffer with a 10-byte length");
		
		byte[] temp = new byte[3];
		gen.nextBytes(temp);
		
		for(byte b : temp)
			test.put(b);
		test.flip();
		System.out.print("Expected: [5");
		while(test.hasRemaining())
			System.out.print(", " + test.get());
		test.limit(3);
		test.position(3);
		res = insertMarkers(test);
		res.flip();
		System.out.print(", 127], Actual: [" + res.get());
		while(res.hasRemaining())
			System.out.print(", " + res.get());
		System.out.println("]");
		System.out.println(res);
	}
//
//	private ByteBuffer allImportantClone(ByteBuffer buffer) {
//		ByteBuffer res = ByteBuffer.allocate(buffer.capacity());
//		
//		int pos = buffer.position();
//		buffer.rewind();
//		
//		while(buffer.hasRemaining())
//			res.put(buffer.get());
//		
//		res.flip();
//		buffer.rewind();
//		return res;
//	}
	
//	public PacketSenderImpl() 
//	{ 
//		super(null);
//	}

}
