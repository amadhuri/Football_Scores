package barqsoft.footballscores;

import android.util.Log;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;
    public static final int LIGUE_2=397;
    public static final int PREMIER_LEAGUE2 = 398;
    public static final int PRIMERA_DIV = 399;
    public static final int SERIES_A = 401;
    private static final String TAG = Utilies.class.getSimpleName();

    public static String getLeague(int league_num)
    {
        switch (league_num)
        {
            case SERIE_A :
            case SERIES_A :  return "Seria A";
            case PREMIER_LEGAUE : return "Premier League";
            case CHAMPIONS_LEAGUE : return "UEFA Champions League";
            case PRIMERA_DIVISION : return "Primera Division";
            case BUNDESLIGA : return "Bundesliga";
            case LIGUE_2 : return "Ligue 2";
            case PREMIER_LEAGUE2 : return "Premier League";
            case PRIMERA_DIV : return "Primera Division";
            default: return String.format("League:"+league_num);
        }
    }
    public static String getMatchDay(int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        Log.d(TAG,"getTeamCrestByTeamName:"+teamname);
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.

            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Arsenal FC" : return R.drawable.arsenal;
            case "Aston Villa FC" : return R.drawable.aston_villa;

            case "Burnley F.C." : return R.drawable.burney_fc_hd_logo;

            case "Chelsea FC" : return R.drawable.chelsea;
            case "Crystal Palace FC" : return R.drawable.crystal_palace_fc;

            case "Everton FC" : return R.drawable.everton_fc_logo1;

            case "1. FC Union Berlin" : return R.drawable.fc_union_berlin_logo;
            case "Fortuna Düsseldorf" : return R.drawable.fortuna_dusseldorf;
            case "FSV Frankfurt" : return R.drawable.fsv_frankfurt;

            case "Granada CF" : return R.drawable.granada_cf;
            case "Hull City A.F.C" : return R.drawable.hull_city_afc_hd_logo;

            case "Karlsruher SC" : return R.drawable.ksc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Leicester City FC" : return R.drawable.leicester_city_fc_hd_logo;
            case "Liverpool FC" : return R.drawable.liverpool;

            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Manchester City FC" : return R.drawable.manchester_city;

            case "Newcastle United FC" : return R.drawable.newcastle_united;

            case "Queens Park Rangers F.C." : return R.drawable.queens_park_rangers_hd_logo;

            case "Rayo Vallecano de Madrid": return R.drawable.rayo_vallecano_logo;
            case "RCD Espanyol" : return R.drawable.rcd_espanyol_logo;
            case "Real Betis" : return R.drawable.real_betis_logo;
            case "Red Bull Leipzig" : return R.drawable.rb_leipzig;

            case "SC Paderborn 07" : return R.drawable.sc_paderborn_07;
            case "SC Freiburg" : return R.drawable.sc_freiburg;
            case "Southampton FC" : return R.drawable.southampton_fc;
            case "SpVgg Greuther Fürth" : return R.drawable.spvgg_greuther_furth;
            case "SS Lazio" : return R.drawable.ss_lazio;
            case "Stoke City FC" : return R.drawable.stoke_city;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Swansea City" : return R.drawable.swansea_city_afc;

            case "Torino FC" : return R.drawable.torino_fc;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "Udinese Calcio" : return R.drawable.udinese_calcio;
            case "Valencia CF" : return R.drawable.valenciacf;

            case "West Ham United FC" : return R.drawable.west_ham;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            default: return R.drawable.no_icon;
        }
    }

    public static String getFileExtension(String fileName) {
        try {
            //fileName = "mad.png";
            int index = fileName.lastIndexOf(".");
            String ext = fileName.substring(index+1);
            return ext;
        } catch (Exception e) {
            Log.d(TAG,"getFileExtension exception :"+e.getMessage() );
            return "";
        }
    }
}
