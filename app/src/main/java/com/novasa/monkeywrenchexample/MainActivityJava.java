package com.novasa.monkeywrenchexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.novasa.monkeywrench.MonkeyWrench;

public class MainActivityJava extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView tv = findViewById(R.id.textView);
        final Button btn = findViewById(R.id.buttonSpan);
        final String input = getString(R.string.input);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonkeyWrench.build()
                        .addSchematic(MonkeyWrench.allOfIt()
                                .strikethrough())
                        .doTheThingUnto(input, tv);
            }
        });
    }
}
