package anubhav.github.finder.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import anubhav.github.finder.BuildConfig;
import anubhav.github.finder.R;
import anubhav.github.finder.global.MyApp;
import anubhav.github.finder.helpers.Helper;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        MyApp.getSharedPrefs().registerOnSharedPreferenceChangeListener(this);

        EditTextPreference mqttBrokerPortPref = findPreference(MyApp.getAppContext().getString(R.string.pref_key_mqtt_broker_port));
        if (mqttBrokerPortPref != null)
            mqttBrokerPortPref.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));

        updateDynamicPrefs();

        bindClickablePrefs();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key == null)
            return;

        //if (key.contains("mqtt_"))
        //    MqttClient.disconnect();

        if (key.equals(MyApp.getAppContext().getString(R.string.pref_key_mqtt_broker_tls)) ||
            key.equals(MyApp.getAppContext().getString(R.string.pref_key_mqtt_client_use_auth)))
            updateDynamicPrefs();
    }

    private static void setPrefEnabled(Preference pref, boolean enabled) {
        if (pref == null) return;
        pref.setEnabled(enabled);
        pref.setShouldDisableView(!enabled);
    }

    private void updateDynamicPrefs() {
        Preference tlsValidatePref = findPreference(MyApp.getAppContext().getString(R.string.pref_key_mqtt_broker_tls_validate));
        setPrefEnabled(tlsValidatePref, MyApp.getSharedPrefs().getBoolean(MyApp.getAppContext().getString(R.string.pref_key_mqtt_broker_tls), false));

        boolean useAuth = MyApp.getSharedPrefs().getBoolean(MyApp.getAppContext().getString(R.string.pref_key_mqtt_client_use_auth), false);
        Preference usernamePref = findPreference(MyApp.getAppContext().getString(R.string.pref_key_mqtt_client_auth_username));
        setPrefEnabled(usernamePref, useAuth);
        Preference passwordPref = findPreference(MyApp.getAppContext().getString(R.string.pref_key_mqtt_client_auth_password));
        setPrefEnabled(passwordPref, useAuth);
    }

    private void bindClickablePrefs() {
        buildClickablePref(R.string.pref_key_backup_save, (pref) -> { Helper.backupData(getContext()); return true; });
        buildClickablePref(R.string.pref_key_backup_restore, (pref) -> { Helper.restoreData(getContext()); return true; });
        buildClickablePref(R.string.pref_key_mqtt_broker_test, (pref) -> { return true; });

        Preference githubPref = findPreference(MyApp.getAppContext().getString(R.string.pref_key_info_github));
        if (githubPref != null) {
            githubPref.setSummary(MyApp.getAppContext().getString(R.string.pref_sum_info_github, BuildConfig.COMMIT_HASH));
            buildClickablePref(githubPref, (pref) -> { Helper.startUrlIntent("https://github.com/manojvermamv/Github-Finder"); return true; });
        }
    }

    private void buildClickablePref(int prefKey, Preference.OnPreferenceClickListener onClickHandler) {
        Preference pref = findPreference(MyApp.getAppContext().getString(prefKey));
        buildClickablePref(pref, onClickHandler);
    }
    private void buildClickablePref(Preference pref, Preference.OnPreferenceClickListener onClickHandler) {
        if (pref == null)
            return;
        pref.setOnPreferenceClickListener(onClickHandler);
    }
}