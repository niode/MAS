package Grads;

import Ares.World.Info.SurroundInfo;

public class Surround extends Payload {
	private SurroundInfo surround;
	
	public Surround() {
	}

	public Surround(SurroundInfo surround) {
		super();
		this.surround = surround;
	}

	public SurroundInfo getSurround() {
		return surround;
	}

	public void setSurround(SurroundInfo surround) {
		this.surround = surround;
	}
}
