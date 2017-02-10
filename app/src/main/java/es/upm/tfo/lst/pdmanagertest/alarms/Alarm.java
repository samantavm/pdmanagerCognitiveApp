package es.upm.tfo.lst.pdmanagertest.alarms;

import android.content.Context;

public class Alarm
{
	public int id, dow, hour, min, repeat;
	public AlarmInternals ai;
	
	public static final String
		kID		=	"id",
		kDayOfWeek = "dow",
		kHour	=	"hour",
		kMinute	=	"minute",
		kType	=	"type";

	public static final int
		JUST_TODAY = 0,
		JUST_ONCE = 1,
		REPEAT_DAILY = 2,
		REPEAT_WEEKLY = 3;
	
	/*
		Alarm a = new Alarm(0, 13, 49, false);
		int id = db.addAlarm(a);
		a.activate(getApplicationContext(), id);
	 */
	
	// Step 0. Creation of the data
	public Alarm(int dow, int hour, int min, int repeat)
	{
		this.dow = dow;
		this.hour = hour;
		this.min = min;
		this.repeat = repeat;
	}
	
	// Step 1. Setting up the alarm with the given data.
	public void activate(Context ctx, int id)
	{
		this.id = id;
		ai = new AlarmInternals(ctx, id);
		switch(repeat)
		{
			case JUST_TODAY:
			{
				ai.triggerAt(hour, min);
				break;
			}
			case JUST_ONCE:
			{
				ai.triggerAlarmAt(dow, hour, min);
				break;
			}
			case REPEAT_DAILY:
			{
				ai.triggerDailyAlarmAt(dow, hour, min);
				break;
			}
			case REPEAT_WEEKLY:
			{
				ai.triggerWeeklyAlarmAt(dow, hour, min);
				break;
			}
		}
	}
	
	public void deactivate(Context ctx)
	{
		ai = new AlarmInternals(ctx, id);
		ai.cancelAlarm();
	}
}
