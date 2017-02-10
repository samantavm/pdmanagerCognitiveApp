package es.upm.tfo.lst.pdmanagertest.tools;

import android.os.Bundle;

import es.upm.tfo.lst.pdmanagertest.persistance.Preferences;

public class SoundFeedbackActivity extends LoggingTestActivity
{
    protected Speak speak;
    protected Tones tones;
    protected Preferences prefs;

    @Override
    protected  void onPause()
    {
        super.onPause();
        speak.silence();
        tones.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        speak = Speak.getInstance(getApplicationContext());
        tones = Tones.getInstance();
        prefs = new Preferences(getApplicationContext());
    }
}
