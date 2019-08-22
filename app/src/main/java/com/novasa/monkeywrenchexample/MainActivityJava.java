package com.novasa.monkeywrenchexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.novasa.monkeywrench.MonkeyWrench;
import com.novasa.monkeywrench.finder.Finders;
import com.novasa.monkeywrench.schematic.Bits;
import com.novasa.monkeywrench.schematic.Mutater;
import com.novasa.monkeywrench.schematic.Mutaters;
import com.novasa.monkeywrench.schematic.Schematics;

import org.jetbrains.annotations.NotNull;

public class MainActivityJava extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView tv = findViewById(R.id.textView);
        final Button btnSpan = findViewById(R.id.buttonSpan);
        final Button btnReset = findViewById(R.id.buttonReset);
        final String text = getString(R.string.input);

        tv.setText(text);

        btnSpan.setOnClickListener(v -> MonkeyWrench.create()
                .addSchematic(Schematics.create()
                        .addMutater(input -> null)
                        .addFinder(Finders.createHtmlBold())
                        .addMutater(Mutaters.createUpperCase())
                        .addBit(Bits.createStrikeThrough()))
                .workOn(tv));

        btnReset.setOnClickListener(v -> tv.setText(text));
    }
}
