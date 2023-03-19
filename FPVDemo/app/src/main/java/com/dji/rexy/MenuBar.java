package com.dji.rexy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuBar extends Activity implements View.OnClickListener {

    private Button controller_button, tapFly_button, instructions_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        init_ui();
        init_listeners();
    }

    private void init_ui(){
        this.controller_button = findViewById(R.id.voice_commands_button);
        this.tapFly_button = findViewById(R.id.FlyTap_button);
        this.instructions_button = findViewById(R.id.intro_button);
    }

    private void init_listeners(){
        this.controller_button.setOnClickListener(this);
        this.tapFly_button.setOnClickListener(this);
        this.instructions_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.voice_commands_button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
