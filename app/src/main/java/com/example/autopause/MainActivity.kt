package com.example.autopause

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
//import android.support.v4.app.ActivityCompat
//import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import java.io.IOException
import android.media.AudioRecord
import android.os.Handler
import androidx.appcompat.widget.AppCompatTextView
import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import android.os.SystemClock





//import android.widget.ProgressBar
//class MainActivity : AppCompatActivity() {
//
//    private var progressBar: ProgressBar? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        // finding progressbar by its id
//        progressBar = findViewById<ProgressBar>(R.id.progressBarTest) as ProgressBar
//        progressBar!!.progress = 10
//        Thread(Runnable {
//            // this loop will run until the value of i becomes 99
//            while (progressBar!!.progress < 100) {
//                // Update the progress bar and display the current value
//                progressBar!!.progress += 5
//                try {
//                    Thread.sleep(100)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//            // reset to 5 in the end
//            progressBar!!.progress = 5
//        }).start()
//    }
//}

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class AudioRecordTest : AppCompatActivity() {

    private var ar: AudioRecord? = null
    private var minSize = 0
    private var mAudioManager: AudioManager? = null

    private var fileName: String = ""

    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null
    private var textVolume: AppCompatTextView? = null

    private var playButton: PlayButton? = null
    private var player: MediaPlayer? = null

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        //for play/pause toggle
        //for play/pause toggle
//        val i = Intent("com.android.music.musicservicecommand")
//        i.putExtra("command", "togglepause")
//        this.sendBroadcast(i)
        if (mAudioManager == null) mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val eventDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
        mAudioManager?.dispatchMediaKeyEvent(eventDown)
        val eventUp = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY)
        mAudioManager?.dispatchMediaKeyEvent(eventUp)
//        player = MediaPlayer().apply {
//            try {
//                setDataSource(fileName)
//                prepare()
//                start()
//            } catch (e: IOException) {
//                Log.e(LOG_TAG, "prepare() failed")
//            }
//        }
    }

    private fun stopPlaying() {
//        val i = Intent("com.android.music.musicservicecommand")
//        i.putExtra("command", "togglepause")
//        sendBroadcast(i)
//        player?.release()
//        player = null
        if (mAudioManager == null) mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val eventDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE)
        mAudioManager?.dispatchMediaKeyEvent(eventDown)
        val eventUp = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE)
        mAudioManager?.dispatchMediaKeyEvent(eventUp)
    }

    fun getAmplitude(): Double {
        val bufferSize = 8000 * 5
        if (ar != null) {
            val buffer = ShortArray(bufferSize)
            ar!!.read(buffer, 0, bufferSize, AudioRecord.READ_NON_BLOCKING)
            var max = 0
            var sum = 0
            for (s in buffer) {
                if (Math.abs(s.toInt()) > max) {
                    max = Math.abs(s.toInt())
                }
                sum += Math.abs(s.toInt())
            }
            println(max.toDouble())
            if (sum.toDouble() / bufferSize > 50) {
                stopPlaying()
                Handler().postDelayed(this::getAmplitude, 2000)
            } else {
                startPlaying()
                Handler().postDelayed(this::getAmplitude, 1000)
            }
            textVolume?.apply {
//                text = max.toString()
                text = (sum.toDouble() / bufferSize).toString()
                return max.toDouble()
            }
        }
        return 0.0
    }

    private fun startRecording() {

        minSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//             TODO: Consider calling
//                ActivityCompat#requestPermissions
//             here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
//             for ActivityCompat#requestPermissions for more details.
            return
        }

        ar = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize
        )
        ar?.startRecording()
        getAmplitude()

//        recorder = MediaRecorder().apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//            setOutputFile(fileName)
//            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//
//            try {
//                prepare()
//            } catch (e: IOException) {
//                Log.e(LOG_TAG, "prepare() failed")
//            }
//
//            start()
//        }
    }

    private fun stopRecording() {
//        recorder?.apply {
//            stop()
//            release()
//        }
//        recorder = null
        if (ar != null) {
            ar!!.stop();
        }
        ar = null
    }

    internal inner class RecordButton(ctx: Context) : AppCompatButton(ctx) {

        var mStartRecording = true

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            text = when (mStartRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : AppCompatButton(ctx) {
        var mStartPlaying = true
        var clicker: OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = when (mStartPlaying) {
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Record to the external cache directory for visibility
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        recordButton = RecordButton(this)
        playButton = PlayButton(this)
        textVolume = AppCompatTextView(this)
        val ll = LinearLayout(this).apply {
            addView(
                recordButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f
                )
            )
            addView(
                playButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f
                )
            )
            addView(
                textVolume,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f
                )
            )
        }
        setContentView(ll)
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }
}