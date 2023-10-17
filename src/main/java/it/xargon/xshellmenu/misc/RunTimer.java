package it.xargon.xshellmenu.misc;

public class RunTimer {
	private String cl;
	private long s;
	
	public RunTimer(String cl) {
		s = System.currentTimeMillis();
		this.cl = cl;
		System.out.println("START -> " + cl);
	}
	
	public void tick(String tag) {
		long t = System.currentTimeMillis();
		System.out.println("TICK  -> " + cl + " <" + tag + ">: " + (t - s) + "ms");
		s = t;
	}
}
