package es.upm.tfo.lst.pdmanagertest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import es.upm.tfo.lst.pdmanagertest.alarms.Alarm;
import es.upm.tfo.lst.pdmanagertest.persistance.AlarmsDBInterface;
import es.upm.tfo.lst.pdmanagertest.persistance.Preferences;
import es.upm.tfo.lst.pdmanagertest.views.ButtonRepeatListener;

public class Settings2Activity extends Activity
{
    private ViewGroup llDow;
    private TextView tvTitle;
    private Button bEnd;
    private View bMon, bTue, bWed, bThu, bFri, bSat, bSun;
    private View[] views;
    private Preferences prefs;
    private AlarmsDBInterface adbi;
    private boolean
        setconfig = false, // Show ok message befor exiting activity
        changed = false; // No interactions means no changes on the Alarm database.

    private View.OnClickListener
        oclbDay = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v.isSelected()) v.setSelected(false);
                else v.setSelected(true);
                changed = true;
            }
        },

        oclbEnd = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (changed)
                {
                    saveDowPreferences();
                    deletAllAlarms();
                    setNewAlarms();
                }
                if (!setconfig) updateOkStatus();
                else finish();
            }
        };

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), Settings1Activity.class);
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }

    private void updateOkStatus()
    {
        setconfig = true;
        llDow.setVisibility(View.INVISIBLE);
        bEnd.setText(R.string.ok);
        tvTitle.setText(R.string.config_ok);
    }

    private void setNewAlarms()
    {
        int
            h0 = prefs.getAlarmHour0(),
            h1 = prefs.getAlarmHour1();
        for (int i=0; i<views.length; i++)
        {
            View v = views[i];
            if (v.isSelected())
            {
                Alarm
                    a0 = new Alarm(i, h0, 0, Alarm.REPEAT_WEEKLY),
                    a1 = new Alarm(i, h1, 0, Alarm.REPEAT_WEEKLY);
                int
                    ida0 = adbi.addAlarm(a0),
                    ida1 = adbi.addAlarm(a1);
                a0.activate(getApplicationContext(), ida0);
                a1.activate(getApplicationContext(), ida1);
            }
        }
    }

    private void deletAllAlarms()
    {
        ArrayList<Alarm> alarms = adbi.getAllAlarms();
        for (Alarm a:alarms)
        {
            adbi.deleteAlarm(a.id);
            a.deactivate(getApplicationContext());
        }
    }

    private void saveDowPreferences()
    {
        boolean
                bmon = bMon.isSelected(),
                btue = bTue.isSelected(),
                bwed = bWed.isSelected(),
                bthu = bThu.isSelected(),
                bfri = bFri.isSelected(),
                bsat = bSat.isSelected(),
                bsun = bSun.isSelected();
        prefs.saveDow(bmon, btue, bwed, bthu, bfri, bsat, bsun);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings2);

        prefs = new Preferences(getApplicationContext());
        adbi = new AlarmsDBInterface(getApplicationContext());
        llDow = (ViewGroup)findViewById(R.id.lDow);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        bEnd = (Button)findViewById(R.id.bNext);
        bEnd.setOnClickListener(oclbEnd);
        bMon = findViewById(R.id.bMon);
        bMon.setOnClickListener(oclbDay);
        bTue = findViewById(R.id.bTue);
        bTue.setOnClickListener(oclbDay);
        bWed = findViewById(R.id.bWed);
        bWed.setOnClickListener(oclbDay);
        bThu = findViewById(R.id.bThu);
        bThu.setOnClickListener(oclbDay);
        bFri = findViewById(R.id.bFri);
        bFri.setOnClickListener(oclbDay);
        bSat = findViewById(R.id.bSat);
        bSat.setOnClickListener(oclbDay);
        bSun = findViewById(R.id.bSun);
        bSun.setOnClickListener(oclbDay);
        views = new View[] { bMon, bTue, bWed, bThu, bFri, bSat, bSun };
        loadPrefs();
    }

    private void loadPrefs()
    {
        ArrayList<Boolean> dows = prefs.getDoW();
        for (int i=0; i<dows.size(); i++) views[i].setSelected(dows.get(i));
    }
}
