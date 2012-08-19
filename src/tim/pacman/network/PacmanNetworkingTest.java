package tim.pacman.network;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Test;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.impl.TagMode;
import tim.pacman.impl.ai.AStarGameMap;

public class PacmanNetworkingTest {
	@Test
	public void test() throws Exception {
		GameMap gameMap = new AStarGameMap(GameMap.GAME_MAP);
		GameMode gameMode = new TagMode(gameMap);
		HostNetworking host = new HostNetworking(gameMode);
		
		System.out.println("Created host network successfully");
		
		System.out.println("Attaching client...");
		ClientNetworking client = new ClientNetworking(
				new InetSocketAddress(InetAddress.getLocalHost(), PacmanNetworking.PORT),
				"Test Client");
		Thread.sleep(25);
		
		
		assertEquals(host.getPlayers().size(), 1);
		assertEquals(host.getPlayers().get(0).getName(), "Test Client");
		// TODO move the player around a little
		
		System.out.println("Detaching client...");
		client.disconnect();
		host.doTick(0l);
		Thread.sleep(10);
		host.doTick(0l);
		
		assertEquals(host.getPlayers().size(), 0);
	}

}
