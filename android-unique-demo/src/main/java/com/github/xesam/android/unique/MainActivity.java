package com.github.xesam.android.unique;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.xesam.android.unique.demo.R;

public class MainActivity extends AppCompatActivity {

    TextView vTv;
    Unique unique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vTv = (TextView) findViewById(R.id.content);
        unique = new Unique(this);
        findViewById(R.id.generate_unique_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.get_unique_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vTv.setText(unique.getUniqueId());
            }
        });
    }
}
