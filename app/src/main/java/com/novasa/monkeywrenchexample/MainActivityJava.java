package com.novasa.monkeywrenchexample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.novasa.monkeywrench.MonkeyWrench;
import com.novasa.monkeywrench.finder.Finders;
import com.novasa.monkeywrench.schematic.Bits;
import com.novasa.monkeywrench.schematic.Mutaters;
import com.novasa.monkeywrench.schematic.Schematics;

import kotlin.Unit;

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

        btnSpan.setOnClickListener(v ->
                MonkeyWrench.create()
                        .addSchematic(Schematics.createClickable()
                                .onClick(uri -> {
                                    Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
                                    return Unit.INSTANCE;
                                })
                                .addFinder(Finders.createHtmlALink())
                                .addBit(Bits.createTextColor(Color.BLUE)))
                        .addSchematic(Schematics.create()
                                .addFinder(Finders.createRegex("<.*?>", 0)) // Remove any remaining html tags
                                .addMutater(Mutaters.createDelete()))
                        .addSchematic(Schematics.create()
                                .addFinder(Finders.createHtmlBold())
                                .addFinder(Finders.createHtmlStrong())
                                .addBit(Bits.createFakeBold()))
                        .debug()
                        .workOn(tv));
//
//        btnSpan.setOnClickListener(v -> MonkeyWrench.create()
//                .addSchematic(Schematics.create()
//                        .addMutater(Mutaters.createDelete())
//                        .addFinder(Finders.createHtmlBold())
//                        .addMutater(Mutaters.createUpperCase())
//                        .addBit(Bits.createStrikeThrough()))
//                .workOn(tv));

        btnReset.setOnClickListener(v -> tv.setText(text));

    }
}
