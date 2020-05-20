package it.tiburtinavalley.mpopengl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        Objects.requireNonNull(getSupportActionBar()).hide();

        super.onCreate(savedInstanceState);

        //init
        ContextUtil.getInstance().init(getApplicationContext());
        //use via ApplicationContext.get()
        assert (getApplicationContext() == ContextUtil.get());

        if ( getIntent().getStringExtra(getString(R.string.triangle)) != null) {
            glView = new MyGLSurfaceViewTriangle(this);
        }
        else {
            boolean isAutoCamera = getIntent().getBooleanExtra(getResources().getString(R.string.string_auto_camera), true);
            glView = new MyGLSurfaceView(this, isAutoCamera);
            glView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        setContentView(glView);
    }
}
