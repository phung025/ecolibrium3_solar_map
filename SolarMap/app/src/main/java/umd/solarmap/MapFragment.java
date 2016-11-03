package umd.solarmap;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.popup.Popup;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
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

    /**
     * Instance field
     */
    private MapView mainMapView;
    private EditText searchTextField;
    private FloatingActionButton searchButton;
    private FloatingActionButton toCurrentLocationButton;
    private AlertDialog locationActionDialog;
    private Popup popup; // Will display rooftop layer inforamtion

    // Map Components
    private LocatorTask locatorTask;
    private GeocodeParameters geocodeParams;

    // Map Layers
    private ServiceFeatureTable mServiceFeatureTable;
    private FeatureLayer mFeaturelayer;                 // Rooftop layer
    private ArcGISVectorTiledLayer insol_dlh_annovtpk;  // Rooftop solar energy layer
    private ArcGISTiledLayer raw_solar;                 // Raw solar energy image layer

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        locatorTask = new LocatorTask(getString(R.string.geocode));

        mainMapView = (MapView) getActivity().findViewById(R.id.mainMapView);
        searchTextField = (EditText) getActivity().findViewById(R.id.locationSearchTextField);
        searchButton = (FloatingActionButton) getActivity().findViewById(R.id.locationSearchActionButton);
        toCurrentLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.toCurrentLocationButton);

        (geocodeParams = new GeocodeParameters()).setCountryCode("United States");

        this.setupMap();
        this.setupTextField();
        this.setupButtons();
        this.setupOtherComponents();

        Basemap basemap = Basemap.createImagery();
        ArcGISMap map = new ArcGISMap(basemap);

        // Setting initial view point of the map
        Viewpoint vp = new Viewpoint(46.7867, -92.1005, 72223.819286);
        map.setInitialViewpoint(vp);

        // Setup map layers
        mFeaturelayer = new FeatureLayer(mServiceFeatureTable = new ServiceFeatureTable(getString(R.string.foot_dlh_5k)));
        mFeaturelayer.setSelectionColor(Color.rgb(0, 255, 255)); //cyan, fully opaque
        mFeaturelayer.setSelectionWidth(3);
        insol_dlh_annovtpk = new ArcGISVectorTiledLayer(getString(R.string.insol_dlh_annovtpk));
        raw_solar = new ArcGISTiledLayer(getString(R.string.raw_solar)); // <--- Layer doesnt work and probably isnt even supported

        map.getOperationalLayers().add(insol_dlh_annovtpk);
        map.getOperationalLayers().add(mFeaturelayer);
        //map.getOperationalLayers().add(raw_solar); Doesn't display

        mainMapView.setMap(map);

        // Listener for selecting a feature.
        // Why doesn't this work in the context of our current project?
        mainMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mainMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                Point clickPoint = mainMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                int tolerance = 40;
                double mapTolerance = tolerance * mainMapView.getUnitsPerPixel();

                // create objects required to do a selection with a query
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, map.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);

                // popup constructor requires GeoElement.
                //popup = new Popup(getContext(), GeoElement);

                // select features
                final ListenableFuture<FeatureQueryResult> future = mFeaturelayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                // launches after a selection
                future.addDoneListener(() -> {
                    try {
                        //call get on the future to get the result
                        FeatureQueryResult result = future.get();

                        //find out how many items there are in the result
                        int i = 0;
                        for (; result.iterator().hasNext(); ++i) {
                            result.iterator().next();
                        }

                    } catch (Exception e1) {
                        Log.e(getResources().getString(R.string.app_name), "Failed: " + e1.getMessage());
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
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

    /**
     * Setup other components beside buttons & text fields
     */
    private void setupOtherComponents() {

        // Setup the dialog for asking user to share or save location
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] optionsTitle = {"Share this place", "Save this place"};
        builder.setTitle("Set Location");
        builder.setItems(optionsTitle, (dialog, which) -> {

        });
        this.locationActionDialog = builder.create();
    }
}
