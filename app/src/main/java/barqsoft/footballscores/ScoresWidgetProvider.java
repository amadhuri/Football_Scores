package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Arrays;

import barqsoft.footballscores.service.ScoresWidgetService;

/**
 * Created by Madhuri on 12/30/2015.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {


    public final String TAG = ScoresWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews= new RemoteViews(context.getPackageName(),R.layout.scores_appwidget);

        final int N = appWidgetIds.length;
        for (int i=0;i<N;i++) {

            int currentWidgetId = appWidgetIds[i];
            Intent svcIntent = new Intent(context, ScoresWidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, currentWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(R.id.lv_widget_scores_list, svcIntent);
            remoteViews.setEmptyView(R.id.lv_widget_scores_list, R.id.tv_widget_emptyview);

            Intent launchActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            remoteViews.setOnClickPendingIntent(R.id.score_widget, pendingIntent);

            appWidgetManager.updateAppWidget(currentWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
