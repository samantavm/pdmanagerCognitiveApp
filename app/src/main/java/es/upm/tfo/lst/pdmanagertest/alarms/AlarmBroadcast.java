package es.upm.tfo.lst.pdmanagertest.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import es.upm.tfo.lst.pdmanagertest.AlarmActivity;

public class AlarmBroadcast extends BroadcastReceiver
{

/* Include in Manifest:
        <receiver android:name="alarms.AlarmBroadcast" android:exported="false">
	   		<intent-filter>
				<action android:name="pdmanageralarm" />
			</intent-filter>
		</receiver>
*/
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle b = intent.getExtras();
		if (b!=null)
		{
			Intent i = new Intent(context, AlarmActivity.class);
			i.putExtra(Alarm.kID, -1); // We need to send just a message to the activity.
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}

}
