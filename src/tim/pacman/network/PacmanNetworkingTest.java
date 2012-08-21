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
		final String hostName = "Host";
		final String plOneName = "Client 2";
		final String plTwoName = "Client 3";
		GameMap gameMap = new AStarGameMap(GameMap.GAME_MAP);
		GameMode gameMode = new MultiplayerTagMode(gameMap, 1);
		HostNetworking host = new HostNetworking(gameMode, 2, 1, "Host Player");
		
		System.out.println("Created host network successfully " + host.getPlayerNames());
		
		System.out.println("Attaching client...");
		ClientNetworking client = new ClientNetworking(
				new InetSocketAddress(InetAddress.getLocalHost(), PacmanNetworking.PORT),
				plOneName);
		Thread.sleep(100);
		
		host.doTick(0l);
		
		System.out.println(hostName + " says Players: " + host.getPlayerNames());
		System.out.println(plOneName + " says Players: " + client.getPlayerNames());
		assertEquals(host.getPlayers().size(), 2);
		assertEquals(host.getPlayers().get(1).getName(), plOneName);
		
		System.out.println("Attaching client 2");
		
		ClientNetworking client2 = new ClientNetworking(
				new InetSocketAddress(InetAddress.getLocalHost(), PacmanNetworking.PORT),
				plTwoName);
		Thread.sleep(100);
		
		host.doTick(0l);
		client.doTick(0l);
		client2.doTick(0l);
		host.doTick(0l);
		client.doTick(0l);
		client2.doTick(0l);
		
		System.out.println(hostName + " says Players: " + host.getPlayerNames());
		System.out.println(plOneName + " says Players: " + client.getPlayerNames());
		System.out.println(plTwoName + " says Players: " + client2.getPlayerNames());
		assertEquals(hostName + " didn't learn about " + plTwoName + ".", host.getPlayers().size(), 3);
		assertEquals(plOneName + " didn't learn about " + plTwoName + ".", client.getPlayers().size(), 3);
		assertEquals(plTwoName + " does not have the correct number of players.", client2.getPlayers().size(), 3);
		assertEquals(host.getPlayers().get(1).getName(), plOneName);
		assertEquals(host.getPlayers().get(2).getName(), plTwoName);
		
		for(int i = 0; i < host.getPlayers().size(); i++)
		{
			assertEquals(host.getPlayers().get(i), client.getPlayers().get(i));
			assertEquals(host.getPlayers().get(i), client2.getPlayers().get(i));
		}
		// TODO move the player around a little
		
		System.out.println("Detaching client 1...");
		client.disconnect();
		host.doTick(0l);
		Thread.sleep(10);
		host.doTick(0l);
		host.doTick(0l);
		Thread.sleep(200);
		client2.doTick(0l);
		client2.doTick(0l);
		
		
		assertEquals(host.getPlayers().size(), 2);
		assertEquals(host.getPlayers().get(1).getName(), plTwoName);
		
		System.out.println("After client 1 was detached");
		
		System.out.println(hostName + " says Players: " + host.getPlayerNames());
		System.out.println(plTwoName + " says Players: " + client2.getPlayerNames());
		
		for(int i = 0; i < host.getPlayers().size(); i++)
		{
			assertEquals(host.getPlayers().get(i).getName() 
					+ " vs " + client2.getPlayers().get(i).getName(), host.getPlayers().get(i), client2.getPlayers().get(i));
		}
		
		System.out.println("Detaching client 2...");
		client2.disconnect();
		
		Thread.sleep(10);
		host.doTick(0l);
		
		assertEquals(host.getPlayers().size(), 1);
	}

}
