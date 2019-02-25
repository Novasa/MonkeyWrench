package com.novasa.monkeywrenchexample

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
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

        val onClick: (Uri) -> Unit = {
            Toast.makeText(this@MainActivity, "$it", Toast.LENGTH_SHORT).show()
        }

        buttonSpan.setOnClickListener {
            MonkeyWrench.span(input, textView) {

                htmlBold {
                    typeFace(Typeface.DEFAULT_BOLD)
                }

                htmlLink(onClick) {
                    color(Color.RED)
                    scale(1.2f)
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
