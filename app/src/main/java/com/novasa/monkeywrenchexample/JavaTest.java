package com.novasa.monkeywrenchexample;

import com.novasa.monkeywrench.MonkeyWrench;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class JavaTest {

    void test(String input) {
        new MonkeyWrench(input)
                .htmlBold(new Function1<MonkeyWrench.Wrench, Unit>() {

                    @Override
                    public Unit invoke(MonkeyWrench.Wrench wrench) {
                        wrench.fakeBold();
                        return null;
                    }
                });
    }
}
