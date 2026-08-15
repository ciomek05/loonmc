package ciomek.loon;

import org.spongepowered.asm.mixin.Unique;

public class TPSTracker {
	@Unique
	private static volatile double tps = 0;

	public static double getTPS()
	{
		return tps;
	}

	public static void setTPS(double tps)
	{
		TPSTracker.tps = tps;
	}
}
