package barqsoft.footballscores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;


/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends CursorAdapter
{
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME_ID = 10;
    public static final int COL_AWAY_ID = 11;

    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#football_scores";
    private ImageView mImageView;
    private static final String TAG = scoresAdapter.class.getSimpleName();

    public scoresAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);

        String[] teamProjection = new String[] {DatabaseContract.team_table.TEAM_ID_COL,
                                                DatabaseContract.team_table.TEAM_LOGO_COL};
        String homeID = cursor.getString(COL_HOME_ID);

        Cursor teamCursor = mContext.getContentResolver().query(DatabaseContract.team_table.buildTeamWithTeamID(),
                teamProjection, null, new String[]{homeID}, null);
        if (teamCursor.getCount() != 0) {
            teamCursor.moveToFirst();
            ImageViewHolder homeCrestHolder = new ImageViewHolder(mHolder.home_crest,
                    teamCursor.getString(teamCursor.getColumnIndexOrThrow(DatabaseContract.team_table.TEAM_LOGO_COL)));

            teamCursor.close();
            Log.d(TAG,"GetfileExtension:"+homeCrestHolder.getImagePath());
            String ext = Utilies.getFileExtension(homeCrestHolder.getImagePath());
            if (ext.contentEquals("png")) {
                Log.d(TAG,"PNG");
                Uri imagePath = Uri.parse(homeCrestHolder.getImagePath());

                Picasso.with(context)
                        .load(homeCrestHolder.getImagePath())//"https://upload.wikimedia.org/wikipedia/en/8/86/Sevilla_cf_200px.png")
                        .placeholder(R.drawable.no_icon)
                        .into(mHolder.home_crest);
               // mHolder.home_crest.setImageURI(imagePath);
            } else if(ext.contentEquals("")) {
                mHolder.home_crest.setImageResource(R.drawable.no_icon);
            }
            else {
                mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(cursor.getString(COL_HOME)));
               //HttpImageRequestTask getImageTask = new HttpImageRequestTask();
                //getImageTask.execute(homeCrestHolder);
            }
        }
        else {
            mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(cursor.getString(COL_HOME)));
        }
       // mImageView = mHolder.home_crest;
       /*Picasso.with(context)
                .load("http://i.i mgur.com/DvpvklR.png")
                .resize(50,50)
                .placeholder(R.drawable.no_icon)
                .into(mHolder.home_crest);*/
       /*mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));*/
        mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)));
        String[] projection = new String[] {DatabaseContract.league_table.LEAGUE_ID_COL,
                                            DatabaseContract.league_table.LEAGUE_NAME_COL};
        Cursor retCursor = mContext.getContentResolver().query(DatabaseContract.league_table.buildScoreWithLeagueID(),
                                    projection,null,new String[]{ cursor.getString(COL_LEAGUE)},null);
        if (retCursor.getCount() == 0)
          mHolder.league_name.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE)));
        else {
            retCursor.moveToFirst();
            String leagueName = retCursor.getString(retCursor.getColumnIndexOrThrow(DatabaseContract.league_table.LEAGUE_NAME_COL));
            mHolder.league_name.setText(leagueName);
        }
        retCursor.close();
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id)
        {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.detail_league_textview);
            league.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText()+" "
                    +mHolder.score.getText()+" "+mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    @SuppressLint("NewApi")
    private void updateImageView(ImageViewHolder imageViewHolder) {
        if (imageViewHolder != null) {
            Drawable drawable = imageViewHolder.getImageDrawable();
            if (drawable != null) {
                imageViewHolder.crestImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                imageViewHolder.crestImage.setImageDrawable(drawable);
            }
        }
    }

    private class HttpImageRequestTask extends AsyncTask<ImageViewHolder,Void, ImageViewHolder> {
        private final String TAG = HttpImageRequestTask.class.getSimpleName();
        @Override
        protected ImageViewHolder doInBackground(ImageViewHolder... params) {
            try {
                final URL url = new URL(params[0].getImagePath()); //new URL("https://upload.wikimedia.org/wikipedia/commons/2/2f/VfR_Aalen_Wappen.svg");

                ImageViewHolder imageHolder = params[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                Log.d(TAG,"Getting image before inputstream:"+url.toString());
                SVG svg = SVG.getFromInputStream(inputStream);
                Log.d(TAG,"Getting image after inputstream:"+url.toString());
                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                imageHolder.setImageDrawable(drawable);

                return imageHolder;

            } catch(Exception e) {
                Log.e(TAG,e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ImageViewHolder imageViewHolder) {
            updateImageView(imageViewHolder);
        }
    }

}
