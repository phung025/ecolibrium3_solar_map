package umd.solarmap;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.FeatureTable;
import com.esri.arcgisruntime.datasource.Field;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
    private FloatingActionButton toCurrentLocationButton;
    private android.app.AlertDialog locationActionDialog;

    // Map Components
    private ArcGISMap mainMap;
    private LocatorTask locatorTask;
    private GeocodeParameters geocodeParams;

    // Map Layers
//    private ServiceFeatureTable mServiceFeatureTable;
//    private FeatureLayer mFeaturelayer;                 // Rooftop layer
    private ArcGISVectorTiledLayer insol_dlh_annovtpk;  // Rooftop solar energy layer
//    private ArcGISTiledLayer raw_solar;                 // Raw solar energy image layer

    // Map's graphic overlay for putting markers
    private GraphicsOverlay mapMarkersOverlay;

    private Callout mCallout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        locatorTask = new LocatorTask(getString(R.string.geocode));
        insol_dlh_annovtpk = new ArcGISVectorTiledLayer(getString(R.string.insol_dlh_annovtpk));

        mainMapView = (MapView) getActivity().findViewById(R.id.mainMapView);
        searchTextField = (EditText) getActivity().findViewById(R.id.locationSearchTextField);
        toCurrentLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.toCurrentLocationButton);

        (geocodeParams = new GeocodeParameters()).setCountryCode("United States");

        mainMapView.getGraphicsOverlays().add(mapMarkersOverlay = new GraphicsOverlay()); // Add the overlay for displaying markers to the map

        // Setting initial view point of the map
        Viewpoint vp = new Viewpoint(46.7867, -92.1005, 72223.819286);
        (mainMap = new ArcGISMap("http://umn.maps.arcgis.com/home/item.html?id=53151b88aa124cf09d5a58c02bfe5a33")).setInitialViewpoint(vp);

        this.setupMap();
        this.setupTextField();
        this.setupButtons();
        this.setupOtherComponents();

        //sets the base map
        mainMapView.setMap(mainMap);

        //Gets the callout
        mCallout = mainMapView.getCallout();

        // Listener for selecting a feature.
        mainMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mainMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // remove any existing callouts
                if(mCallout.isShowing()){
                    mCallout.dismiss();
                }
                // User click point
                final Point clickPoint = mainMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                // Map tolerance
                int tolerance = 10;
                double mapTolerance = tolerance * mainMapView.getUnitsPerPixel();
                // Query envelope
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mainMap.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);
                // Gets feature attributes. Change made HERE, making the select feature call on the service is incorrect.
                final ListenableFuture<FeatureQueryResult> future = ((FeatureLayer)mainMap.getOperationalLayers().get(2)).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);

                future.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Result
                            FeatureQueryResult result = future.get();

                            Iterator<Feature> iterator = result.iterator();
                            // create a TextView to display field values
                            TextView calloutContent = new TextView(getContext());
                            // Sets textView setting
                            calloutContent.setTextColor(Color.BLACK);
                            calloutContent.setSingleLine(false);
                            calloutContent.setVerticalScrollBarEnabled(true);
                            calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                            calloutContent.setMovementMethod(new ScrollingMovementMethod());
                            calloutContent.setLines(5);

                            int counter = 0;
                            Feature feature;
                            while (iterator.hasNext()){
                                feature = iterator.next();
                                // create a Map of all available attributes as name value pairs
                                Map<String, Object> attr = feature.getAttributes();
                                Set<String> keys = attr.keySet();
                                for(String key:keys){
                                    Object value = attr.get(key);
                                    // format observed field value as date
                                    if(value instanceof GregorianCalendar){
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                        value = simpleDateFormat.format(((GregorianCalendar) value).getTime());
                                    }
                                    // append name value pairs to TextView
                                    calloutContent.append(key + " | " + value + "\n");
                                }
                                counter++;
                                // center the mapview on selected feature
                                Envelope envelope = feature.getGeometry().getExtent();
                                mainMapView.setViewpointGeometryWithPaddingAsync(envelope, 200);
                                // callout display
                                mCallout.setLocation(clickPoint);
                                mCallout.setContent(calloutContent);
                                mCallout.show();
                            }
                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                        }
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
            mainMapView.getLocationDisplay().setShowLocation(true);
            mainMapView.getLocationDisplay().startAsync();

        });

        // Change web map's insol_dlh_annovtpk layer to our own insol_dlh_annovtpk layer
        mainMap.addDoneLoadingListener(() -> {

            if ((mainMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) || (mainMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD.NOT_LOADED)) {
                System.out.println("MAP FAILED TO LOAD");

            } else if ((mainMap.getLoadStatus() == LoadStatus.LOADED)) {


                for (Layer layer : mainMap.getOperationalLayers()) {

                    System.out.println(layer.getName() + layer.getId());
                }

                mainMap.getOperationalLayers().set(0, insol_dlh_annovtpk);
            }
        });

        // Customer touch event listener class for the map view
        // Override other touch action events in here if you want to implement
        // new action
        class CustomMapViewTouchListener extends DefaultMapViewOnTouchListener {

            public CustomMapViewTouchListener(Context context, MapView mapView) {
                super(context, mapView);
            }

            @Override
            public void onLongPress(MotionEvent event) {



                // NOTE: This function need to check if the user touched a marker or just a location

                /*
                // Display the dialog
                Point clickPoint = mainMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                SimpleMarkerSymbol marker = new SimpleMarkerSymbol();

                //final PictureMarkerSymbol marker = new PictureMarkerSymbol(String.valueOf(R.drawable.query_location_marker));
                Graphic selectedLocationGraphic = new Graphic(clickPoint, marker);

                mapMarkersOverlay.getGraphics().add(selectedLocationGraphic);
                */
                locationActionDialog.show();
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                // get the point that was clicked and convert it to a point in map coordinates
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                int tolerance = 44;
                double mapTolerance = tolerance * mMapView.getUnitsPerPixel();

                // create objects required to do a selection with a query
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mainMap.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);

                // call select features
                final ListenableFuture<FeatureQueryResult> future = ((FeatureLayer)mainMap.getOperationalLayers().get(0)).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                // add done loading listener to fire when the selection returns
                future.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //call get on the future to get the result
                            FeatureQueryResult result = future.get();

                            //find out how many items there are in the result
                            Iterator<Feature> result_iterator = result.iterator();
                            while (result_iterator.hasNext()) {

                                Feature current_building_feature = result.iterator().next();
                                FeatureTable selected_building_feature_table = current_building_feature.getFeatureTable();
                                   Iterator<Field> all_fields_of_selected_building = selected_building_feature_table.getFields().iterator();
                                System.out.println("Building ID: " + current_building_feature.getAttributes().toString());
                                System.out.println("Table feature name: " + selected_building_feature_table.getTableName());
                                System.out.println("Total features count: " + selected_building_feature_table.getTotalFeatureCount());
                                System.out.println("All fields of this feature table: ");
                                while (all_fields_of_selected_building.hasNext()) {
                                    Field current_field = all_fields_of_selected_building.next();
                                    System.out.println("Name: " + current_field.getName() +
                                            " | Alias: " + current_field.getAlias() +
                                            " | Domain: " + current_field.getDomain() +
                                            " | Field type: " + current_field.getFieldType().toString() +
                                            " | Length: " + current_field.getLength());
                                }

                                System.out.println("\n\n");
                            }

                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }

        }
        mainMapView.setOnTouchListener(new CustomMapViewTouchListener(getContext(), mainMapView));
    }

    private void setupButtons() {

        /**
         * Action when clicking go to current location button
         */
        //region onClickedToCurrentLocationButton()
        toCurrentLocationButton.setOnClickListener(view -> {
            Point currentLocationPoint = mainMapView.getLocationDisplay().getLocation().getPosition();

            // If the current location is detected
            if (currentLocationPoint != null) {
                // Zoom the map to current location
                Viewpoint vp  = new Viewpoint(mainMapView.getLocationDisplay().getLocation().getPosition(), 4000.0);
                mainMapView.setViewpointAsync(vp);
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
                // Hide the keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                String address = searchTextField.getText().toString();
                if (!(address.length() == 0)) {
                    final ListenableFuture<List<GeocodeResult>> geocodeFuture = locatorTask.geocodeAsync(address, geocodeParams);

                    geocodeFuture.addDoneListener(() -> {
                        try {
                            List<GeocodeResult> geocodeResults = geocodeFuture.get();

                            // Use the first result - for example display on the map
                            if (!geocodeResults.isEmpty()) {
                                GeocodeResult topResult = geocodeResults.get(0);
                                mainMapView.setViewpointCenterAsync(topResult.getDisplayLocation());
                            } else {
                                Toast.makeText(getContext(), getString(R.string.cantFindLocation), Toast.LENGTH_LONG).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    });
                }

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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        String[] optionsTitle = {"Share this place", "Save this place"};
        builder.setTitle("Set Location");
        builder.setItems(optionsTitle, (dialog, which) -> {

        });
        this.locationActionDialog = builder.create();
    }
}
