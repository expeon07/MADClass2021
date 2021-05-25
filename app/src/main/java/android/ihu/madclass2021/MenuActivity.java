package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    public static String menu_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView menu_greeting = findViewById(R.id.menu_greeting);
        menu_greeting.setText(menu_message);

        Button jukebox_button = findViewById(R.id.jukebox_btn);
        Log.d("onPostExecute", "Jukebox button");
        jukebox_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, JukeboxActivity.class));
            }
        });
    }
}