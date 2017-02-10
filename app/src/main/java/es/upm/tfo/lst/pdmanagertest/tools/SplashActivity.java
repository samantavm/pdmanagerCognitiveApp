package es.upm.tfo.lst.pdmanagertest.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import es.upm.tfo.lst.pdmanagertest.R;
import es.upm.tfo.lst.pdmanagertest.MainMenu;

/**
 * Created by isen on 14/05/2015.
 */
public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                     sleep(500) ; //Delay of 3

                } catch (Exception e) {

                } finally {
                    Intent i = new Intent(SplashActivity.this, MainMenu.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

}
