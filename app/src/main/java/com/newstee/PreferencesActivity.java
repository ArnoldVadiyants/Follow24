package com.newstee;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.newstee.helper.InternetHelper;
import com.newstee.helper.SessionManager;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        String mCountry;
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference countryPref = findPreference("country");
            boolean isLogin = new SessionManager(getActivity()).isLoggedIn();
            if(!isLogin)
            {
                countryPref.setSummary(R.string.msg_select_country);
                countryPref.setEnabled(false);
            }
            else
            {
                countryPref.setSummary(R.string.summary_country_pref);
                countryPref.setEnabled(true);
            }


            Preference link_to_sitePref = findPreference("link_to_site");

            link_to_sitePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    InternetHelper.goToDeveloperSite(getActivity());
                    return true;
                }
            });
            mCountry = new SessionManager(getActivity()).getCountrySettings();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            String newCountry = new SessionManager(getActivity()).getCountrySettings();
            if(mCountry.equals(newCountry))
            {
                return;
            }
           new MainLoadAsyncTask(getActivity()) {
               @Override
               void hideContent() {

               }

               @Override
               void showContent() {

               }
           }.execute(Constants.ARGUMENT_COUNTRY_CODE);
        }
    }
}