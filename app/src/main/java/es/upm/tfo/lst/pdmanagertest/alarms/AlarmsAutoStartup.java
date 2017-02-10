package es.upm.tfo.lst.pdmanagertest.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import es.upm.tfo.lst.pdmanagertest.AlarmActivity;
import es.upm.tfo.lst.pdmanagertest.persistance.AlarmsDBInterface;

public class AlarmsAutoStartup extends BroadcastReceiver
{

/* Include in Manifest:
		<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
        <receiver android:name=".alarms.AlarmsAutoStartup" android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
*/

	@Override
	public void onReceive(Context context, Intent intent)
	{
		AlarmsDBInterface adbi = new AlarmsDBInterface(context);
		ArrayList<Alarm> alarms = adbi.getAllAlarms();
		for (Alarm a:alarms) a.activate(context, a.id);
	}

}
