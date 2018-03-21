package savanyuk.sevastyan.sample.avd_circle

import android.widget.SeekBar
import io.reactivex.subjects.Subject


class SeekBarListener(private val progressReporter: Subject<Float>,
                      private val stopTracker: Subject<SeekBar>) : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progressValue: Int, fromUser: Boolean) {
        progressReporter.onNext(progressValue.toFloat() / seekBar.max.toFloat())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // Do nothing.
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        stopTracker.onNext(seekBar)
    }
}