package de.domjos.mamaplanner.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.services.caldav.CalDavCredentials;
import de.domjos.mamaplanner.services.caldav.CalDavSync;
import de.domjos.mamaplanner.settings.Global;

public final class SyncActivity extends AbstractActivity {
    private Spinner spAppSyncType;
    private EditText txtAppSyncHost, txtAppSyncBase, txtAppSyncUser, txtAppSyncPwd;
    private ImageButton cmdAppSyncSave;
    private TableRow rowAppSyncCustom;
    private TextView lblAppSyncLog;

    public SyncActivity() {
        super(R.layout.sync_activity);
    }

    @Override
    protected void initActions() {

        this.spAppSyncType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rowAppSyncCustom.setVisibility(i == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        this.cmdAppSyncSave.setOnClickListener(view -> {
            CalDavCredentials calDavCredentials = this.getData();

            CalDavSync calDavSync = new CalDavSync(calDavCredentials);
            SyncActivity.this.runOnUiThread(() -> {
                if (calDavSync.test()) {
                    this.lblAppSyncLog.setText(R.string.app_sync_success);
                    Global.setSettingToPreference(CalDavCredentials.CAL_HOST, calDavCredentials.getHostName(), this.getApplicationContext());
                    Global.setSettingToPreference(CalDavCredentials.CAL_BASE, calDavCredentials.getBasePath(), this.getApplicationContext());
                    Global.setSettingToPreference(CalDavCredentials.CAL_USER, calDavCredentials.getUserName(), this.getApplicationContext());
                    Global.setSettingToPreference(CalDavCredentials.CAL_PWD, calDavCredentials.getPassword(), this.getApplicationContext());
                } else {
                    this.lblAppSyncLog.setText(R.string.app_sync_no_success);
                }
            });
        });
    }

    @Override
    protected void initControls() {
        this.spAppSyncType = this.findViewById(R.id.spAppSyncType);

        this.txtAppSyncHost = this.findViewById(R.id.txtAppSyncHost);
        this.txtAppSyncBase = this.findViewById(R.id.txtAppSyncBasic);
        this.txtAppSyncUser = this.findViewById(R.id.txtAppSyncUser);
        this.txtAppSyncPwd = this.findViewById(R.id.txtAppSyncPassword);

        this.cmdAppSyncSave = this.findViewById(R.id.cmdAppSyncSave);
        this.rowAppSyncCustom = this.findViewById(R.id.rowCustom);
        this.lblAppSyncLog = this.findViewById(R.id.lblAppSyncLog);

        this.rowAppSyncCustom.setVisibility(View.GONE);
        this.setData();
    }

    private void setData() {
        String host = Global.getSettingFromPreference(CalDavCredentials.CAL_HOST, "", this.getApplicationContext());
        String pwd = Global.getSettingFromPreference(CalDavCredentials.CAL_PWD, "", this.getApplicationContext());
        String user = Global.getSettingFromPreference(CalDavCredentials.CAL_USER, "", this.getApplicationContext());
        String base = Global.getSettingFromPreference(CalDavCredentials.CAL_BASE, "", this.getApplicationContext());

        if(!host.isEmpty()) {
            if(host.contains("google.com")) {
                this.spAppSyncType.setSelection(0);
            } else if(host.contains("yahoo.com")) {
                this.spAppSyncType.setSelection(1);
            }
        }
        this.txtAppSyncHost.setText(host);
        this.txtAppSyncBase.setText(base);
        this.txtAppSyncUser.setText(user);
        this.txtAppSyncPwd.setText(pwd);
    }

    private CalDavCredentials getData() {
        if(this.spAppSyncType.getSelectedItemPosition()==2) {
            String host = this.txtAppSyncHost.getText().toString();
            String base = this.txtAppSyncBase.getText().toString();
            String pwd = this.txtAppSyncPwd.getText().toString();
            String user = this.txtAppSyncUser.getText().toString();

            return new CalDavCredentials(user, pwd, host, base);
        } else {
            String pwd = this.txtAppSyncPwd.getText().toString();
            String user = this.txtAppSyncUser.getText().toString();

            if(this.spAppSyncType.getSelectedItemPosition()==0) {
                return new CalDavCredentials(user, pwd, CalDavCredentials.Type.GOOGLE);
            } else {
                return new CalDavCredentials(user, pwd, CalDavCredentials.Type.YAHOO);
            }
        }
    }
}
