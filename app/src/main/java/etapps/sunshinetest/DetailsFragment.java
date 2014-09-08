package etapps.sunshinetest;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import etapps.sunshinetest.data.WeatherContract.LocationEntry;
import etapps.sunshinetest.data.WeatherContract.WeatherEntry;

/**
 * Created by emanuele on 7/09/14.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private static final String LOCATION_KEY = "location";
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int DETAIL_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
// This works because the WeatherProvider returns location data joined with
// weather data, even though they're stored in two different tables.
            LocationEntry.COLUMN_LOCATION_SETTING
    };
    //indexes for views
    public ImageView iconView;
    public TextView dateView;
    public TextView dayView;
    public TextView descriptionView;
    public TextView highTempView;
    public TextView lowTempView;
    public TextView humidityView;
    public TextView windView;
    public TextView pressureView;
    private ShareActionProvider mShareActionProvider;
    private String mLocation;
    private String mForecast;
    private String mDateStr;

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailsActivity.DATE_KEY) &&
                mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mDateStr = arguments.getString(DetailsActivity.DATE_KEY);
        }
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        iconView = (ImageView) view.findViewById(R.id.detail_image_icon);
        dateView = (TextView) view.findViewById(R.id.detail_date_textview);
        dayView = (TextView) view.findViewById(R.id.detail_day_textview);
        descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        windView = (TextView) view.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.share_action_menu);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailsActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order: Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDate(
                mLocation, mDateStr);
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read date from cursor and update views for day of week and date
            String date = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_DATETEXT));
            String dateString = Utility.formatDate(date);
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            dayView.setText(friendlyDateText);
            dateView.setText(dateText);

            int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));
            iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            // set the description
            String weatherDescription =
                    data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
            descriptionView.setText(weatherDescription);
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(getActivity(),
                    data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
            highTempView.setText(high);
            String low = Utility.formatTemperature(getActivity(),
                    data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), isMetric);

            lowTempView.setText(low);
            String wind = Utility.getFormattedWind(getActivity(),
                    data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)),
                    data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES)));
            windView.setText(wind);
            // Read pressure from cursor and update view
            float pressure = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
            pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
            // Read humidity from cursor and update view
            float humidity = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
            humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
            iconView.setContentDescription(weatherDescription);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
