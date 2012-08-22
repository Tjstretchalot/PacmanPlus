package tim.pacman.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Test;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.PacmanApplication;
import tim.pacman.impl.TagMode;
import tim.pacman.impl.ai.AStarGameMap;
import tim.pacman.impl.multiplayer.AbstractMultiplayerGameMode;
import tim.pacman.impl.multiplayer.ClientMP;
import tim.pacman.impl.multiplayer.MultiplayerTagMode;

public class PacmanNetworkingTest {
	
	@Test
	public void test() throws Exception
	{
		for(int i = 0; i < 2; i++)
		{
			testGame();
			System.gc();
		}
	}
	
	public void testGame() throws Exception {
		final String hostName = "Host";
		final String plOneName = "Client 2";
		final String plTwoName = "Client 3";
		GameMap gameMap = new AStarGameMap(GameMap.GAME_MAP);
		HostNetworking host = new HostNetworking(null, 2, 1, "Host Player");
		AbstractMultiplayerGameMode gameMode = new MultiplayerTagMode(gameMap, host, 1);
		host.gameMode = gameMode;
		
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
		
		playSampleGame(gameMode, host, client, client2);
		// TODO move the player around a little
		
//		System.out.println("Detaching client 1...");
//		client.disconnect();
//		host.doTick(0l);
//		Thread.sleep(10);
//		host.doTick(0l);
//		host.doTick(0l);
//		Thread.sleep(200);
//		client2.doTick(0l);
//		client2.doTick(0l);
//		
//		
//		assertEquals(host.getPlayers().size(), 2);
//		assertEquals(host.getPlayers().get(1).getName(), plTwoName);
//		
//		System.out.println("After client 1 was detached");
//		
//		System.out.println(hostName + " says Players: " + host.getPlayerNames());
//		System.out.println(plTwoName + " says Players: " + client2.getPlayerNames());
//		
//		for(int i = 0; i < host.getPlayers().size(); i++)
//		{
//			assertEquals(host.getPlayers().get(i).getName() 
//					+ " vs " + client2.getPlayers().get(i).getName(), host.getPlayers().get(i), client2.getPlayers().get(i));
//		}
//		
//		System.out.println("Detaching client 2...");
//		client2.disconnect();
//		
//		Thread.sleep(10);
//		host.doTick(0l);
//		
//		assertEquals(host.getPlayers().size(), 1);
	}

	private volatile boolean running;
	
	private void playSampleGame(AbstractMultiplayerGameMode gameMode,
			final HostNetworking host, final ClientNetworking... clients) throws InterruptedException {
		System.out.println(" -- Beginning Game -- ");
		running = true;
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				while(running)
				{
					long time = PacmanApplication.getTime();
					host.doTick(time);
					for(ClientNetworking client : clients)
					{
						client.doTick(time);
					}
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		
		});
		th.start();
		
		host.startGame();
		
		System.out.println(" - Game Started - ");
		
		Thread.sleep(3000);
		
		System.out.println("*******************BEGIN ANALYSIS OF CLIENTS FOR ACCURACY*******************");
		
		System.out.println(host.getLogPre() + "Players:" + host.getPlayers());
		for(ClientNetworking networking : clients)
		{
			System.out.println(networking.getLogPre() + "Players: " + host.getPlayers());
			Thread.sleep(25);
			System.out.println("Comparing against hosts...");
			for(int i = 0; i < networking.getPlayers().size(); i++) {
				ClientMP pla = host.getPlayers().get(i);
				ClientMP pla2 = networking.getPlayers().get(i);
				assertEquals(pla.getName() + " does not match " + pla2.getName(), pla, pla2);
			}
		}
		
		System.out.println("*******************ANALYSIS COMPLETE*******************");
		
		System.out.println(" -- Ending Game -- ");
		host.endGame();
		Thread.sleep(200);
		running = false;
		System.out.println(" - Game Ended - ");
	}

}
