package es.upm.tfo.lst.pdmanagertest.tools;

import android.content.Context;
import android.os.Vibrator;

public class Vibr
{

	private Vibrator v;
	
	public Vibr(Context c) { v = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE); }
	
	public void vibrate(int ms) { if (v!=null) v.vibrate(ms); }
	public void vibrate(long[] pattern) { if (v!=null) v.vibrate(pattern, -1); }
}
