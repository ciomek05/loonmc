package ciomek.loon;

import org.spongepowered.asm.mixin.Unique;

public class TPSTracker {
	@Unique
	private static volatile double tps = 0;

	public static double getTPS()
	{
		return tps;
	}

	public static double getTPSRounded()
	{
		return Math.round(tps * 10.0) / 10.0;
	}

	public static void setTPS(double tps)
	{
		TPSTracker.tps = tps;
	}
}
