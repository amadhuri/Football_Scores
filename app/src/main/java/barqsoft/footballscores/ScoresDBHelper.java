package barqsoft.footballscores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.DatabaseContract.scores_table;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 7;

    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CreateScoresTable = "CREATE TABLE " + DatabaseContract.SCORES_TABLE + " ("
                + scores_table._ID + " INTEGER PRIMARY KEY,"
                + scores_table.DATE_COL + " TEXT NOT NULL,"
                + scores_table.TIME_COL + " INTEGER NOT NULL,"
                + scores_table.HOME_COL + " TEXT NOT NULL,"
                + scores_table.AWAY_COL + " TEXT NOT NULL,"
                + scores_table.LEAGUE_COL + " INTEGER NOT NULL,"
                + scores_table.HOME_GOALS_COL + " TEXT NOT NULL,"
                + scores_table.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + scores_table.MATCH_ID + " INTEGER NOT NULL,"
                + scores_table.MATCH_DAY + " INTEGER NOT NULL,"
                + scores_table.HOME_ID_COL + " INTEGER NOT NULL,"
                + scores_table.AWAY_ID_COL + " INTEGER NOT NULL,"
                + " UNIQUE ("+scores_table.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);

        final String CreateLeagueTable = "CREATE TABLE " + DatabaseContract.LEAGUE_TABLE + " ("
                + DatabaseContract.league_table._ID+ " INTEGER PRIMARY KEY,"
                + DatabaseContract.league_table.LEAGUE_ID_COL + " INTEGER NOT NULL,"
                + DatabaseContract.league_table.LEAGUE_NAME_COL + " TEXT NOT NULL,"
                + " UNIQUE ("+DatabaseContract.league_table.LEAGUE_ID_COL+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateLeagueTable);

        final String CreateTeamTable = "CREATE TABLE " + DatabaseContract.TEAM_TABLE + " ("
                + DatabaseContract.team_table._ID + " INTEGER PRIMARY KEY,"
                + DatabaseContract.team_table.TEAM_ID_COL + " INTEGER NOT NULL,"
                + DatabaseContract.team_table.TEAM_LOGO_COL + " TEXT NOT NULL,"
                + " UNIQUE ("+DatabaseContract.team_table.TEAM_ID_COL + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateTeamTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.LEAGUE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TEAM_TABLE);
    }
}
