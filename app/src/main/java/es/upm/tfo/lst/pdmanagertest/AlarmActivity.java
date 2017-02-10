package es.upm.tfo.lst.pdmanagertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import es.upm.tfo.lst.pdmanagertest.alarms.Alarm;
import es.upm.tfo.lst.pdmanagertest.alarms.WakeUpDevice;
import es.upm.tfo.lst.pdmanagertest.cognitive.AttentionSwitchingTaskTest;
import es.upm.tfo.lst.pdmanagertest.cognitive.LondonTowersTest;
import es.upm.tfo.lst.pdmanagertest.cognitive.PALPRM;
import es.upm.tfo.lst.pdmanagertest.cognitive.SpatialSpanTest;
import es.upm.tfo.lst.pdmanagertest.cognitive.SpatialWorkingMemoryTest;
import es.upm.tfo.lst.pdmanagertest.cognitive.StopSignalTaskTest;
import es.upm.tfo.lst.pdmanagertest.cognitive.VisualAnalogueScaleTest;
import es.upm.tfo.lst.pdmanagertest.cognitive.WisconsinCardSorting;
import es.upm.tfo.lst.pdmanagertest.persistance.Preferences;
import es.upm.tfo.lst.pdmanagertest.tools.RNG;
import es.upm.tfo.lst.pdmanagertest.tools.SoundPlayer;
import es.upm.tfo.lst.pdmanagertest.tools.Vibr;

public class AlarmActivity extends Activity
{
    private Preferences prefs;
    private RNG rand;
    private SoundPlayer sp;
    private Vibr v;
    private ArrayList<Integer> testBattery;
    private TextView tvTitle;
    private int
        maxCount = 3,
        count = 0;

    private final Integer
        INIT_VAS = -1,
        END_VAS = -2,
        END_TESTS = -3;
    public static final Class[] tests =
    {
            PALPRM.class,
            //SpatialWorkingMemoryTest.class,
            SpatialSpanTest.class,
            StopSignalTaskTest.class,
            AttentionSwitchingTaskTest.class,
            WisconsinCardSorting.class,
            LondonTowersTest.class
    };

    private SoundPlayer.OnSoundPlayerEnded ospel = new SoundPlayer.OnSoundPlayerEnded()
    {
        @Override
        public void onSoundEnded()
        {
            if (count==maxCount) stopNoise();
            else
            {
                sp.play(getApplicationContext(), R.raw.alarm);
                v.vibrate(3*1000);
                count++;
            }
        }
    };

    private boolean
        toStart = true,
        toEnd = false;
    private Button bStart;
    private View.OnClickListener oclbStart = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            stopNoise();
            if (toEnd) finish();
            else
            {
                toStart = false;
                executeTests();
            }
        }
    };

    private void executeTests()
    {
        int nextTest = testBattery.get(0);
        testBattery.remove(0);
        prefs.setTests(testBattery);
        if (nextTest<0)
        {
            if (nextTest==END_TESTS)
            {
                tvTitle.setText(R.string.end_tests);
                bStart.setText(R.string.ok);
                toEnd = true;
            }
            else if (nextTest==INIT_VAS)
            {
                Intent intent = new Intent(getApplicationContext(), VisualAnalogueScaleTest.class);
                intent.putExtra(VisualAnalogueScaleTest.FLAG, true);
                startActivity(intent);
            }
            else if (nextTest==END_VAS)
            {
                Intent intent = new Intent(getApplicationContext(), VisualAnalogueScaleTest.class);
                intent.putExtra(VisualAnalogueScaleTest.FLAG, false);
                startActivity(intent);
            }
        }
        else
        {
            Class tolaunch = tests[nextTest];
            Intent intent = new Intent(getApplicationContext(), tolaunch);
            startActivity(intent);
        }
    }

    private void shuffleTests()
    {
        final int
            firstRunTests = 3,
            secondRunTests = 2;
        ArrayList<Integer> aux = new ArrayList<Integer>();
        for (Integer i=0; i<tests.length; i++) aux.add(i);

        testBattery = new ArrayList<Integer>();
        testBattery.add(INIT_VAS);
        for (int j=0; j<firstRunTests; j++)
        {
            int n = rand.getIntInClosedRange(0, aux.size()-1);
            testBattery.add(aux.get(n));
            aux.remove(n);
        }
        testBattery.add(END_VAS);
        testBattery.add(END_TESTS);
        testBattery.add(INIT_VAS);
        for (int k=0; k<secondRunTests; k++)
        {
            int n = rand.getIntInClosedRange(0, aux.size()-1);
            testBattery.add(aux.get(n));
            aux.remove(n);
        }
        testBattery.add(END_VAS);
        testBattery.add(END_TESTS);
        prefs.setTests(testBattery);
    }

    private void stopNoise() { sp.setOnSoundPlayerEnded(null); }
    private void doNoise()
    {
        WakeUpDevice.awake(getApplicationContext());
        sp.setOnSoundPlayerEnded(ospel);
        sp.play(getApplicationContext(), R.raw.beep);
        v.vibrate(1000);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        testBattery = prefs.getTests();
        if (testBattery.size()==0 && !toEnd) shuffleTests();
        if (!toStart) executeTests();
    }

    @Override
    public void onBackPressed()
    {
        testBattery = prefs.getTests();
        AlertDialog d = new AlertDialog.Builder(AlarmActivity.this)
            .setMessage(R.string.end_test_query)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (testBattery.size()>0)
                    {
                        boolean br = false;
                        int i=0;
                        while (testBattery.size()>0 && !br)
                        {
                            int n = testBattery.get(i);
                            if (n!=END_TESTS) testBattery.remove(i);
                            else br=true;
                        }
                        if (testBattery.size()==0) testBattery.add(END_TESTS);
                        prefs.setTests(testBattery);
                        executeTests();
                    }
                    dialog.dismiss();
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
            })
            .create();
        if (!toEnd) d.show();
        else finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmactivity);
        prefs = new Preferences(getApplicationContext());
        rand = new RNG();
        getWindow().setFlags
                (
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,

                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                );
        v = new Vibr(getApplicationContext());
        sp = new SoundPlayer();
        Bundle b = getIntent().getExtras();
        if (b!=null && b.getInt(Alarm.kID)<0) doNoise();
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        bStart = (Button)findViewById(R.id.bTests);
        bStart.setOnClickListener(oclbStart);
    }

}