package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Madhuri on 1/9/2016.
 */
public class ScoresWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context ctxt = null;
    private ArrayList listItemList = new ArrayList();
    private int appWidgetId;
    private ContentResolver cr;
    private Cursor resultsCursor;
    private String TAG = ScoresWidgetViewsFactory.class.getSimpleName();

    public ScoresWidgetViewsFactory(Context ctxt, Intent intent) {
        this.ctxt = ctxt;
        this.cr   = ctxt.getContentResolver();
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

        int count;
        Uri contentURI = Uri.parse(DatabaseContract.BASE_CONTENT_URI+"/date");
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String[] curDateStr = new String[1];
        curDateStr[0] = mformat.format(curDate);
        if ((resultsCursor != null) && (resultsCursor.isClosed() == false))
            resultsCursor.close();
        resultsCursor = cr.query(contentURI,null,null,curDateStr,null);
        if (resultsCursor != null) {
            count = resultsCursor.getCount();
            Log.e(TAG, "resultsCursor count:" + count);
            populateListItem();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.widget_list_row);

        WidgetListItem curListItem = (WidgetListItem)listItemList.get(position);

        row.setTextViewText(R.id.home_name, curListItem.home_name);
        row.setTextViewText(R.id.away_name, curListItem.away_name);
        row.setImageViewResource(R.id.home_crest, curListItem.home_crest);
        row.setImageViewResource(R.id.away_crest, curListItem.away_crest);
        row.setTextViewText(R.id.score_textview, Utilies.getScores(curListItem.home_score, curListItem.away_score));
        row.setTextViewText(R.id.data_textview, curListItem.date);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {

    }

    private void populateListItem() {
        int itemCount = resultsCursor.getCount();
        resultsCursor.moveToFirst();
        for (int i=0; i<itemCount;i++) {
            WidgetListItem widgetListItem = new WidgetListItem();
            widgetListItem.home_name = resultsCursor.getString(resultsCursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
            widgetListItem.away_name = resultsCursor.getString(resultsCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
            widgetListItem.date = resultsCursor.getString(resultsCursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
            widgetListItem.home_score = resultsCursor.getInt(resultsCursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
            widgetListItem.away_score = resultsCursor.getInt(resultsCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));
            widgetListItem.home_crest = Utilies.getTeamCrestByTeamName(widgetListItem.home_name);
            widgetListItem.away_crest = Utilies.getTeamCrestByTeamName(widgetListItem.away_name);
            listItemList.add(widgetListItem);
            resultsCursor.moveToNext();
        }
    }

}
