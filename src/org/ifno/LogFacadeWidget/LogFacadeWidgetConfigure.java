package org.ifno.LogFacadeWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created with IntelliJ IDEA.
 * User: inferno
 * Date: 08.07.13
 * Time: 14:10
 * Palamarchuk Maksym © 2013
 */
public class LogFacadeWidgetConfigure extends Activity {
    private ListView listView;
    private String[] logLevels;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_facade_config);
        ArrayAdapter<CharSequence> stringArrayAdapter = ArrayAdapter.createFromResource(this, R.array.log_levels, android.R.layout.simple_list_item_single_choice);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(stringArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onOkClicked(view);
            }
        });
        logLevels = getResources().getStringArray(R.array.log_levels);

    }

    public void onOkClicked(View view) {
        final int checkedItemPosition = listView.getCheckedItemPosition();
        if (checkedItemPosition == ListView.INVALID_POSITION) {
            new AlertDialog.Builder(this).setMessage("You must select log level").setCancelable(true).show();
            return;
        }
        Intent updateLogLevelIntent = new Intent();
        updateLogLevelIntent.setAction(LogFacadeWidgetProvider.ACTION_WIDGET_UPDATE_LOG_LEVEL);
        updateLogLevelIntent.putExtra(LogFacadeWidgetProvider.KEY_LOG_LEVEL, logLevels[checkedItemPosition]);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LogFacadeWidgetProvider.class));
        updateLogLevelIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(updateLogLevelIntent);
        finish();
    }
}