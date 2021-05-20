package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button jukebox_button = findViewById(R.id.jukebox_btn);
        jukebox_button.setOnClickListener(v -> {
            setContentView(R.layout.activity_jukebox);
        });
    }
}