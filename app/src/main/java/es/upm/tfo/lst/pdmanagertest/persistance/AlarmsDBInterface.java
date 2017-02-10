package es.upm.tfo.lst.pdmanagertest.persistance;

import java.util.ArrayList;

import es.upm.tfo.lst.pdmanagertest.alarms.Alarm;
import android.content.Context;
import android.database.Cursor;

public class AlarmsDBInterface
{

	private DataBase db;
	
	public AlarmsDBInterface(Context ctx) { db = new DataBase(ctx); }
	
	public int addAlarm(Alarm a)
	{
		db.open();
			int res = db.addAlarm(a.dow, a.hour, a.min, a.repeat);
		db.close();
		return res;
	}
	
	private Alarm initAlarm(Cursor c)
	{
		int
			id = c.getInt(c.getColumnIndex(DataBase.ALARM_ID)),
			hour = c.getInt(c.getColumnIndex(DataBase.ALARM_HOUR)),
			min = c.getInt(c.getColumnIndex(DataBase.ALARM_MINUTE)),
			dow = c.getInt(c.getColumnIndex(DataBase.ALARM_DAY_OF_WEEK)),
			rep = c.getInt(c.getColumnIndex(DataBase.ALARM_REPEAT));
		Alarm alarm = new Alarm(dow, hour, min, rep);
		alarm.id = id;
		return alarm;
	}
	
	public Alarm getAlarm(int id)
	{
		Alarm alarm = null;
		db.open();
			Cursor c = db.getAlarm(id);
			if (c.moveToFirst()) alarm = initAlarm(c);
		db.close();
		return alarm;
	}
	
	public void deleteAlarm(int id)
	{
		db.open();
			db.deleteAlarm(id);
		db.close();
	}
	
	public ArrayList<Alarm> getAllAlarms()
	{
		db.open();
			ArrayList<Alarm> list = new ArrayList<Alarm>();
			Cursor c = db.getAllAlarms();
			if (c.moveToFirst())
				do
				{
					Alarm alarm = initAlarm(c);
					list.add(alarm);
				}
				while (c.moveToNext());
		db.close();
		return list;
	}
	
}
