package com.szysky.note.storeortest.audio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.TextView;

import com.szysky.note.storeortest.R;

import java.util.ArrayList;

/**
 * @author Erik Hellman
 */
public class SpeechRecognitionDemo extends Activity {

    private SpeechRecognizer mSpeechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_recognition_demo);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.
                setRecognitionListener(new MyRecognitionListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
    }

    public void doSpeechRecognition(View view) {
        view.setEnabled(false);

        Intent recognitionIntent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "en-US");

        mSpeechRecognizer.startListening(recognitionIntent);
    }

    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
            ((TextView) findViewById(R.id.speech_result)).setText("");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // Not used
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            findViewById(R.id.do_speech_recognition_btn).setEnabled(true);
        }

        @Override
        public void onError(int i) {
            // Something went wrong...
            findViewById(R.id.do_speech_recognition_btn).setEnabled(true);
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> partialResults =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (partialResults != null && partialResults.size() > 0) {
                String bestResult = partialResults.get(0);
                ((TextView) findViewById(R.id.speech_result)).
                        setText(bestResult + ".");
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            ArrayList<String> partialResults =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (partialResults != null && partialResults.size() > 0) {
                String bestResult = partialResults.get(0);
                ((TextView) findViewById(R.id.speech_result)).
                        setText(bestResult);
            }
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            // Not used...
        }
    }
}
