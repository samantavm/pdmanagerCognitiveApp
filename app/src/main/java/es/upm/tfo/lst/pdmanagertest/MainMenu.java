package es.upm.tfo.lst.pdmanagertest;

/**
 * Main menu, starting point of the application.
 *
 * It shows two different buttons to launch the cognitive test or the finger tapping tests
 *
 * Main menu to launch the different cognitive tests
 *
 * @authors Quentin DELEPIERRE, Thibaud PACQUETET, Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.upm.tfo.lst.pdmanagertest.cognitive.CognitiveMenu;
import es.upm.tfo.lst.pdmanagertest.fingertapping.FingerTappingTestOne;
import es.upm.tfo.lst.pdmanagertest.fingertapping.FingerTappingTestTwo;
import es.upm.tfo.lst.pdmanagertest.speech.SpeechTest;
import es.upm.tfo.lst.pdmanagertest.tools.SoundFeedbackActivity;

public class MainMenu extends SoundFeedbackActivity implements View.OnClickListener {

    Button buttonCognitive, buttonFTT, buttonVoice;

    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        speak.shutdown();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        buttonCognitive= (Button) findViewById(R.id.buttonMenuCognitive);
        buttonFTT = (Button) findViewById(R.id.buttonMenuFTT);
        buttonVoice = (Button) findViewById(R.id.buttonMenuVoice);

        buttonCognitive.setOnClickListener(this);
        buttonFTT.setOnClickListener(this);
        buttonVoice.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonMenuFTT:
                Intent menuFingerTappingIntent = new Intent(MainMenu.this, FingerTappingTestOne.class);
                startActivity(menuFingerTappingIntent);
                break;

            case R.id.buttonMenuCognitive:
                Intent menuCognitiveIntent = new Intent(MainMenu.this, CognitiveMenu.class);
                startActivity(menuCognitiveIntent);
                break;

            case R.id.buttonMenuVoice:
                Intent menuVoice = new Intent(MainMenu.this, SpeechTest.class);
                startActivity(menuVoice);
                break;
        }
    }



}


