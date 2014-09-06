package etapps.sunshinetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

import etapps.sunshinetest.data.WeatherContract;

/**
 * Created by emanuele on 3/09/14.
 */
public class Utility {

    //private static final String PREFS_NAME = "user.prefs";

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.location_pref_key),
                context.getString(R.string.pref_default_location));
    }
    public static String getUnits(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.units_list_pref_key),
                context.getString(R.string.pref_units_metric));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.units_list_pref_key),
                context.getString(R.string.pref_units_metric_val))
                .equals(context.getString(R.string.pref_units_metric_val));
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = ((9*temperature)/5)+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }
    static String formatDate(String dateString) {
        Date date = WeatherContract.WeatherEntry.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

}
