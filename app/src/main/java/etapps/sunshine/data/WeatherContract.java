package etapps.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by emanuele on 1/09/14.
 */
public class WeatherContract {
    public final static String CONTENT_AUTHORITY = "etapps.sunshine";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String PATH_WEATHER = "weather";
    public final static String PATH_LOCATION = "location";

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String TABLE_NAME = "weather";        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
                // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";                // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";
        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";
        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";
        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";
        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
        private static final String DATE_FORMAT = "yyyyMMdd";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }

        public static String getdbDateString(Date date) {
            SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);
            return sdf.format(date);
        }

        public static Date getDateFromDb(String dateText) {
            SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                return dbDateFormat.parse(dateText);
            } catch ( ParseException e ) {
                e.printStackTrace();
                return null;
            }
        }







    }

    public static final class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String TABLE_NAME = "location";        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
                // The name of the city
        public static final String COLUMN_CITY_NAME = "city";                // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String COLUMN_COORD_LONG = "coord_long";
        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        public static Uri buildLocationbyId(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }







    }
}
