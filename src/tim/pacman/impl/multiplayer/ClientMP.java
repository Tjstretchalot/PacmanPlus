package tim.pacman.impl.multiplayer;

import java.util.Comparator;

import tim.pacman.Player;

public class ClientMP extends Player {
	public static final Comparator<ClientMP> COMPARATOR = new Comparator<ClientMP>() {

		@Override
		public int compare(ClientMP o1, ClientMP o2) {
			return o1.getScore() - o2.getScore();
		}

	};
	
	private boolean prepared;

	public ClientMP(String nm, float x, float y) {
		super(nm, x, y);
	}

	public boolean isPrepared() {
		return prepared;
	}
	
	public void setPrepared(boolean b)
	{
		prepared = b;
	}

}
