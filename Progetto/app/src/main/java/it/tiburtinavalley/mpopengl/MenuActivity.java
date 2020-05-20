package it.tiburtinavalley.mpopengl;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MenuActivity extends AppCompatActivity{

    Holder holder;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_page);

        context = getApplicationContext();
        holder = new Holder();

    }

    public class Holder implements View.OnClickListener{

        private Button btnManual;
        private Button btnAuto;
        private Button btnTriangle;

        Holder() {
            btnManual = findViewById(R.id.btn_manual);
            btnAuto = findViewById(R.id.btn_auto);
            btnTriangle = findViewById(R.id.btn_triangle);
            btnManual.setOnClickListener(this);
            btnTriangle.setOnClickListener(this);
            btnAuto.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_triangle) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(getString(R.string.triangle), "");
                startActivity(i);
            }
            else if (v.getId() == R.id.btn_manual) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(getString(R.string.string_auto_camera), false);
                startActivity(i);
            } else if (v.getId() == R.id.btn_auto) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(getString(R.string.string_auto_camera), true);
                startActivity(i);
            }
        }
    }
}
