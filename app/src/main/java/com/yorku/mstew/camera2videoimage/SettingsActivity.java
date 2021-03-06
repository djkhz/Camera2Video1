package com.yorku.mstew.camera2videoimage;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;
import static java.lang.System.getProperties;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    static ArrayList<Size>newarraylist;
    public static Object[] SizeArray;

    boolean ResolutionChanged=false;
    boolean TimelapseChanged=false;
    static boolean ExitBooleanValue=false;




    public void onWindowFocusChanged(boolean hasFocas){
        super.onWindowFocusChanged(hasFocas);
        View decorView=getWindow().getDecorView();
        if(hasFocas){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


            );
        }
    }



    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();


            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);



                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        if(Camera2VideoImageActivity.arraylistcall){
            newarraylist=new ArrayList<Size>(Camera2VideoImageActivity.arraylist);
            Camera2VideoImageActivity.arraylistcall=false;
        }




    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */

    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }



    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || RawCaptureClass.class.getName().equals(fragmentName)
                || resolutionClass.class.getName().equals(fragmentName)
                || OpticalStabilization.class.getName().equals(fragmentName)
                || ExitPreferenceFragemnet.class.getName().equals(fragmentName)
                || Timelapseinput.class.getName().equals(fragmentName)
                || ShowRealTimeInfo.class.getName().equals(fragmentName)
                || VideoEditinput.class.getName().equals(fragmentName)
                || ExportTxtFile.class.getName().equals(fragmentName)
                || ExposureCompensation.class.getName().equals(fragmentName)
                || TestingPatterns.class.getName().equals(fragmentName)
                ;
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    public static class RawCaptureClass extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.new_xml);
            final ListPreference listPreference3=(ListPreference)findPreference("noise_reduction_mode");
            CharSequence[] entries=new CharSequence[Camera2VideoImageActivity.NoiseReductionModes.length];
            CharSequence[] entryValues=new CharSequence[Camera2VideoImageActivity.NoiseReductionModes.length];










            //int[]NoiseReductionModes=Camera2VideoImageActivity.NoiseReductionModes;


                for(int i=0;i<Camera2VideoImageActivity.NoiseReductionModes.length;i++){
                    if(Camera2VideoImageActivity.NoiseReductionModes[i]==0){
                        entries[i]="Noise Reduction Off";
                        entryValues[i]="0";

                    }
                    if(Camera2VideoImageActivity.NoiseReductionModes[i]==1){
                        entries[i]="Noise Redction Fast";
                        entryValues[i]="1";

                    }
                    if(Camera2VideoImageActivity.NoiseReductionModes[i]==2){
                        entries[i]="Noise Reduction High Quality";
                        entryValues[i]="2";

                    }
                    if(Camera2VideoImageActivity.NoiseReductionModes[i]==3){
                        entries[i]="Noise Reduction Minimal";
                        entryValues[i]="3";

                    }
                    if(Camera2VideoImageActivity.NoiseReductionModes[i]==4){
                        entries[i]="Noise Reduction Mode Zero Shutter lag";
                        entryValues[i]="4";
                    }
                    //entries[i]=""+Camera2VideoImageActivity.NoiseReductionModes[i];

                }



            listPreference3.setEntryValues(entryValues);
            listPreference3.setEntries(entries);




            //WhitePoingListPreference.setEntryValues();
            
            setHasOptionsMenu(true);
            
            

        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item){
            int id=item.getItemId();

            return true;
        }

    }
    public static class resolutionClass extends PreferenceFragment{
        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.resolution_xml);
            final ListPreference listPreference=(ListPreference)findPreference("resolution_list");
            if(listPreference.getValue()!=null){
                Camera2VideoImageActivity.SettingresolutionChanged=true;
            }

            SizeArray=newarraylist.toArray();
            CharSequence[] entries=new CharSequence[SizeArray.length];
            CharSequence[] entryValues=new CharSequence[SizeArray.length];
            ListPreference lp1=(ListPreference)findPreference("sensor_available_test_modes");
            CharSequence[] entries2=new CharSequence[Camera2VideoImageActivity.TestPatternModes.length];
            CharSequence[] EntryValues2=new CharSequence[Camera2VideoImageActivity.TestPatternModes.length];
            ListPreference EdgeModeListPreferences=(ListPreference)findPreference("edge_options");
            CharSequence[] entries3=new CharSequence[Camera2VideoImageActivity.EdgeModesAvailable.length];
            CharSequence[] EntryValues3=new CharSequence[Camera2VideoImageActivity.EdgeModesAvailable.length];
            ListPreference HotPixelListPreferences=(ListPreference)findPreference("hot_pixel_mode");
            CharSequence[] entries4=new CharSequence[Camera2VideoImageActivity.HotPixelModes.length];
            CharSequence[] EntryValues4=new CharSequence[Camera2VideoImageActivity.HotPixelModes.length];
            EditTextPreference JPEGQualityPreference=(EditTextPreference)findPreference("set_jpeg_quality");
            bindPreferenceSummaryToValue(findPreference("set_jpeg_quality"));
            ListPreference ToneMapPref=(ListPreference)findPreference("tonemap_mode");
            CharSequence[] entries5=new CharSequence[Camera2VideoImageActivity.AvailableTonemapModes.length];
            CharSequence[] EntryValues5=new CharSequence[Camera2VideoImageActivity.AvailableTonemapModes.length];
            EditTextPreference ChangeBlackLevelPref=(EditTextPreference)findPreference("change_black_level");
            ChangeBlackLevelPref.setSummary("Set number <"+Camera2VideoImageActivity.MaxRawValueOutput);
            bindPreferenceSummaryToValue(findPreference("change_black_level"));

            for(int i=0;i<Camera2VideoImageActivity.AvailableTonemapModes.length;i++){
                if(Camera2VideoImageActivity.AvailableTonemapModes[i]==0){
                    entries5[i]="Contrast Curve";
                    EntryValues5[i]="0";
                }
                if(Camera2VideoImageActivity.AvailableTonemapModes[i]==1){
                    entries5[i]="Fast";
                    EntryValues5[i]="1";
                }
                if(Camera2VideoImageActivity.AvailableTonemapModes[i]==2){
                    entries5[i]="High Quality";
                    EntryValues5[i]="2";
                }
                if(Camera2VideoImageActivity.AvailableTonemapModes[i]==3){
                    entries5[i]="Gamma Value";
                    EntryValues5[i]="3";
                }
                if(Camera2VideoImageActivity.AvailableTonemapModes[i]==4){
                    entries5[i]="Present Curve";
                    EntryValues5[i]="4";
                }

            }


            ToneMapPref.setEntries(entries5);
            ToneMapPref.setEntryValues(EntryValues5);
            for(int j=0;j<Camera2VideoImageActivity.HotPixelModes.length;j++){
                if(Camera2VideoImageActivity.HotPixelModes[j]==0){
                    entries4[j]="Off";
                    EntryValues4[j]="0";
                }
                if(Camera2VideoImageActivity.HotPixelModes[j]==1){
                    entries4[j]="Fast";
                    EntryValues4[j]="1";
                }
                if(Camera2VideoImageActivity.HotPixelModes[j]==2){
                    entries4[j]="High Quality";
                    EntryValues4[j]="2";
                }


            }
            HotPixelListPreferences.setEntries(entries4);
            HotPixelListPreferences.setEntryValues(EntryValues4);

            for(int i=0;i<Camera2VideoImageActivity.TestPatternModes.length;i++){
                //entries[i]=""+Camera2VideoImageActivity.TestPatternModes[i];

                if(Camera2VideoImageActivity.TestPatternModes[i]==0){
                    entries2[i]="Off";
                    EntryValues2[i]="0";
                }
                if(Camera2VideoImageActivity.TestPatternModes[i]==1){
                    entries2[i]="Solid Color";
                    EntryValues2[i]="1";
                }
                if(Camera2VideoImageActivity.TestPatternModes[i]==2){
                    entries2[i]="Color Bars";
                    EntryValues2[i]="2";
                }
                if(Camera2VideoImageActivity.TestPatternModes[i]==3){
                    entries2[i]="Pattern Mode Fade To Gray";
                    EntryValues2[i]="3";
                }
                if(Camera2VideoImageActivity.TestPatternModes[i]==4){
                    entries2[i]="PN9";
                    EntryValues2[i]="4";
                }
                if(Camera2VideoImageActivity.TestPatternModes[i]==256){
                    entries2[i]="Custom1";
                    EntryValues2[i]="5";
                }

            }
            for(int i=0;i<Camera2VideoImageActivity.EdgeModesAvailable.length;i++){
                if(Camera2VideoImageActivity.EdgeModesAvailable[i]==0){
                    entries3[i]="Off";
                    EntryValues3[i]="0";
                }
                if(Camera2VideoImageActivity.EdgeModesAvailable[i]==1){
                    entries3[i]="Fast";
                    EntryValues3[i]="1";
                }
                if(Camera2VideoImageActivity.EdgeModesAvailable[i]==2){
                    entries3[i]="High Quality";
                    EntryValues3[i]="2";
                }
                if(Camera2VideoImageActivity.EdgeModesAvailable[i]==3){
                    entries3[i]="Zero Shutter Lag";
                    EntryValues3[i]="3";
                }

            }
            EdgeModeListPreferences.setEntries(entries3);
            EdgeModeListPreferences.setEntryValues(EntryValues3);
            for (int i=0;i<SizeArray.length;i++){
                entries[i]=SizeArray[i].toString();
                entryValues[i]=""+i;
            }

            setHasOptionsMenu(true);
            lp1.setEntries(entries2);
            lp1.setEntryValues(EntryValues2);
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);

        }
    }
    public static class OpticalStabilization extends  PreferenceFragment{
        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.optical_stabilization);

        }
    }
    public static class ShowRealTimeInfo extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.show_real_time_info);

        }
    }
    public static class ExitPreferenceFragemnet extends PreferenceFragment{
        
        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            ExitBooleanValue=true;










        }




    }





    public static class Timelapseinput extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.timelpase_change);
            bindPreferenceSummaryToValue(findPreference("PictureTimeLimit"));
            bindPreferenceSummaryToValue(findPreference("PictureSecondStep"));
            bindPreferenceSummaryToValue(findPreference("VideoSecondStep"));
            bindPreferenceSummaryToValue(findPreference("VideoTimelapseTimeLimit"));



        }
    }
    public static class VideoEditinput extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.videosettings);

            //bindPreferenceSummaryToValue(findPreference("control_antibanding_mode"));
            bindPreferenceSummaryToValue(findPreference("RecordTimeStop"));
            bindPreferenceSummaryToValue(findPreference("ChangeVideoFPS"));
            bindPreferenceSummaryToValue(findPreference("EncodingBitRate"));

        }

    }
    public static class ExportTxtFile extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.export_files);

        }
    }
    public static class ExposureCompensation extends  PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.exposure_compensation_seekbar);

        }
    }

    public static class TestingPatterns extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.new_xml);

        }
    }





}
