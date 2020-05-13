package it.tiburtinavalley.mpopengl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private Button btnExit;
    private ImageView ivIcon;       // nulla associato

    @Override
    public void onCreate(Bundle savedInstanceState){
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_page);
        btnStart = findViewById(R.id.btn_manual);
        btnExit = findViewById(R.id.btn_auto);
        btnStart.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_manual){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(getString(R.string.string_auto_camera), false);
            startActivity(i);
        }
        else if(v.getId() == R.id.btn_auto){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(getString(R.string.string_auto_camera), true);
            startActivity(i);
        }
    }
}
