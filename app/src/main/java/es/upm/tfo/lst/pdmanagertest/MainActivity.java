package es.upm.tfo.lst.pdmanagertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
{
    private View bConfig, bTests;

    private View.OnClickListener oclButton = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent i;
            if (v==bConfig) i = new Intent(getApplicationContext(), SettingsActivity.class);
            else i = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(i);
            overridePendingTransition(0, 0);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        bConfig = findViewById(R.id.bConfig);
        bConfig.setOnClickListener(oclButton);
        bTests = findViewById(R.id.bTests);
        bTests.setOnClickListener(oclButton);
    }
}
