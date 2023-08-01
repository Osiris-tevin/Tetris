package com.tetris.android.logic.util

import android.media.AudioManager
import android.media.SoundPool
import com.tetris.android.R
import com.tetris.android.TetrisApplication

object SoundUtil {

    private val Sounds = listOf(
        SoundType.Move,
        SoundType.Rotate,
        SoundType.Start,
        SoundType.Drop,
        SoundType.Clean
    )
    private val soundPool: SoundPool by lazy {
        SoundPool.Builder().setMaxStreams(4).setMaxStreams(AudioManager.STREAM_MUSIC).build()
    }
    private val map = mutableMapOf<SoundType, Int>()

    /**
     * 初始化SoundUtil, 加载音效资源
     */
    fun init() {
        Sounds.forEach {
            map[it] = soundPool.load(TetrisApplication.context, it.res, 1)
        }
    }

    /**
     * 释放SoundUtil的资源
     */
    fun release() {
        soundPool.release()
    }

    /**
     * 播放音效
     */
    fun play(isMute: Boolean, sound: SoundType) {
        if (!isMute) {
            soundPool.play(requireNotNull(map[sound]), 1f, 1f, 0, 0, 1f)
        }
    }

}

sealed class SoundType(val res: Int) {
    object Move: SoundType(R.raw.move)
    object Rotate: SoundType(R.raw.rotate)
    object Start: SoundType(R.raw.start)
    object Drop: SoundType(R.raw.drop)
    object Clean: SoundType(R.raw.clean)
}