package tim.pacman.network;

import java.net.SocketAddress;

public class LANGame {
	private String name;
	private SocketAddress address;
	
	public LANGame(String name, SocketAddress address) {
		super();
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SocketAddress getAddress() {
		return address;
	}

	public void setAddress(SocketAddress address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "LANGame [name=" + name + ", address=" + address + "]";
	}
	
	
}
