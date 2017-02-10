package es.upm.tfo.lst.pdmanagertest.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase
{

	// Database name and version
	private static final String NameDB = "PDManager.db";
	private static final int VersionDB = 1;
	
	// Adapter resources
	private SQLiteDatabase db;
	private DataBaseAsistant dba;

	// SQLite tokens
	@SuppressWarnings("unused")
	private static final String
		OPEN = " (",
		CLOSE = ")",
		SEP = ",",
		QUOTE = "'",
		SEPSTMT = ";",
		CREATE = "CREATE ",
		SELCOUNT = " SELECT COUNT(*) FROM ",
		
		// Tables
		TABLE = "TABLE ",
		TXTPK = " text primary key,",
		TEXT = " text",
		INTEGER = " integer",
		INTEGERPK = " integer primary key,",
		INTEGERPKAI = " integer primary key autoincrement,",
		NOTNULL = " not null ",
		
		// Triggers
		AFTER = " AFTER",
		DELETE = " DELETE",
		FROM = " FROM ",
		ON = " ON ",
		TRIGGER = "TRIGGER ",
		WHEN = " WHEN",
		BEGIN = " BEGIN ",
		END = " END",

		
		// DB Management
		DROP = "DROP TABLE IF EXISTS ",
		SEQUENCE = "sqlite_sequence WHERE name=",
		
		// Order tokens
		ASC = " ASC",
		DESC = " DESC",
		
		// Ops
		MAX = "MAX", // MAX(int_value_in_table)

		// Comparision tokens
		EQZERO = "==0",
		EQ = " = '",
		ENDEQ = "'",
		EQ_SAFE = "=?";

	// Tables
	public static final String
		AlarmsTable = "AlarmsTable",
			ALARM_ID = "id",
			ALARM_TEXT = "text",
			ALARM_VOICE = "voice",
			ALARM_REPEAT = "repeat",
			ALARM_DAY_OF_WEEK = "dayofweek",
			ALARM_HOUR = "hour",
			ALARM_MINUTE = "minute";

// -- Helper class -----------------------------------------------------------
	
	private static class DataBaseAsistant extends SQLiteOpenHelper
	{
		
		private static final String
		
		// Alarms
			createAlarmsTable =
				CREATE + TABLE + AlarmsTable +
				OPEN +
					ALARM_ID + INTEGERPKAI + // Id of the alarm (set on its pending intent)
					ALARM_DAY_OF_WEEK + INTEGER + SEP + // Day of the week
					ALARM_HOUR + INTEGER + SEP + // Hour, 24h format
					ALARM_MINUTE + INTEGER + SEP + // Min [0, 59]
					ALARM_REPEAT + INTEGER + // Type of the alarm as defined in the Alarm class
				CLOSE + SEPSTMT;
		

		public DataBaseAsistant(Context c, String nombreBD, CursorFactory cf, int ver)
		{
			super(c, nombreBD, cf, ver);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(createAlarmsTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
		{
			db.execSQL(DROP + AlarmsTable);
			onCreate(db);
		}
		
	} // DataBaseAsistant


// -- DataBase explotation ---------------------------------------------------

	public DataBase(Context c)
	{
		dba = new DataBaseAsistant(c, NameDB, null, VersionDB);
	}

	public boolean open()
	{
		try
		{
			db = dba.getWritableDatabase();
			return true;
		}
		catch (SQLiteException e)
		{
			db = dba.getReadableDatabase();
			return false;
		}
	}

	public void close()
	{
		db.close();
		dba.close();
	}
	
// Alarms
	public int addAlarm(int dow, int hour, int min, int repeat)
	{
		ContentValues cv = new ContentValues();
		cv.put(ALARM_DAY_OF_WEEK, dow);
		cv.put(ALARM_HOUR, hour);
		cv.put(ALARM_MINUTE, min);
		cv.put(ALARM_REPEAT, repeat);
		int res = (int)db.insert(AlarmsTable, null, cv);
		return res;
	}
	
	public void deleteAlarm(int id)
	{
		String where = ALARM_ID + EQ + id + ENDEQ;
		db.delete(AlarmsTable, where, null);
	}
	
	public Cursor getAlarm(int id)
	{
		String where = ALARM_ID + EQ + id + ENDEQ;
		Cursor cursor = db.query(true, AlarmsTable, null, where,
			null, null, null, null, null);
		return cursor;
	}
	
	public Cursor getAllAlarms()
	{
		Cursor cursor = db.query(true, AlarmsTable, null, null,
				null, null, null, null, null);
		return cursor;
	}

} // DataBase
