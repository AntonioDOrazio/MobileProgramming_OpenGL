package it.tiburtinavalley.mpopengl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private Button btnExit;
    private ImageView ivIcon;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_page);
        btnStart = findViewById(R.id.btnStart);
        btnExit = findViewById(R.id.btnExit);
        ivIcon = findViewById(R.id.ivIcon);
        btnStart.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnStart){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        else if(v.getId() == R.id.btnExit){
            System.exit(0);
        }
    }
}
