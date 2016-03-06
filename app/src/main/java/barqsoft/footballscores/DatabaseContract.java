package barqsoft.footballscores;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract
{
    public static final String SCORES_TABLE = "scores_table";
    public static final String LEAGUE_TABLE = "league_table";
    public static final String TEAM_TABLE = "team_table";

    public static final class scores_table implements BaseColumns
    {
        //Table data
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";
        public static final String HOME_ID_COL = "home_url";
        public static final String AWAY_ID_COL = "away_url";

        //public static Uri SCORES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH)
                //.build();

        //Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static Uri buildScoreWithLeague()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
        }
        public static Uri buildScoreWithId()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }
        public static Uri buildScoreWithDate()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }
    //URI data
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final String PATH = "scores";
    public static final String LEAGUE_PATH = "league";
    public static final String TEAM_PATH = "team";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class league_table implements BaseColumns {

        //Table data
        public static final String LEAGUE_ID_COL = "league_ID";
        public static final String LEAGUE_NAME_COL = "league_name";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + LEAGUE_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + LEAGUE_PATH;
        public static Uri buildScoreWithLeagueID()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("leagueid").build();
        }
    }

    public static final class team_table implements BaseColumns {

        //Table data
        public static final String TEAM_ID_COL = "team_ID";
        public static final String TEAM_LOGO_COL = "team_logo";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TEAM_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TEAM_PATH;
        public static Uri buildTeamWithTeamID() {
            return BASE_CONTENT_URI.buildUpon().appendPath("teamid").build();
        }
    }
}