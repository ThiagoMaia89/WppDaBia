package com.simplesoftware.wppdabia.domain.utils

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import java.io.File

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun startRecording(context: Context) {

        mediaRecorder?.release()
        mediaRecorder = null

        audioFile = File(context.cacheDir, "audio_${System.currentTimeMillis()}.mp3")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording(): Uri? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        return audioFile?.let { Uri.fromFile(it) }
    }
}