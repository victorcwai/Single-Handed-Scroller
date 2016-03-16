package wai.innovative2;

import android.app.TabActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
//    private TextView xText, yText, zText;
    public Float X, Y, Z;
    private Sensor mySensor;
    private SensorManager SM;

    // start voice command
    public boolean voiceCommandMode=false;
    private SpeechRecognizer sr;
    private static final String TAG = "MyStt3Activity";
    // end voice command


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

// Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

// Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

// Assign TextView
//        xText = (TextView) findViewById(R.id.xText);
//        yText = (TextView) findViewById(R.id.yText);
//        zText = (TextView) findViewById(R.id.zText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        sr = SpeechRecognizer.createSpeechRecognizer(this);        // 初始化识别工具，得到句柄

        sr.setRecognitionListener(new listener());         // 注册回调类及函数
        if (voiceCommandMode==true)
            sr.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

//code for the 下拉選單
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //建立一個ArrayAdapter物件，並放置下拉選單的內容
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,new String[]{"default.txt","poem.txt","sowing.txt"});
        //設定下拉選單的樣式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //設定項目被選取之後的動作
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You have chosen " + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                //update the text view
                updateTextView(readTextFile(adapterView.getSelectedItem().toString()));
            }

            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(MainActivity.this, "You have not chosen anything", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (voiceCommandMode==true) {
                voiceCommandMode = false;
                item.setTitle("Turn on audio mode");
            }
            else {
                voiceCommandMode=true;
                Button p1_button = (Button)findViewById(R.id.action_settings);
                item.setTitle("Turn off Audio mode");
                sr.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Text";
                case 1:
                    return "Image";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            LinearLayout LL = (LinearLayout) rootView.findViewById(R.id.section_label);
            TextView tv = (TextView) LL.getChildAt(0);
            ImageView iv = (ImageView) LL.getChildAt(1);
            tv.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            iv.setImageResource(R.drawable.interstellar);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) { // if it is the IMAGE tab
                tv.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
            }
            return rootView;
        }
    }
    //PlaceholderFragment class end

    //Our code ---------------------------------------------------------------
    public void tabSwitch() { // switch between the 2 tabs
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager.getCurrentItem() == 0) {
            mViewPager.setCurrentItem(1, true);
        } else {
            mViewPager.setCurrentItem(0, true);
        }
    }

    public void scroll(String direction) { // might need to adjust value of x and y
        mViewPager = (ViewPager) findViewById(R.id.container);
        View activeView = mViewPager.getChildAt(mViewPager.getCurrentItem());
        if (activeView != null) {
            ScrollView V = (ScrollView) activeView.findViewById(R.id.my_id);                       // separate from horizontal, coz we only can make scrollview scroll vertically
            HorizontalScrollView HV = (HorizontalScrollView) activeView.findViewById(R.id.my_id2); // we need to create a horizontal view to move left/right
            if (direction == "Up") V.smoothScrollBy(0, 30);
            else if (direction == "Down") V.smoothScrollBy(0, -30);
            else if (direction == "Left") HV.smoothScrollBy(30, 0);
            else if (direction == "Right") HV.smoothScrollBy(-30, 0);
            else if (direction == "None") ;
        }
    }
    public void quickScroll(String direction) { // might need to adjust value of x and y
        mViewPager = (ViewPager) findViewById(R.id.container);
        View activeView = mViewPager.getChildAt(mViewPager.getCurrentItem());
        if (activeView != null) {
            ScrollView V = (ScrollView) activeView.findViewById(R.id.my_id);                       // separate from horizontal, coz we only can make scrollview scroll vertically
            HorizontalScrollView HV = (HorizontalScrollView) activeView.findViewById(R.id.my_id2); // we need to create a horizontal view to move left/right
            if (direction == "Up") V.smoothScrollBy(0, 150);
            else if (direction == "Down") V.smoothScrollBy(0, -150);
            else if (direction == "Left") HV.smoothScrollBy(150, 0);
            else if (direction == "Right") HV.smoothScrollBy(-150, 0);
            else if (direction == "None") ;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (voiceCommandMode==true)
            return;
//        xText.setText("X: " + event.values[0]);
//        yText.setText("Y: " + event.values[1]);
//        zText.setText("Z: " + event.values[2]);
        X = event.values[0];
        Y = event.values[1];
        Z = event.values[2];
        if (Y < 8 && Y > 3.5) {
            if (X > 4) {
                if(X > 6)
                    this.quickScroll("Right");
                else
                    this.scroll("Right");
            } else if (X < -4) {
                if(X < -6)
                    this.quickScroll("Left");
                else
                    this.scroll("Left");
            }

        } else {
            if (Y > 8) {
                if(Z < 1)
                    this.quickScroll("Up");
                else
                    this.scroll("Up");
            } else if (Y < 3.5) {
                if(Y < 0)
                    this.quickScroll("Down");
                else
                    this.scroll("Down");
            }
        }

        //     start brightness adjustment
        if (Y<-5){
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            float brightness=0.0f;
            lp.screenBrightness = brightness;
            getWindow().setAttributes(lp);

        }else if(Y>2){
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            float brightness=1.0f;
            lp.screenBrightness = brightness;
            getWindow().setAttributes(lp);

        }
        System.out.println("X: "+X);
        System.out.println("Y: "+Y);
        System.out.println("Z: "+Z);
        System.out.println("----------------------------");

        //     end brightness adjustment
    }

    //reference: http://stackoverflow.com/questions/9676773/read-a-text-file-android
    public String readTextFile(String fileName) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            final StringBuilder buffer = new StringBuilder();
            while ((line = in.readLine()) != null) {
                buffer.append(line).append(System.getProperty("line.separator"));
            }
            return buffer.toString();
        } catch (final IOException e) {
            return "";
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // ignore //
            }
        }
    }
    //end reference

    protected void updateTextView(String input) {
        mViewPager = (ViewPager) findViewById(R.id.container);
        View activeView = mViewPager.getChildAt(mViewPager.getCurrentItem());
        if (activeView != null) {

            ScrollView v = (ScrollView) activeView.findViewById(R.id.my_id);
            if (mViewPager.getCurrentItem() == 0) { // if we are in the TEXT tab
                TextView t = (TextView) activeView.findViewById(R.id.text_view_1);// change is needed
                t.setText(input);
            }
        }
    }



    class listener implements RecognitionListener            // 回调类的实现

    {

        public void onReadyForSpeech(Bundle params)

        {

            Log.d(TAG, "onReadyForSpeech");

        }

        public void onBeginningOfSpeech()

        {

            Log.d(TAG, "onBeginningOfSpeech");

        }

        public void onRmsChanged(float rmsdB)

        {

            Log.d(TAG, "onRmsChanged");

        }

        public void onBufferReceived(byte[] buffer)

        {

            Log.d(TAG, "onBufferReceived");

        }

        public void onEndOfSpeech()

        {

            Log.d(TAG, "onEndofSpeech");

        }

        public void onError(int error)

        {

            Log.d(TAG, "error " + error);
            String s = "";
            switch(error){
                case SpeechRecognizer.ERROR_AUDIO:
                    s = "1";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    s = "2";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    s = "3";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    s = "4";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    s = "5";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    s = "6";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    s = "7";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    s = "8";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    s = "9";
                    break;
            }
//            xText.setText("error please retry");
            if (voiceCommandMode==true)
                sr.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

        }

        public void onResults(Bundle results)     // 返回识别到的数据

        {

            String str = new String();

            Log.d(TAG, "onResults " + results);

            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < data.size(); i++)

            {

                Log.d(TAG, "result " + data.get(i));

                str += data.get(i);

            }
            byte[] bs = str.getBytes();
            String tempString ="";
            for(byte b : bs)
                tempString=tempString.concat(Integer.toHexString(b));


//            xText.setText(str);
            boolean page ;
            if (str.toLowerCase().contains("page")|| tempString.contains("ffffffe9ffffffa0ffffff81"))
                page=true;
            else
                page=false;
            if(str.toLowerCase().contains("down") || str.toLowerCase().contains("next") || tempString.contains("ffffffe4ffffffb8ffffff8b") ){

                ScrollView v = (ScrollView)findViewById(R.id.my_id);
                if (page){

                  //  xText.setText("next page");
                    v.smoothScrollBy(0, 500);

                }else{

                   // xText.setText("next few lines");
                    v.smoothScrollBy(0, 25);

                }
            }else if (str.toLowerCase().contains("up") || tempString.contains("ffffffe4ffffffb8ffffff8a")  ) {

                ScrollView v = (ScrollView)findViewById(R.id.my_id);

                if (page){

                  //  xText.setText("previous page");
                    v.smoothScrollBy(0, -500);

                }else{

                   // xText.setText("up few lines");
                    v.smoothScrollBy(0, -25);

                }

            }
            if (voiceCommandMode==true)
                sr.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

        }

        public void onPartialResults(Bundle partialResults)

        {

            Log.d(TAG, "onPartialResults");

        }

        public void onEvent(int eventType, Bundle params)

        {

            Log.d(TAG, "onEvent " + eventType);

        }





        public void onClick(View v) {

            // if (v.getId() == R.id.btn_speak) {

            //    sr.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

            // }

        }



    }

}



