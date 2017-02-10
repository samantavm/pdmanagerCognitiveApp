package es.upm.tfo.lst.pdmanagertest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import es.upm.tfo.lst.pdmanagertest.persistance.Preferences;
import es.upm.tfo.lst.pdmanagertest.views.ButtonRepeatListener;

public class Settings1Activity extends Activity
{
    private View bH0up, bH0down, bH1up, bH1down, bNext;
    private TextView tvH0, tvH1;
    private Typeface digital7;
    private Preferences prefs;
    private int h0, h1;

    private final int
        h0min = 6,
        h0max = 12,
        h1min = 13,
        h1max = 23;

    private View.OnClickListener
        oclH0 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v==bH0up) h0++;
                else if (v==bH0down) h0--;
                h0 = setInRange(h0min, h0max, h0);
                setH0();
            }
        },

        oclH1 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v==bH1up) h1++;
                else if (v==bH1down) h1--;
                h1 = setInRange(h1min, h1max, h1);
                setH1();
            }
        },

        oclbNext = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prefs.setAlarmHour0(h0);
                prefs.setAlarmHour1(h1);
                Intent i = new Intent(getApplicationContext(), Settings2Activity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            }
        };

    private View.OnTouchListener
        otlH0 = new ButtonRepeatListener(oclH0),
        otlH1 = new ButtonRepeatListener(oclH1);

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }

    private void loadHours()
    {
        h0 = prefs.getAlarmHour0();
        h1 = prefs.getAlarmHour1();
        if (h0<0 || h1<0)
        {
            h0 = 10;
            h1 = 17;
        }
        setH0();
        setH1();
    }

    private int setInRange(int min, int max, int n)
    {
        int mid = max-min;
        if (n<min) n = setInRange(min, max, n+mid+1);
        else if (n>max) n = setInRange(min, max, n-mid-1);
        return n;
    }

    private void setH0()
    {
        String sh0 = "";
        if (h0<10) sh0 = "0";
        sh0 += h0+":00";
        tvH0.setText(sh0);
    }

    private void setH1()
    {
        String sh1 = "";
        if (h1<10) sh1 = "0";
        sh1 += h1+":00";
        tvH1.setText(sh1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings1);
        prefs = new Preferences(getApplicationContext());
        digital7 = Typeface.createFromAsset(getAssets(), "digital7.ttf");
        tvH0 = (TextView)findViewById(R.id.tvHour0);
        tvH0.setTypeface(digital7);
        tvH1 = (TextView)findViewById(R.id.tvHour1);
        tvH1.setTypeface(digital7);
        loadHours();

        bNext = findViewById(R.id.bNext);
        bNext.setOnClickListener(oclbNext);

        bH0up = findViewById(R.id.bhup0);
        bH0up.setOnTouchListener(otlH0);
        bH0down = findViewById(R.id.bhdown0);
        bH0down.setOnTouchListener(otlH0);
        bH1up = findViewById(R.id.bhup1);
        bH1up.setOnTouchListener(otlH1);
        bH1down = findViewById(R.id.bhdown1);
        bH1down.setOnTouchListener(otlH1);
    }
}
