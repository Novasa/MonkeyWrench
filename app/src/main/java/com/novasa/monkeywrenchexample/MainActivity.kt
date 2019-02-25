package com.novasa.monkeywrenchexample

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.novasa.monkeywrench.MonkeyWrench
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = getString(R.string.input)

        buttonSpan.setOnClickListener {
            MonkeyWrench.span(input, textView) {

                htmlBold {
                    fakeBold()
                }

                htmlLink {
                    color(Color.RED)
                    scale(1.5f)
                    underline()

                    onClick { uri ->
                        Toast.makeText(this@MainActivity, "$uri", Toast.LENGTH_SHORT).show()
                    }
                }

                tag("QQ", "WW") {
                    underline()
                }

                interval(2 to 4, 6 to 8) {
                    backgroundColor(Color.GREEN)
                }

                regex("amet") {
                    color(Color.BLACK)
                }
            }
        }

    }
}
