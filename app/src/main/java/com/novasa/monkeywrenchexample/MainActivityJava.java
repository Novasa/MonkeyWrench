package com.novasa.monkeywrenchexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.novasa.monkeywrench.MonkeyWrench;
import com.novasa.monkeywrench.schematic.Bits;
import com.novasa.monkeywrench.schematic.DeleteMutater;
import com.novasa.monkeywrench.schematic.Schematics;

public class MainActivityJava extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView tv = findViewById(R.id.textView);
        final Button btnSpan = findViewById(R.id.buttonSpan);
        final Button btnReset = findViewById(R.id.buttonReset);
        final String input = getString(R.string.input);

        tv.setText(input);

        btnSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonkeyWrench.create()
                        .addSchematic(Schematics.htmlBold()
                                .addMutater(new DeleteMutater())
                                .addBit(Bits.strikeThrough()))
                        .workOn(tv);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(input);
            }
        });
    }
}
