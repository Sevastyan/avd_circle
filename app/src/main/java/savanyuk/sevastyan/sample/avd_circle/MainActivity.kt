package savanyuk.sevastyan.sample.avd_circle

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var circle: Animatable
    private lateinit var indicator: TextView
    private val progress = PublishSubject.create<Float>()
    private val stopTracker = PublishSubject.create<SeekBar>()
    private val logError: (Throwable) -> Unit = {
        Log.e(MainActivity::class.java.simpleName, "Error retrieving progress", it)
    }
    private val toPercentage: (Float) -> String = {
        NumberFormat.getPercentInstance().format(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        circle = (circle_drawable as ImageView).drawable as Animatable
        indicator = progress_value_indicator

        seekBar.setOnSeekBarChangeListener(SeekBarListener(progress, stopTracker))

        progress
                .subscribeOn(Schedulers.computation())
                .map(toPercentage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateProgressValueIndicator,
                        logError)

        val onSeekingStopped = stopTracker.publish().autoConnect(2)

        onSeekingStopped
                .subscribeOn(Schedulers.computation())
                .map {
                    "Stopped at: ${it.progress}"
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            raiseSnackBar(window.decorView, it)
                        },
                        logError
                )

        onSeekingStopped
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::animateCircle,
                        logError
                )

    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        seekBar.progress = 0
    }

    private fun updateProgressValueIndicator(text: String) {
        indicator.text = text
    }

    private fun raiseSnackBar(view: View, message: String) {
        Snackbar.make(view, message, 2000).show()
    }

    private fun animateCircle(seekBar: SeekBar) {
        circle.start()
    }
}
