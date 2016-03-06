package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService
{
    public static final String LOG_TAG = "myFetchService";
    public static int count = 0;
    public myFetchService()
    {
        super("myFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        getData("n2");
        getData("p2");

        return;
    }

    private void getData (String timeFrame)
    {
        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            m_connection.addRequestProperty("X-Auth-Token",getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Exception here" + e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }
                processJSONdata(JSON_data, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }
    private void processJSONdata (String JSONdata,Context mContext, boolean isReal)
    {
        //JSON data
        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes
        final String BUNDESLIGA1 = "394";
        final String BUNDESLIGA2 = "395";
        final String LIGUE1 = "396";
        final String LIGUE2 = "397";
        final String PREMIER_LEAGUE = "398";
        final String PRIMERA_DIVISION = "399";
        final String SEGUNDA_DIVISION = "400";
        final String SERIE_A = "401";
        final String PRIMERA_LIGA = "402";
        final String Bundesliga3 = "403";
        final String EREDIVISIE = "404";


        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";
        final String HOME_TEAM_URL = "homeTeam";
        final String AWAY_TEAM_URL = "awayTeam";
        final String TEAM_LINK_URL = "http://api.football-data.org/alpha/teams/";

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;
        String HomeTeamID = null;
        String AwayTeamID = null;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++)
            {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");
                League = League.replace(SEASON_LINK,"");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if(     League.equals(PREMIER_LEAGUE)      ||
                        League.equals(SERIE_A)             ||
                        League.equals(BUNDESLIGA1)         ||
                        League.equals(BUNDESLIGA2)         ||
                        League.equals(PRIMERA_DIVISION)     )
                {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString("href");
                    match_id = match_id.replace(MATCH_LINK, "");
                    if(!isReal){
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id=match_id+Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0,mDate.indexOf("T"));
                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(mDate+mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0,mDate.indexOf(":"));

                        if(!isReal){
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG,e.getMessage());
                    }
                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);

                    HomeTeamID = match_data.getJSONObject(LINKS).getJSONObject(HOME_TEAM_URL).getString("href");
                    HomeTeamID = HomeTeamID.replace(TEAM_LINK_URL, "");

                    if(Utilies.getTeamCrestByTeamName(Home) == R.drawable.no_icon) { //icon not present in static images
                        String[] imageProjection = new String[]{DatabaseContract.team_table.TEAM_LOGO_COL};
                        Cursor logoCursor = mContext.getContentResolver().query(DatabaseContract.team_table.buildTeamWithTeamID(),
                                                            imageProjection,null,new String[]{HomeTeamID},null);
                        if(logoCursor.getCount() == 0) { //if the logo image URL not found in the database
                            FetchImageURL fetchHomeImageURL = new FetchImageURL();
                            fetchHomeImageURL.execute(HomeTeamID);
                        }
                        logoCursor.close();
                    }

                    AwayTeamID = match_data.getJSONObject(LINKS).getJSONObject(AWAY_TEAM_URL).getString("href");
                    AwayTeamID = AwayTeamID.replace(TEAM_LINK_URL, "");

                    if(Utilies.getTeamCrestByTeamName(Away) == R.drawable.no_icon) { //icon not present in static images
                        String[] imageProjection = new String[]{DatabaseContract.team_table.TEAM_LOGO_COL};
                        Cursor logoCursor = mContext.getContentResolver().query(DatabaseContract.team_table.buildTeamWithTeamID(),
                                imageProjection,null,new String[]{AwayTeamID},null);
                        if(logoCursor.getCount() == 0) { //if the logo image URL not found in the database
                            FetchImageURL fetchHomeImageURL = new FetchImageURL();
                            fetchHomeImageURL.execute(AwayTeamID);
                        }
                        logoCursor.close();
                    }

                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID,match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL,mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL,mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL,Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL,Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL,Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL,Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL, League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY, match_day);
                    match_values.put(DatabaseContract.scores_table.HOME_ID_COL, HomeTeamID);
                    match_values.put(DatabaseContract.scores_table.AWAY_ID_COL, AwayTeamID);

                    String[] projection = new String[] {DatabaseContract.league_table.LEAGUE_ID_COL, DatabaseContract.league_table.LEAGUE_NAME_COL};
                    Cursor retCursor = mContext.getContentResolver().query(DatabaseContract.league_table.buildScoreWithLeagueID(),
                                                                            projection,null,new String[]{League},null);

                    if (retCursor.getCount() == 0) { //league id/league name not in the DB
                        FetchLeagueName fetchLeagueTask = new FetchLeagueName();
                        fetchLeagueTask.execute(League);
                    }
                    else {
                        retCursor.moveToFirst();
                        int columnCount = retCursor.getColumnCount();
                        String columnNames[] = retCursor.getColumnNames();
                        int leagueNameIdx = retCursor.getColumnIndex(columnNames[1]);
                        String leagueName = retCursor.getString(leagueNameIdx);

                    }
                    retCursor.close();

                    values.add(match_values);
                }
            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI,insert_data);

            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    private class FetchImageURL extends AsyncTask<String, Void, JSONObject> {

        private final String TAG = FetchImageURL.class.getSimpleName();
        private final String TEAM_LINK_URL = "http://api.football-data.org/alpha/teams/";

        @Override
        protected JSONObject doInBackground(String... params) {

            BufferedReader reader = null;
            String teamURL = TEAM_LINK_URL + params[0];
            String teamJSONStr = null;
            URL fetch = null;

            try {
                fetch = new URL(teamURL);

                HttpURLConnection m_connection = (HttpURLConnection) fetch.openConnection();
                m_connection.setRequestMethod("GET");
                m_connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key) );
                m_connection.connect();

                // Read the input stream into a String
                InputStream inputStream = m_connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                if(buffer.length() == 0) {
                    //Stream is empty. So no need to parse.
                    Log.e(TAG,"load network buffer is null");

                    return null;
                }
                teamJSONStr = buffer.toString();
                JSONObject teamJObj = new JSONObject(teamJSONStr);
                return teamJObj;
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject teamJObj) {
            final String LINKS = "_links";
            if (teamJObj == null) {
                return;
            } else {
                try {
                    String teamID = teamJObj.getJSONObject(LINKS).getJSONObject("self")
                                            .getString("href");
                    teamID = teamID.replace(TEAM_LINK_URL,"");
                    String teamURL = teamJObj.getString("crestUrl");
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.team_table.TEAM_ID_COL,teamID);
                    values.put(DatabaseContract.team_table.TEAM_LOGO_COL, teamURL);
                    Log.d(TAG,"teamId:" + teamID+" teamURL:"+teamURL);
                    Uri insertedUri = getApplicationContext().getContentResolver().insert(
                                                DatabaseContract.team_table.buildTeamWithTeamID(),values);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

     private class FetchLeagueName extends AsyncTask<String, Void, JSONObject> {
         private final String TAG = FetchLeagueName.class.getSimpleName();


         @Override
         protected JSONObject doInBackground(String... params) {

             final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
             BufferedReader reader = null;
             String leagueURL = SEASON_LINK + params[0];
             String leagueJSONStr = null;
             URL fetch = null;
             try {
                 fetch = new URL(leagueURL);

                 HttpURLConnection m_connection = (HttpURLConnection) fetch.openConnection();
                 m_connection.setRequestMethod("GET");
                 m_connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
                 m_connection.connect();

                 // Read the input stream into a String
                 InputStream inputStream = m_connection.getInputStream();
                 StringBuffer buffer = new StringBuffer();
                 if (inputStream == null) {
                     // Nothing to do.
                     return null;
                 }
                 reader = new BufferedReader(new InputStreamReader(inputStream));
                 String line;
                 while ((line = reader.readLine()) != null) {
                     buffer.append(line);
                 }
                 if(buffer.length() == 0) {
                     //Stream is empty. So no need to parse.
                     Log.e(TAG,"load network buffer is null");

                     return null;
                 }
                 leagueJSONStr = buffer.toString();
                 JSONObject leagueJObj = new JSONObject(leagueJSONStr);
                 return leagueJObj;

            } catch (Exception e) {
                 e.printStackTrace();
                 return null;
            }
     }

     @Override
     protected void onPostExecute(JSONObject leagueJObj) {

         if(leagueJObj == null)
             return;
         try {
             final String LINKS = "_links";
             final String SOCCER_SEASON = "soccerseason";
             final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
             String leagueName = leagueJObj.getString("caption");
             String leagueID = leagueJObj.getJSONObject(LINKS).getJSONObject("self").
                     getString("href");
             leagueID = leagueID.replace(SEASON_LINK,"");
             ContentValues values = new ContentValues();
             values.put(DatabaseContract.league_table.LEAGUE_ID_COL, leagueID);
             values.put(DatabaseContract.league_table.LEAGUE_NAME_COL, leagueName);
             Log.d(TAG, "Inserting League id:" + leagueID + " leagueName:" + leagueName);
             Uri insertedUri = getApplicationContext().getContentResolver().insert(DatabaseContract.league_table.buildScoreWithLeagueID(),values);
             Log.d(TAG,"Uri:"+insertedUri);
             return;

         } catch (JSONException e) {
             e.printStackTrace();
         }
     }
 }
}

