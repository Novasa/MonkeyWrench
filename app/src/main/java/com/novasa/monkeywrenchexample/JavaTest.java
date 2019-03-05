package com.novasa.monkeywrenchexample;

import android.graphics.Color;
import com.novasa.monkeywrench.MonkeyWrench;

public class JavaTest {

    void test(String input) {

        MonkeyWrench.span(input)
                .addSchematic(MonkeyWrench.htmlBold()
                        .color(Color.BLACK)
                        .fakeBold())
                .doTheThing();
    }
}
