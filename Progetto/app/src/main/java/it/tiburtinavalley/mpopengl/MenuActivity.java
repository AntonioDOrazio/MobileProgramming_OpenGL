package it.tiburtinavalley.mpopengl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private Button btnExit;
    private ImageView ivIcon;

    @Override
    public void onCreate(Bundle savedInstanceState){
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_page);
        btnStart = findViewById(R.id.btnManual);
        btnExit = findViewById(R.id.btnAuto);
        btnStart.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnManual){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(getString(R.string.String_Auto_Camera), false);
            startActivity(i);
        }
        else if(v.getId() == R.id.btnAuto){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(getString(R.string.String_Auto_Camera), true);
            startActivity(i);
        }
    }
}
