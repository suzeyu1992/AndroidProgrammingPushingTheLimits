package com.szysky.note.storeortest.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Author :  suzeyu
 * Time   :  2016-10-03  下午8:40
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 利用AudioRecorder 来录制16位单声道16kHz的音频采样
 */

public class AudioRecorderDemo {

    private final int mMinBufferSize;
    private final AudioRecord mAudioRecord;
    private boolean mDoRecord = false;

    public AudioRecorderDemo(){

        // 确定音频流的最小缓冲区和大小
        mMinBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mMinBufferSize * 2);
    }

    public void writeAudioToStream(OutputStream stream){
        mDoRecord = true;
        mAudioRecord.startRecording();

        byte[] buffer = new byte[mMinBufferSize * 2];
        while(mDoRecord){
            int bytesWritten = mAudioRecord.read(buffer, 0, buffer.length);
            try {
                stream.write(buffer, 0, bytesWritten);
            } catch (IOException e) {
                // 简单处理,
                mDoRecord = false;
            }
        }
        mAudioRecord.stop();
        mAudioRecord.release();
    }

    public void stopRecording(){
        mDoRecord = false;
    }
}
