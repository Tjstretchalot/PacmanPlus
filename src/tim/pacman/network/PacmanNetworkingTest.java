package tim.pacman.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Test;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.impl.TagMode;
import tim.pacman.impl.ai.AStarGameMap;
import tim.pacman.impl.multiplayer.MultiplayerTagMode;

public class PacmanNetworkingTest {
	@Test
	public void test() throws Exception {
		GameMap gameMap = new AStarGameMap(GameMap.GAME_MAP);
		GameMode gameMode = new MultiplayerTagMode(gameMap, 1);
		HostNetworking host = new HostNetworking(gameMode, 2, 1, "Host Player");
		
		System.out.println("Created host network successfully " + host.getPlayers());
		
		System.out.println("Attaching client...");
		ClientNetworking client = new ClientNetworking(
				new InetSocketAddress(InetAddress.getLocalHost(), PacmanNetworking.PORT),
				"Test Client");
		Thread.sleep(100);
		
		host.doTick(0l);
		
		System.out.println("Host says Players: " + host.getPlayers());
		System.out.println("Client says Players: " + client.getPlayers());
		assertEquals(host.getPlayers().size(), 2);
		assertEquals(host.getPlayers().get(1).getName(), "Test Client");
		for(int i = 0; i < host.getPlayers().size(); i++)
		{
			assertEquals(host.getPlayers().get(i), client.getPlayers().get(i));
		}
		// TODO move the player around a little
		
		System.out.println("Detaching client...");
		client.disconnect();
		host.doTick(0l);
		Thread.sleep(10);
		host.doTick(0l);
		host.doTick(0l);
		
		assertEquals(host.getPlayers().size(), 1);
	}

}
