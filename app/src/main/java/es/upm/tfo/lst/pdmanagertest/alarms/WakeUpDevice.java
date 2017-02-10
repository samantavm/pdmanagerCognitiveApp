package es.upm.tfo.lst.pdmanagertest.alarms;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class WakeUpDevice
{
	public static void awake(Context ctx)
	{
		try
		{
			PowerManager pm = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
					PowerManager.FULL_WAKE_LOCK |
					PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
			wl.acquire(60*1000);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
}
