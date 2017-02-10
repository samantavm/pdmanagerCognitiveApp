package es.upm.tfo.lst.pdmanagertest.speech;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.mfcc.MFCC;
import es.upm.tfo.lst.pdmanagertest.R;
import es.upm.tfo.lst.pdmanagertest.persistance.Preferences;
import es.upm.tfo.lst.pdmanagertest.tools.SoundFeedbackActivity;
import es.upm.tfo.lst.pdmanagertest.tools.Statistics;

import static android.content.ContentValues.TAG;


/**
 *
 * Speech Sorting Test
 *
 * @authors Miguel Páramo (mparamo@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class SpeechTest extends SoundFeedbackActivity
{
    private Preferences prefs;
    private AudioRecorder ar;
    private Typeface digital7;
    private TextView tvCountdown, tvTitle;
    private int
        secsPerMs = 1000,
        coundownStart = 5,
        countdownRecording = 6;

    private String audioFilePath;

    final static String prefixDataFileName = "MFCC_COEFF_";
    final static String prefixMeanFilename = "MFCC_MEAN_";
    final static String prefixStdFilename = "MFCC_STD_";
    final static String prefixMinFilename = "MFCC_MIN_";
    final static String prefixMaxFilename = "MFCC_MAX_";
    int suffixFileName = 1;
    final String typeFile = ".csv";
    final static String CSV_PATH = "/AudioProcessing";

    private AudioDispatcher dispatcher;

    // Coefficients values and their statistical analysis.
    private ArrayList<float[]> mfccList;
    private ArrayList<float[]> mean_mfccs;
    private ArrayList<float[]> std_mfccs;
    private ArrayList<float[]> min_mfccs;
    private ArrayList<float[]> max_mfccs;

    //MFCC attributes Comprobar con Matlab
    //final int samplesPerFrame = 512; // Original Value
    //final int sampleRate = 16000; // Original Value
    final int samplesPerFrame = 40;
    final int sampleRate = 8000;
    final int amountOfCepstrumCoef = 21; //actually 20 but energy column would be discarded
    //int amountOfMelFilters = 30; // Original Value
    int amountOfMelFilters = 20;
    //float lowerFilterFreq = 133.3334f; // Original Value
    //float upperFilterFreq = ((float)sampleRate)/2f; // Original Value
    float lowerFilterFreq = 300; // Original Value: 133.3334f
    float upperFilterFreq = 3700;

    private void infoTest()
    {
        setContentView(R.layout.activity_start);
        TextView textViewToChange = (TextView) findViewById(R.id.level);
        String str = getResources().getString(R.string.speech_instruction);
        textViewToChange.setText(str);
        speak.speakFlush(str);
        Button buttonStart = (Button) findViewById(R.id.play);
        buttonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speak.silence();
                setContentView(R.layout.speechactivity);
                tvTitle = (TextView) findViewById(R.id.tvTitle);
                tvCountdown = (TextView) findViewById(R.id.tvCountdown);
                tvCountdown.setTypeface(digital7);
                startRecCountdown();
            }
        });
    }

    private void startRecCountdown()
    {
        CountDownTimer timerTask = new CountDownTimer(coundownStart*secsPerMs, secsPerMs)
        {
            @Override public void onTick(final long ms)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int c = (int)(ms/secsPerMs)-1;
                        if (c!=0)
                        {
                            tvCountdown.setText("" + c);
                            tones.makeTone(ToneGenerator.TONE_DTMF_1, 100);
                        }
                        else
                        {
                            tvCountdown.setText(R.string.start_speech);
                            tones.makeTone(ToneGenerator.TONE_DTMF_6, 500);
                        }
                    }
                });
            }
            @Override public void onFinish() { startRecording(); }
        };
        timerTask.start();
    }

    private void startRecording()
    {
        tvTitle.setText(R.string.speech_start);
        ar.startRecording();
        CountDownTimer timerTask = new CountDownTimer(countdownRecording*secsPerMs, secsPerMs)
        {
            @Override public void onTick(final long ms)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int c = (int)(ms/secsPerMs)-1;
                        tvCountdown.setText("" + c);
                    }
                });
            }

            @Override
            public void onFinish()
            {

                //String audioFilePath = ar.stopRecording();
                audioFilePath = ar.stopRecording();
                // Create of dispatcher (required by TARSOSDSP library)
                initDispatcher(audioFilePath);
                // Extraction of MFCC Coefficients
                startMfccExtraction();
                endTest();
            }
        };
        timerTask.start();
    }

    private void endTest()
{

    setContentView(R.layout.activity_end);
    Button b = (Button) findViewById(R.id.buttonFTTEndExit);
    b.setVisibility(View.INVISIBLE);
    Button buttonRepeat = (Button) findViewById(R.id.buttonFTTEndRepeat);
    buttonRepeat.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v) { finish(); }
    });
}

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(getApplicationContext());
        digital7 = Typeface.createFromAsset(getAssets(), "digital7.ttf");
        String username = prefs.getUsername();
        if (username==null) username = "";
        ar = AudioRecorder.getInstance(username);
        infoTest();
    }

    /**
     * Writes the values of the ArrayList into a CSV file. It creates a new CSV file if there is
     * another with the same name by adding one.
     * @param csvInput ArrayList<Float[]> that will be written in the CSV file.
     * @param prefixFileName prefix name of the CSV file.
     */
    private void audioFeatures2csv(ArrayList<float[]> csvInput, String prefixFileName) throws IOException {

        Preferences prefs = new Preferences(getApplicationContext());
        String username = prefs.getUsername();
        if (username==null) username = "";

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PD_manager/"+username);
        if (!folder.exists())
        {
            try  { folder.mkdir(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PD_manager/"+username;

        //get audioFilePath to external storage (SD card)
        String csvfileStoragePath = path + File.separator + CSV_PATH;
        File sdCsvStorageDir = new File(csvfileStoragePath);

        //create storage directories, if they don't exist
        if(!sdCsvStorageDir.exists())
            sdCsvStorageDir.mkdirs();

        if(sdCsvStorageDir.exists())
        {

            PrintWriter csvWriter;
            try
            {
                String filePath = sdCsvStorageDir.toString() + File.separator + prefixFileName + typeFile;

                File file = new File(filePath);

               while(file.exists()){
                    suffixFileName++;
                    filePath = sdCsvStorageDir.toString() + File.separator + prefixFileName+typeFile;
                    file = new File(filePath);
                }

                csvWriter = new  PrintWriter(new FileWriter(filePath,false));

                for(float[] oneline: csvInput)
                {
                    for(float d : oneline)
                    {
                        csvWriter.print(d + ",");
                    }
                    csvWriter.print("\r\n");

                }

                Log.d("CSV Writer", "CSV Data Written !!");
                csvWriter.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }//end of audio2csv

    /**
     * Initiantes the dispatcher needed for audioprocessing.
     * @param pathAudioFile the path of the audio file (.3gp).
     */
    public void initDispatcher(String pathAudioFile)
    {
        Log.d(TAG, "initDispatcher done");
        mfccList = new ArrayList<float[]>();
        mean_mfccs = new ArrayList<float[]>();
        std_mfccs = new ArrayList<float[]>();
        max_mfccs = new ArrayList<float[]>();
        min_mfccs = new ArrayList<float[]>();
        final int bufferSize = 40;
        final int sampleRate = 8000;

        //sampleRate, audioBufferSize, int bufferOverlap
        //Florian suggested to use 16kHz as sample rate

        new AndroidFFMPEGLocator(getApplicationContext());
        File wavFile = new File (pathAudioFile);

        if(wavFile.exists()){
            Log.d(TAG,"Wav File exists");
        }else{
            Log.d(TAG,"Wav File does not exist");
        }
        dispatcher = AudioDispatcherFactory.fromPipe(wavFile.getAbsolutePath(),sampleRate,bufferSize,0);

        if(dispatcher==null){
            Log.d(TAG,"Dispatcher is null");
        }else{
            Log.d(TAG,"Dispatcher is not null");
        }
    }

    /**
     * Checks if the dispatcher is null
     * @return true if the dispatcher is not null. False otherwise.
     */
    public boolean isDispatcherNull()
    {
        if(dispatcher == null)
            return true;
        else
            return false;
    }

    /**
     * Stops the dispatcher
     */
    public void stopDispatcher()
    {
        dispatcher.stop();
        dispatcher = null;

    }

    /**
     * Extracts the coefficients from dispatcher previously initiated. Coefficients values are
     * defined in the class.
     */
    public void startMfccExtraction()
    {


        //MFCC( samplesPerFrame, sampleRate ) //typical samplesperframe are power of 2 & Samples per frame = (sample rate)/FPS
        //Florian suggested to use 16kHz as sample rate and 512 for frame size
        final MFCC mfccObj = new MFCC(samplesPerFrame, sampleRate, amountOfCepstrumCoef, amountOfMelFilters, lowerFilterFreq, upperFilterFreq ); //(1024,22050);

        if(mfccObj == null) {
            Log.d(TAG,"MFCC object is null");
        }else{
            Log.d(TAG,"MFCC object is not null");
        }

  		/*AudioProcessors are responsible for actual digital signal processing. AudioProcessors are meant to be chained
  		e.g. execute an effect and then play the sound.
  		The chain of audio processor can be interrupted by returning false in the process methods.
  		*/
        dispatcher.addAudioProcessor(mfccObj);
        //handlePitchDetection();
        dispatcher.addAudioProcessor(new AudioProcessor() {

            @Override
            public void processingFinished() {
                // TODO Auto-generated method stub
                //Notify the AudioProcessor that no more data is available and processing has finished
                Log.d("TEST","PROCESSING FINISHED");

                String[] splitFilename = audioFilePath.split("/");
                String filename = splitFilename[splitFilename.length-1];
                filename = filename.substring(0, filename.length()-4);
                String f = prefixDataFileName + filename;
                calculateStatistics(mfccList);
                try {
                    audioFeatures2csv(getMfccList(),prefixDataFileName + filename);
                    audioFeatures2csv(getMeanMFCCS(),prefixMeanFilename + filename);
                    audioFeatures2csv(getSTDMFCCS(),prefixStdFilename + filename);
                    audioFeatures2csv(getMaxMFCCS(),prefixMaxFilename + filename);
                    audioFeatures2csv(getMinMFCCS(),prefixMinFilename + filename);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public boolean process(AudioEvent audioEvent) {
                // TODO Auto-generated method stub
                //process the audio event. do the actual signal processing on an (optionally) overlapping buffer
                //fetchng MFCC array and removing the 0th index because its energy coefficient and florian asked to discard
                float[] mfccOutput = mfccObj.getMFCC();
                mfccOutput = Arrays.copyOfRange(mfccOutput, 1, mfccOutput.length); // Sentencia original
                //Storing in global arraylist so that i can easily transform it into csv
                mfccList.add(mfccOutput);
                Log.i("MFCC", String.valueOf(Arrays.toString(mfccOutput)));
                return true;
            }
        });
        //its better to use thread vs asynctask here. ref : http://stackoverflow.com/a/18480297/1016544
        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    public ArrayList<float[]> getMfccList(){return mfccList;}

    public ArrayList<float[]> getMeanMFCCS () {return mean_mfccs;}

    public ArrayList<float[]> getSTDMFCCS () {
        return std_mfccs;
    }

    public ArrayList<float[]> getMinMFCCS () {
        return min_mfccs;
    }

    public ArrayList<float[]> getMaxMFCCS () {
        return max_mfccs;
    }


    /**
     * Calculates the statistical values of an ArrayList of float[].
     * @param mfccs  is the arrayList where the statitical values want to be obtained.
     */
    public void calculateStatistics(ArrayList<float[]> mfccs) {
        float[] mean = new float[amountOfCepstrumCoef-1],
                std = new float[amountOfCepstrumCoef-1],
                min = new float[amountOfCepstrumCoef-1],
                max = new float[amountOfCepstrumCoef-1],
                frame = new float[amountOfCepstrumCoef-1];
        double[] coeff = new double[mfccs.size()];
        Statistics st;
        for(int j=0;j<amountOfCepstrumCoef-1;j++){
            for(int i=0; i<mfccs.size();i++){
                frame=mfccs.get(i);
                coeff[i]=frame[j];
            }
            st = new Statistics(coeff);

            double meanValue = st.getMean();
            double stdValue = st.getStdDev();
            double minValue = getMinValue(coeff);
            double maxValue = getMaxValue(coeff);
            mean[j]=(float)meanValue;
            std[j] = (float) stdValue;
            min[j] = (float) minValue;
            max[j] = (float) maxValue;
        }
        mean_mfccs.add(mean);
        min_mfccs.add(min);
        std_mfccs.add(std);
        max_mfccs.add(max);
    }

    public static double getMaxValue(double[] array) {
        double maxValue = -1000;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

    // getting the miniumum value
    public static double getMinValue(double[] array) {
        double minValue = 1000;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

}