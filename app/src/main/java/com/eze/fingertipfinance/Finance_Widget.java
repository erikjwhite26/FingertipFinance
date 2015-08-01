package com.eze.fingertipfinance;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Implementation of App Widget functionality.
 */
public class Finance_Widget extends AppWidgetProvider {

    private static Finance_DataSource ds;
    private static List<Finance_Input> inputs = new ArrayList<Finance_Input>();
    private static float balance = 0;
    private static float incomeAmt = 0;
    private static float billAmt = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        ds = new Finance_DataSource(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.finance__widget);

        Intent widgetIntent = new Intent(context, Finance_Widget_Activity.class);
        PendingIntent widgetPendingIntent = PendingIntent.getActivity(context, 0, widgetIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_add, widgetPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


