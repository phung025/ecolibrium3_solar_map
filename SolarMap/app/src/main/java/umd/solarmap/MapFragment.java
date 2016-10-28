package umd.solarmap;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * To create a fragment, extend the Fragment class, then override key lifecycle methods to insert your app logic, similar to the way you would with an Activity class.
 * One difference when creating a Fragment is that you must use the onCreateView() callback to define the layout. In fact, this is the only callback you need in order
 * to get a fragment running.
 */
public class MapFragment extends Fragment {

    public static final int DEFAULT_MAP_ZOOM = 7;

    private MapView mainMapView;

    private EditText searchTextField;
    private FloatingActionButton searchButton;
    private FloatingActionButton toCurrentLocationButton;
    private LocatorTask locatorTask;
    private GeocodeParameters geocodeParams;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        locatorTask = new LocatorTask(getString(R.string.geocode));

        mainMapView = (MapView) getActivity().findViewById(R.id.mainMapView);
        Basemap basemap = Basemap.createImagery();
        ArcGISMap map = new ArcGISMap(basemap);
        Viewpoint vp = new Viewpoint(46.7867, -92.1005, 72223.819286);
        map.setInitialViewpoint(vp);
        mainMapView.setMap(map);

        ArcGISVectorTiledLayer insol_dlh_annovtpk = new ArcGISVectorTiledLayer(getString(R.string.insol_dlh_annovtpk));
        map.getOperationalLayers().add(insol_dlh_annovtpk);

        searchTextField = (EditText) getActivity().findViewById(R.id.locationSearchTextField);
        searchButton = (FloatingActionButton) getActivity().findViewById(R.id.locationSearchActionButton);
        toCurrentLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.toCurrentLocationButton);
        geocodeParams = new GeocodeParameters();
        geocodeParams.setCountryCode("United States");

        setupMap();
        setupTextField();
        setupButtons();
    }

    private void setupMap() {
        // Set a listener that will be called when the MapView is initialized.
        // Check if the map is ready for a user to make queries.
        //region mainMapView.OnStatusChangedListener()

        mainMapView.addDrawStatusChangedListener(drawStatusChangedEvent -> {
            // Enable the search text field only when the map is initialized
            searchTextField.setEnabled(true);

            mainMapView.getLocationDisplay().setAutoPanMode(LocationDisplay.AutoPanMode.OFF); //changed from LocationDisplayManager.AutoPanMode.LOCATION
            mainMapView.getLocationDisplay().addLocationChangedListener(locationChangedEvent -> {
                Point currentLocationPoint = locationChangedEvent.getSource().getLocation().getPosition();
            });
            mainMapView.getLocationDisplay().setShowLocation(true);
            mainMapView.getLocationDisplay().startAsync();
        });
        mainMapView.setOnLongClickListener(view -> {
            Toast.makeText(getContext(), "What took you so long?", Toast.LENGTH_LONG).show();
            // Return true that the listener has consumed the event
            return true;
        });
    }

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
            if (!address.equals("")){
                final ListenableFuture<List<GeocodeResult>> geocodeFuture = locatorTask.geocodeAsync(address, geocodeParams);

                geocodeFuture.addDoneListener(() -> {
                    try {
                        List<GeocodeResult> geocodeResults = geocodeFuture.get();

                        // Use the first result - for example display on the map
                        GeocodeResult topResult = geocodeResults.get(0);
                        mainMapView.setViewpointCenterAsync(topResult.getDisplayLocation());
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }

        });

        /**
         * Action when clicking go to current location button
         */
        //region onClickedToCurrentLocationButton()
        toCurrentLocationButton.setOnClickListener(view -> {

            Point currentLocationPoint = mainMapView.getLocationDisplay().getLocation().getPosition();

            // If the current location is detected
            if (currentLocationPoint != null) {
                // Zoom the map to current location
                mainMapView.setViewpointCenterAsync(mainMapView.getLocationDisplay().getLocation().getPosition());
            } else {
                Toast.makeText(getContext(), getString(R.string.cantLocateCurrentPosition), Toast.LENGTH_LONG).show();
            }
        });
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
}




