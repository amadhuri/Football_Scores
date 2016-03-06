package barqsoft.footballscores;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider
{
    private static ScoresDBHelper mOpenHelper;
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;
    private static final int LEAGUE_WITH_LEAGUE_ID = 104;
    private static final int TEAM_WITH_TEAM_ID = 105;

    private UriMatcher muriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder ScoreQuery =
            new SQLiteQueryBuilder();
    private static final String SCORES_BY_LEAGUE = DatabaseContract.scores_table.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE =
            DatabaseContract.scores_table.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID =
            DatabaseContract.scores_table.MATCH_ID + " = ?";
    private static final String LEAGUE_BY_LEAGUE_NAME = DatabaseContract.league_table.LEAGUE_ID_COL + " = ?";
    private static final String TEAM_BY_TEAM_ID = DatabaseContract.team_table.TEAM_ID_COL + " = ?";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();
        matcher.addURI(authority, null , MATCHES);
        matcher.addURI(authority, "league" , MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, "id" , MATCHES_WITH_ID);
        matcher.addURI(authority, "date" , MATCHES_WITH_DATE);
        matcher.addURI(authority, "leagueName", LEAGUE_WITH_LEAGUE_ID);
        matcher.addURI(authority, "teamURL", TEAM_WITH_TEAM_ID);
        return matcher;
    }

    private int match_uri(Uri uri)
    {
        String link = uri.toString();
        {
           if(link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString()))
           {
               return MATCHES;
           }
           else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithDate().toString()))
           {
               return MATCHES_WITH_DATE;
           }
           else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithId().toString()))
           {
               return MATCHES_WITH_ID;
           }
           else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithLeague().toString()))
           {
               return MATCHES_WITH_LEAGUE;
           }
           else if(link.contentEquals(DatabaseContract.league_table.buildScoreWithLeagueID().toString()))
           {
               return LEAGUE_WITH_LEAGUE_ID;
           }
           else if(link.contentEquals(DatabaseContract.team_table.buildTeamWithTeamID().toString()))
           {
               return TEAM_WITH_TEAM_ID;
           }
        }
        return -1;
    }
    @Override
    public boolean onCreate()
    {
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = muriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.scores_table.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case LEAGUE_WITH_LEAGUE_ID:
                return DatabaseContract.league_table.CONTENT_ITEM_TYPE;
            case TEAM_WITH_TEAM_ID:
                return DatabaseContract.team_table.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri );
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        //Log.v(FetchScoreTask.LOG_TAG,uri.getPathSegments().toString());
        int match = match_uri(uri);
        //Log.v(FetchScoreTask.LOG_TAG,SCORES_BY_LEAGUE);
        //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[0]);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(match));
        switch (match)
        {
            case MATCHES: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,null,null,null,null,sortOrder); break;
            case MATCHES_WITH_DATE:
                    //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[1]);
                    //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[2]);
                    retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,SCORES_BY_DATE,selectionArgs,null,null,sortOrder); break;
            case MATCHES_WITH_ID: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,SCORES_BY_ID,selectionArgs,null,null,sortOrder); break;
            case MATCHES_WITH_LEAGUE: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,SCORES_BY_LEAGUE,selectionArgs,null,null,sortOrder); break;
            case LEAGUE_WITH_LEAGUE_ID: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.LEAGUE_TABLE,
                    projection, LEAGUE_BY_LEAGUE_NAME, selectionArgs, null, null, sortOrder);break;
            case TEAM_WITH_TEAM_ID: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.TEAM_TABLE,
                    projection, TEAM_BY_TEAM_ID, selectionArgs, null, null, sortOrder);break;

            default: throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();


        switch (match_uri(uri))
        {
            case LEAGUE_WITH_LEAGUE_ID:
                long id = db.insert(DatabaseContract.LEAGUE_TABLE, null, values);
                if (id == -1)
                    return null;
                else
                    return ContentUris.withAppendedId(DatabaseContract.league_table.buildScoreWithLeagueID(), id);
            case TEAM_WITH_TEAM_ID:
                long rowId = db.insert(DatabaseContract.TEAM_TABLE, null, values);
                if (rowId == -1)
                    return null;
                else
                    return ContentUris.withAppendedId(DatabaseContract.team_table.buildTeamWithTeamID(), rowId);
        }
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //db.delete(DatabaseContract.SCORES_TABLE,null,null);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(muriMatcher.match(uri)));
        switch (match_uri(uri))
        {
            case MATCHES:
                db.beginTransaction();
                int returncount = 0;
                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returncount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}

