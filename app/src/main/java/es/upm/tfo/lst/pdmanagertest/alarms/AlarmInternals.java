package es.upm.tfo.lst.pdmanagertest.alarms;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmInternals
{

	private AlarmManager am;
	private int mode = AlarmManager.RTC_WAKEUP;
	private PendingIntent pi;
	private final String alarmIntent = "pdmanageralarm";
	
	/**
	 * Constructs an instance of this object with RTC mode by default.
	 * It is identified by the given id and 
	 * @param ctx
	 * @param id The id of the alarm.
	 */
	public AlarmInternals(Context ctx, int id)
	{
		Intent intent = new Intent(alarmIntent);
		intent.putExtra(Alarm.kID, id);
		am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		pi = PendingIntent.getBroadcast(ctx, id, intent, 0);
	}
	
	/**
	 * Cancels the alarm.
	 */
	public void cancelAlarm() { am.cancel(pi); }
	
	/**
	 * If set, when the alarm triggers it will wake up the device if it is switched off.
	 */
	public void setWakeUpDevice() { this.mode = AlarmManager.RTC_WAKEUP; }
	
	/**
	 * Default alarm mode, it will trigger only if the device is on.
	 */
	public void setDontWakeUpDevice() { this.mode = AlarmManager.RTC; }
	
	
	private Calendar retrieveHMSCalendarAt(int hour, int minute, int seconds)
	{
		long millis = System.currentTimeMillis();
		Calendar
			now = Calendar.getInstance(),
			calendar = Calendar.getInstance();
		now.setTimeInMillis(millis);
		calendar.setTimeInMillis(millis);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, seconds);
		if (calendar.before(now)) calendar.add(Calendar.DAY_OF_YEAR, 1);
		return calendar;
	}
	
	private Calendar retrieveHMSCalendarInto(int hour, int minute, int seconds)
	{
		long millis = System.currentTimeMillis();
		Calendar
			now = Calendar.getInstance(),
			calendar = Calendar.getInstance();
		now.setTimeInMillis(millis);
		calendar.setTimeInMillis(millis);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		calendar.add(Calendar.MINUTE, minute);
		calendar.add(Calendar.SECOND, seconds);
		if (calendar.before(now)) calendar.add(Calendar.DAY_OF_YEAR, 1);
		return calendar;
	}
	
	/**
	 * Example: retrieveDHMCalendar(Calendar.THURSDAY, 18, 28)
	 */
	private Calendar retrieveDHMCalendar(int day, int hour, int minute)
	{
		long millis = System.currentTimeMillis();
		Calendar
			now = Calendar.getInstance(),
			calendar = Calendar.getInstance();
		now.setTimeInMillis(millis);
		calendar.setTimeInMillis(millis);
		calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        if (calendar.before(now)) calendar.add(Calendar.WEEK_OF_YEAR, 1);
		return calendar;
	}
	
	/**
	 * Sets an alarm at the given hour and time from the current time.
	 * If the given time already passed, the alarm will be triggered for tomorrow.
	 * @param hour
	 * @param minute
	 */
	public void triggerAt(int hour, int minute)
	{
		Calendar calendar = retrieveHMSCalendarAt(hour, minute, 0);
		am.set(mode, calendar.getTimeInMillis(), pi);
	}
	
	/**
	 * Sets an alarm to be triggered into the given time from the
	 * moment this function is called.
	 * @param hour
	 * @param minute
	 * @param seconds
	 */
	public void triggerInto(int hour, int minute, int seconds)
	{
		Calendar calendar = retrieveHMSCalendarInto(hour, minute, seconds);
		am.set(mode, calendar.getTimeInMillis(), pi);
	}
	
	/**
	 * Sets an alarm at the given calendar.
	 * @param calendar
	 */
	public void triggerAt(Calendar calendar)
	{
		am.set(mode, calendar.getTimeInMillis(), pi);
	}
	
	/**
	 * Sets an alarm at the given day/time without repetition.
	 * @param calendar
	 */
	public void triggerAlarmAt(int day, int hour, int minute)
	{
		Calendar calendar = retrieveDHMCalendar(day, hour, minute);
		am.set(mode, calendar.getTimeInMillis(), pi);
	}
	
	/**
	 * Sets an alarm at the given calendar with daily repetition.
	 * @param calendar
	 */
	public void triggerDailyAlarmAt(int day, int hour, int minute)
	{
		Calendar calendar = retrieveDHMCalendar(day, hour, minute);
		am.setRepeating(mode, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
	}
	
	/**
	 * Sets an alarm at the given calendar with weekly repetition.
	 * @param calendar
	 */
	public void triggerWeeklyAlarmAt(int day, int hour, int minute)
	{
		Calendar calendar = retrieveDHMCalendar(day, hour, minute);
		am.setRepeating(mode, calendar.getTimeInMillis(), 7*AlarmManager.INTERVAL_DAY, pi);
	}
	
}
