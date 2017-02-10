package es.upm.tfo.lst.pdmanagertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import es.upm.tfo.lst.pdmanagertest.persistance.Preferences;

public class SettingsActivity extends Activity
{
    private View bNext;
    private Preferences prefs;
    private EditText etUsername;
    private String username;

    private View.OnClickListener oclButton = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            username = etUsername.getText().toString();
            prefs.setUsername(username);
            Intent i = new Intent(getApplicationContext(), Settings1Activity.class);
            startActivity(i);
            finish();
            overridePendingTransition(0, 0);
        }
    };

    private TextWatcher tw = new TextWatcher()
    {
        @Override public void afterTextChanged(Editable s) {}
        @Override  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (s.length()==0) bNext.setVisibility(View.INVISIBLE);
            else if (bNext.getVisibility()!=View.VISIBLE) bNext.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings0);
        prefs = new Preferences(getApplicationContext());
        username = prefs.getUsername();
        bNext = findViewById(R.id.bNext);
        bNext.setOnClickListener(oclButton);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etUsername.addTextChangedListener(tw);
        if (username!=null) etUsername.setText(username);
    }
}
