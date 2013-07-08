package org.ifno.LogFacadeWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: inferno
 * Date: 08.07.13
 * Time: 12:34
 * Palamarchuk Maksym © 2013
 */
public class LogFacadeWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_LOG_LEVEL_UPDATED = "org.ifno.LogFacadeWidget.ACTION_LOG_LEVEL_UPDATED";
    public static final String ACTION_WIDGET_RECEIVER = "org.ifno.LogFacadeWidget.ACTION_WIDGET_RECEIVER";
    public static final String ACTION_WIDGET_UPDATE_LOG_LEVEL = "org.ifno.LogFacadeWidget.ACTION_WIDGET_UPDATE_LOG_LEVEL";
    public static final String KEY_LOG_LEVEL = "org.ifno.LogFacadeWidget.KEY_LOG_LEVEL";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Создаем новый RemoteViews
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);

        final String sharedPrefsName = context.getResources().getString(R.string.shared_prefs_name);
        final String defaultLogLevelFromResources = context.getResources().getString(R.string.default_log_level_text);
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_MULTI_PROCESS);
        final String defaultLogLevel = sharedPreferences.getString(KEY_LOG_LEVEL, defaultLogLevelFromResources);

        remoteViews.setTextViewText(R.id.logLevel, defaultLogLevel);

        //Подготавливаем Intent для Broadcast
        Intent active = new Intent(context, LogFacadeWidgetProvider.class);
        active.setAction(ACTION_WIDGET_RECEIVER);

        //создаем наше событие
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_container, actionPendingIntent);
        //обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Ловим наш Broadcast, проверяем и выводим сообщение
        final String action = intent.getAction();
        if (ACTION_WIDGET_RECEIVER.equals(action)) {
            Intent settingsIntent = new Intent(context, LogFacadeWidgetConfigure.class);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);

        }

        if (ACTION_WIDGET_UPDATE_LOG_LEVEL.equals(action)) {
            String msg = "null";
            try {
                msg = intent.getStringExtra(KEY_LOG_LEVEL);
            } catch (NullPointerException e) {
                Log.e("OPPA", "msg = null");
            }
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
            remoteViews.setTextViewText(R.id.logLevel, msg);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, LogFacadeWidgetProvider.class), remoteViews);

            final String sharedPrefsName = context.getResources().getString(R.string.shared_prefs_name);
            final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_MULTI_PROCESS);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LOG_LEVEL, msg);
            editor.commit();

            Toast.makeText(context, sharedPreferences.getString(KEY_LOG_LEVEL, "NULL"), Toast.LENGTH_SHORT).show();

            Intent logLevelUpdatedIntent = new Intent(ACTION_LOG_LEVEL_UPDATED);
            context.sendBroadcast(logLevelUpdatedIntent);
        }
        super.onReceive(context, intent);
    }
}
