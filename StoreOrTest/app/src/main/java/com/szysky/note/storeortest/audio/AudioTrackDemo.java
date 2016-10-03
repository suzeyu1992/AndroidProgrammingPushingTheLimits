package com.szysky.note.storeortest.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Author :  suzeyu
 * Time   :  2016-10-03  下午7:47
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 利用 AudioTrack 类实现低延迟的 VoiIP(网络电话)功能
 */

public class AudioTrackDemo {

    private final AudioTrack mAudioTrack;
    private final int mMinBufferSize;

    public AudioTrackDemo() {

        // 确定音频流的最小缓冲区和大小
        mMinBufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mMinBufferSize * 2,
                AudioTrack.MODE_STREAM);
    }

    public void playPcmPacket(byte[] pcmData){
        if (mAudioTrack != null && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED){

            // 判断是否处在播放状态
            if (mAudioTrack.getPlaybackRate() != AudioTrack.PLAYSTATE_PLAYING){
                mAudioTrack.play();
            }

            mAudioTrack.write(pcmData, 0, pcmData.length);
        }
    }

    //  设置停止
    public void stopPlayback(){
        if (mAudioTrack != null){
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

}
