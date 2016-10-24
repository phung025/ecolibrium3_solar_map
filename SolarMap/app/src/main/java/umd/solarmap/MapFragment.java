package umd.solarmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;

import java.util.List;

/**
 * To create a fragment, extend the Fragment class, then override key lifecycle methods to insert your app logic, similar to the way you would with an Activity class.
 * One difference when creating a Fragment is that you must use the onCreateView() callback to define the layout. In fact, this is the only callback you need in order
 * to get a fragment running.
 */

public class MapFragment extends Fragment {

    //region Instance Field
    public static final int DEFAULT_MAP_ZOOM = 7;

    // UI Components
    private MapView mainMapView;
    private EditText searchTextField;
    private FloatingActionButton searchButton;
    private FloatingActionButton toCurrentLocationButton;

    // arcGIS Location Display Manager
    LocationDisplayManager mapLocationDisplayManager;

    // Arguments for the location retrieved from searching
    private GraphicsLayer queryLayer;
    private Point locationLayerPoint;
    private String locationLayerPointString;

    //endregion

    /**
     * This function is automatically called to inflate the fragment from an activity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        // Get the map view.
        // NOTE: This only works after onCreateView() (i.e onActivityCreated())
        mainMapView = (MapView) getActivity().findViewById(R.id.mainMapView);
        queryLayer = new GraphicsLayer();
        mainMapView.addLayer(queryLayer);

        mainMapView.addLayer(new ArcGISImageServiceLayer(
                "https://gis.uspatial.umn.edu/arcgis/rest/services/solar/mn_solar/ImageServer",null));

        // Get the search text field & search button
        searchTextField = (EditText) getActivity().findViewById(R.id.locationSearchTextField);
        searchButton = (FloatingActionButton) getActivity().findViewById(R.id.locationSearchActionButton);
        toCurrentLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.toCurrentLocationButton);

        this.setupMap();
        this.setupTextField();
        this.setupButtons();
    }


    //region Setup the UI Components Action, etc

    /**
     * Set up all actions associated with the map view
     */
    private void setupMap() {

        // Set a listener that will be called when the MapView is initialized.
        // Check if the map is ready for a user to make queries.
        //region mainMapView.OnStatusChangedListener()
        mainMapView.setOnStatusChangedListener((source, status) -> {

            if ((source == mainMapView) && (status == OnStatusChangedListener.STATUS.INITIALIZED)) {
                // Enable the search text field only when the map is initialized
                searchTextField.setEnabled(true);

                // Get location display manager from arcGIS
                mapLocationDisplayManager = mainMapView.getLocationDisplayManager();
                mapLocationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                mapLocationDisplayManager.setLocationListener(new LocationListener() {

                        @Override
                        public void onLocationChanged(Location location) {

                            // Get current location of the device
                            Point currentLocationPoint = new Point(location.getLatitude(), location.getLongitude());

                            // Zoom the map to the current location
                            //mainMapView.zoomTo(currentLocationPoint, MapFragment.DEFAULT_MAP_ZOOM);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {
                        }

                        @Override
                        public void onProviderEnabled(String s) {
                        }

                        @Override
                        public void onProviderDisabled(String s) {
                        }
                    });
                mapLocationDisplayManager.setShowLocation(true);
                mapLocationDisplayManager.start();
            }
        });
        //endregion

        // Set a listener that will be called when user press a point on
        // the map for too long.
        // This listener is implemented as an action when user touch a position
        // on the map for long time.
        // If that position corresponds to a marker, display options for that marker. (i.e remove marker)
        // Otherwise, display options such as mark location for interest or add to saved location
        //region mainMapView.setOnLongPressListener()
        mainMapView.setOnLongPressListener((x_coordinate, y_coordinate) -> {

            Toast.makeText(getContext(), "What took you so long?", Toast.LENGTH_LONG).show();
            // Return true that the listener has consumed the event
            return true;
        });
        //endregion
    }

    /**
     * Set up all actions associated with the buttons in this fragment
     */
    private void setupButtons() {

        /**
         * Action when clicking search location button
         */
        //region onClickedSearchButton()
        searchButton.setOnClickListener(view -> {

            // Hide the keyboard
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            String address = searchTextField.getText().toString();
            if (!address.equals(""))
                executeLocatorTask(address);
        });
        //endregion

        /**
         * Action when clicking go to current location button
         */
        //region onClickedToCurrentLocationButton()
        toCurrentLocationButton.setOnClickListener(view -> {

            Point currentLocationPoint = mapLocationDisplayManager.getPoint();

            // If the current location is detected
            if (currentLocationPoint != null) {

                // Zoom the map to current location
                mainMapView.zoomTo(mapLocationDisplayManager.getPoint(), MapFragment.DEFAULT_MAP_ZOOM);
            } else {
                Toast.makeText(getContext(), getString(R.string.cantLocateCurrentPosition), Toast.LENGTH_LONG).show();
            }
        });
        //endregion
    }

    /**
     * Set up all actions associated with the text fields in this fragment
     */
    private void setupTextField() {

        searchTextField.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == keyEvent.KEYCODE_ENTER) {
                // Start searching
                searchButton.performClick();
                return true;
            }
            return false;
        });
    }
    //endregion

    //region Functions & Background Thread for Executing Location Search
    private void executeLocatorTask(String address) {

        // Create Locator parameters from single line address string
        LocatorFindParameters findParams = new LocatorFindParameters(address);

        // Use the centre of the current map extent as the find location point
        findParams.setLocation(mainMapView.getCenter(), mainMapView.getSpatialReference());

        // Calculate distance for find operation
        Envelope mapExtent = new Envelope();
        mainMapView.getExtent().queryEnvelope(mapExtent);

        // assume map is in metres, other units wont work, double current envelope
        double distance = (mapExtent != null && mapExtent.getWidth() > 0) ? mapExtent.getWidth() * 2 : 10000;
        findParams.setDistance(distance);
        findParams.setMaxLocations(2);

        // Set address spatial reference to match map
        findParams.setOutSR(mainMapView.getSpatialReference());

        // Execute async task to find the address
        new LocatorAsyncTask().execute(findParams);
        locationLayerPointString = address; // Why do we need this?
    }
    private class LocatorAsyncTask extends AsyncTask<LocatorFindParameters, Void, List<LocatorGeocodeResult>> {
        private Exception mException;

        @Override
        protected List<LocatorGeocodeResult> doInBackground(LocatorFindParameters... params) {
            mException = null;
            List<LocatorGeocodeResult> results = null;
            Locator locator = Locator.createOnlineLocator();
            try {
                results = locator.find(params[0]);
            } catch (Exception e) {
                mException = e;
            }
            return results;
        }

        protected void onPostExecute(List<LocatorGeocodeResult> result) {
            if (mException != null) {
                Log.w("PlaceSearch", "LocatorSyncTask failed with:");
                mException.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.addressSearchFailed), Toast.LENGTH_LONG).show();
                return;
            }

            if (result.size() == 0) {
                Toast.makeText(getContext(), getString(R.string.noResultsFound), Toast.LENGTH_LONG).show();
            } else {

                // Use first result in the list
                LocatorGeocodeResult geocodeResult = result.get(0);

                // get return geometry from geocode result
                Point resultPoint = geocodeResult.getLocation();

                // create marker symbol to represent location
                PictureMarkerSymbol resultSymbol = new PictureMarkerSymbol(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.query_location_marker));
                //SimpleMarkerSymbol resultSymbol = new SimpleMarkerSymbol(Color.RED, 16, SimpleMarkerSymbol.STYLE.CROSS);

                // create graphic object for resulting location
                Graphic resultLocGraphic = new Graphic(resultPoint, resultSymbol);

                // Remove all previous location search markers in the query layer
                queryLayer.removeAll();

                // add graphic to location layer
                queryLayer.addGraphic(resultLocGraphic);

                // create text symbol for return address
                String address = geocodeResult.getAddress();
                TextSymbol resultAddress = new TextSymbol(20, address, Color.BLACK);
                // create offset for text
                resultAddress.setOffsetX(-4 * address.length());
                resultAddress.setOffsetY(10);
                // create a graphic object for address text
                Graphic resultText = new Graphic(resultPoint, resultAddress);

                // add address text graphic to location graphics layer
                queryLayer.addGraphic(resultText);

                locationLayerPoint = resultPoint;

                // Zoom map to geocode result location
                mainMapView.zoomToResolution(geocodeResult.getLocation(), MapFragment.DEFAULT_MAP_ZOOM);
            }
        }
    }
    //endregion
}
