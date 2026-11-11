package com.example.a0100_toefleptapp.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class ToeflAudioPlayer(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _speechRate = MutableStateFlow(0.95f) // Natural exam pacing
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to UK or default
                    tts?.language = Locale.UK
                }
                tts?.setSpeechRate(_speechRate.value)
                tts?.setPitch(1.0f)
                _isTtsReady.value = true

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlaying.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isPlaying.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isPlaying.value = false
                    }
                })
            } else {
                Log.e("ToeflAudioPlayer", "Gagal inisialisasi TextToSpeech Android.")
            }
        }
    }

    fun playAudio(audioScript: String?, audioUrl: String? = null) {
        stop()

        // 1. If an actual audio URL is available, play via MediaPlayer
        if (!audioUrl.isNullOrBlank()) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(audioUrl)
                    setOnPreparedListener {
                        start()
                        _isPlaying.value = true
                    }
                    setOnCompletionListener {
                        _isPlaying.value = false
                        release()
                        mediaPlayer = null
                    }
                    setOnErrorListener { _, _, _ ->
                        _isPlaying.value = false
                        // Fallback to TTS if stream fails
                        if (!audioScript.isNullOrBlank()) {
                            playTts(audioScript)
                        }
                        true
                    }
                    prepareAsync()
                }
                return
            } catch (e: Exception) {
                Log.e("ToeflAudioPlayer", "MediaPlayer error: ${e.message}, falling back to TTS")
            }
        }

        // 2. Play via TextToSpeech (TTS)
        if (!audioScript.isNullOrBlank()) {
            playTts(audioScript)
        }
    }

    private fun playTts(text: String) {
        val ttsInstance = tts
        if (ttsInstance == null || !_isTtsReady.value) {
            // Re-init and retry
            initTts()
            return
        }

        // Clean narration tags like "Woman:", "Man:", "Narrator:" for smoother listening
        val cleanedText = text
            .replace(Regex("(?i)\\b(Woman|Man|Male Speaker|Female Speaker|Student|Professor|Radio Host|Mark|Narrator):\\s*"), "")
            .trim()

        _isPlaying.value = true
        ttsInstance.setSpeechRate(_speechRate.value)
        ttsInstance.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, "toefl_listening_utterance")
    }

    fun toggleSpeed() {
        val nextRate = when (_speechRate.value) {
            0.8f -> 1.0f
            1.0f -> 1.2f
            else -> 0.8f
        }
        _speechRate.value = nextRate
        tts?.setSpeechRate(nextRate)
    }

    fun stop() {
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("ToeflAudioPlayer", "Error stopping audio: ${e.message}")
        } finally {
            _isPlaying.value = false
        }
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
