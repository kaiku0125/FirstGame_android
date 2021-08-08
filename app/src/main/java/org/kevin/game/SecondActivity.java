package org.kevin.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SecondActivity extends Activity {
    LabyrinthView labyrinthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        labyrinthView = findViewById(R.id.view_labyrinth);
        
    }
}