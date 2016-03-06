package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.ScoresWidgetViewsFactory;

/**
 * Created by Madhuri on 1/9/2016.
 */
public class ScoresWidgetService extends RemoteViewsService {
    private static final String TAG = ScoresWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                            AppWidgetManager.INVALID_APPWIDGET_ID);
        return(new ScoresWidgetViewsFactory(this.getApplicationContext(),
                intent));
    }
}
