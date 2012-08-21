package tim.pacman.network;

import java.nio.ByteBuffer;

public class Packet {
	private byte type;
	private ByteBuffer data;
	
	public Packet(byte type, ByteBuffer data)
	{
		this.type = type;
		this.data = data;
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public ByteBuffer getData() {
		return data;
	}

	public void setData(ByteBuffer data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Packet [type=" + type + ", data=" + data + "]";
	}
	
	
}
