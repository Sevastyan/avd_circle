package savanyuk.sevastyan.sample.avd_circle

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var circle: AnimatedVectorDrawable
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

        circle = (circle_drawable as ImageView).drawable as AnimatedVectorDrawable
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
        createNewAVD()
    }

    private fun createNewAVD(): AnimatedVectorDrawable {

    }

    private fun AnimatedVectorDrawable.create(drawableResId: Int, target: String, valueTo: Float): AnimatedVectorDrawable {
        val avd = AnimatedVectorDrawable()

        // Get hold of VectorDrawable.
        val vectorDrawable: VectorDrawable = resources.getDrawable(drawableResId, theme).mutate() as VectorDrawable
        vectorDrawable.callback = avd
                .javaClass
                .getDeclaredField("mCallback")
                .apply {
                    isAccessible = true
                }
                .get(avd) as Drawable.Callback

        val avdState = avd
                .javaClass
                .getDeclaredField("mAnimatedVectorState")
                .apply {
                    isAccessible = true
                }
                .get(avd)

        // Set drawable to inner state.
        avdState
                .javaClass
                .getDeclaredField("mVectorDrawable")
                .apply {
                    isAccessible = true
                }
                .set(avdState, vectorDrawable)

        // Get hold of the Animator (depending on the valueTo).
        AnimatorInflater.loadAnimator(this@MainActivity, animResId)

//        avdState.addTargetAnimator
    }


    private fun AnimatedVectorDrawable.updateAnimatorTrimValue(newValue: Int) {
        val avdState = javaClass
                .getDeclaredField("mAnimatedVectorState")
                .apply {
                    isAccessible = true
                }
                .get(this)

        // Update the Animator.
        @Suppress("UNCHECKED_CAST")
        val animator: ObjectAnimator = (avdState
                .javaClass
                .getDeclaredField("mAnimators")
                .apply {
                    isAccessible = true
                }
                .get(avdState) as ArrayList<Animator>)[0] as ObjectAnimator

        animator.setFloatValues(newValue.toFloat())

        Log.e("TEST", animator.toString())

        // Update the animator in the map.
        val updatedMap: ArrayMap<Animator, String> = ArrayMap<Animator, String>(1).apply { put(animator, "progress") }

        avdState
                .javaClass
                .getDeclaredField("mTargetNameMap")
                .apply {
                    isAccessible = true
                }
                .set(avdState, updatedMap)

        javaClass
                .getDeclaredField("mAnimatorSetFromXml")
                .apply {
                    isAccessible = true
                }
                .set(this, null)
    }
}
