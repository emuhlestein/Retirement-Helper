package com.intelliviz.retirementhelper.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.ui.NavigationActivity;

/**
 * Class for keeping appwidget updated.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class WidgetProvider extends AppWidgetProvider {

    public void updateMilestones(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.milestone_collection_widget_layout);

            // Bind this widget to a remote view service
            Intent intent = new Intent(context, MilestonesRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.collection_widget_list_view, intent);

            Intent templateIntent = new Intent(context, NavigationActivity.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.collection_widget_list_view, templatePendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        updateMilestones(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        updateMilestones(context, appWidgetManager, appWidgetIds);
    }

    public void updateMilestones(Context context) {
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        updateMilestones(context, appWidgetManager, appWidgetIds);
    }
}
