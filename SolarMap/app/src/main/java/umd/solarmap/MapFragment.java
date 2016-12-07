package umd.solarmap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import umd.solarmap.AccountManager.SolarAccountManager;
import umd.solarmap.DialogClasses.SolarRooftopDialog;
import umd.solarmap.RestAPI.HTTPAsyncTask;
import umd.solarmap.UtilitiesClasses.CallbackFunction;
import umd.solarmap.UtilitiesClasses.SolarProjectsXMLParser;

/**
 * To create a fragment, extend the Fragment class, then override key lifecycle methods to insert your app logic, similar to the way you would with an Activity class.
 * One difference when creating a Fragment is that you must use the onCreateView() callback to define the layout. In fact, this is the only callback you need in order
 * to get a fragment running.
 */
public class MapFragment extends Fragment {

    // Instance field
    private MapView mainMapView;
    private EditText searchTextField;
    private FloatingActionButton toCurrentLocationButton;

    // Map Components
    private ArcGISMap mainMap;
    private LocatorTask locatorTask;
    private GeocodeParameters geocodeParams;

    // Variable to populate the list of solar projects after it is retreived from xml on cleanprojects.org
    private List<SolarProject> installed_projects;

    // Map Layers
    private ArcGISVectorTiledLayer insol_dlh_annovtpk;  // Rooftop solar energy layer

    // Map's graphic overlay for putting markers
    private GraphicsOverlay interestedLocationsOverlay;
    private GraphicsOverlay installedProjectsOverlay;
    private HashMap<String, Integer> others_locations;

    // Callout to display rooftop information
    private Callout mCallout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        others_locations = new HashMap<>();

        installed_projects = new ArrayList<>();

        locatorTask = new LocatorTask(getString(R.string.geocode));
        insol_dlh_annovtpk = new ArcGISVectorTiledLayer(getString(R.string.insol_dlh_annovtpk));

        mainMapView = (MapView) getActivity().findViewById(R.id.mainMapView);
        searchTextField = (EditText) getActivity().findViewById(R.id.locationSearchTextField);
        toCurrentLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.toCurrentLocationButton);

        (geocodeParams = new GeocodeParameters()).setCountryCode("United States");
        interestedLocationsOverlay = new GraphicsOverlay();
        installedProjectsOverlay = new GraphicsOverlay();
        mainMapView.getGraphicsOverlays().add(interestedLocationsOverlay); // Add the overlay to display installed solar projects on the map
        mainMapView.getGraphicsOverlays().add(installedProjectsOverlay); // Add the overlay to display interested locations for having solar panel installed

        // Setting initial view point of the map: change viewpoint if getting from savedLocation fragment
        Bundle myBundle = this.getArguments();
        //get viewpoint based on the id received from the bundle
        String bundleId = null;

        Viewpoint vp = new Viewpoint(46.7867, -92.1005, 72223.819286);

        if(myBundle != null) bundleId= myBundle.getString("locationID","null");

        if(bundleId != null)
        {
            //set up query
            QueryParameters query = new QueryParameters();
            query.setReturnGeometry(true);
            query.setWhereClause("upper(OBJECTID) LIKE '%" + bundleId + "%'");

            //fire off query (async)
            try
            {
                FeatureQueryResult myResult = ((FeatureLayer) mainMap.getOperationalLayers().get(2)).getFeatureTable().queryFeaturesAsync(query).get();

                Point p = myResult.iterator().next().getGeometry().getExtent().getCenter();

                vp = new Viewpoint(p,5000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }

            //future.addDoneListener();
        }

        (mainMap = new ArcGISMap(getString(R.string.solar_potential_map_2))).setInitialViewpoint(vp);

        // Setup UI components
        this.setupMap();
        this.setupTextField();
        this.setupButtons();

        //sets the base map
        mainMapView.setMap(mainMap);

        //Gets the callout
        mCallout = mainMapView.getCallout();

        //Sets the callout layout
        Callout.Style style = new Callout.Style(getContext(), R.xml.callout_properties);
        mCallout.setStyle(style);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Display all solar projects in Duluth
        (new SolarProjectsXMLParser() {

            @Override
            protected void onPostExecute(List<SolarProject> results) {
                if (isAdded()) {
                    // create the symbol to mark on the map
                    BitmapDrawable d = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.star_marker);
                    final PictureMarkerSymbol marker = new PictureMarkerSymbol(d);
                    installed_projects = results;

                    // display each point on the map
                    for (SolarProject s : results) {
                        if (s.p != null) {

                            if ((s.p.getY() < Float.valueOf(getString(R.string.YMax)) && s.p.getY() > Float.valueOf(getString(R.string.YMin))) &&
                                    (s.p.getX() < Float.valueOf(getString(R.string.XMin)) && s.p.getX() > Float.valueOf(getString(R.string.XMax)))) {
                                Graphic graphic = new Graphic(s.p, marker);
                                installedProjectsOverlay.getGraphics().add(graphic);
                            }
                        } else {
                            installed_projects.remove(s);
                        }
                    }
                }
            }
        }).execute(getString(R.string.past_projects));

        // Display the interested locations on map
        lets_update();
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

            // Because we cannot load the rooftop solar data from the web map, we'll replace that layer with our own layer we got from
            // other source
            if ((mainMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) || (mainMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD.NOT_LOADED)) {
                System.out.println("MAP FAILED TO LOAD");
            } else if ((mainMap.getLoadStatus() == LoadStatus.LOADED)) {
                mainMap.getOperationalLayers().set(0, insol_dlh_annovtpk);
            }
        });

        // Listener for selecting a feature.
        mainMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mainMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                // remove any existing callouts
                if (mCallout.isShowing()) {
                    mCallout.dismiss();
                }
                // User click point
                final Point clickPoint = mainMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                // Map tolerance
                double tolerance = 0.0000025;
                double mapTolerance = tolerance * mainMapView.getUnitsPerPixel();
                // Query envelope
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mainMap.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);

                double wgsTolerance = 0.000025; //guess at tolerance, it would be better to find a less static way of doing this.
                final Point cp = (Point) GeometryEngine.project(new Point(clickPoint.getX(), clickPoint.getY(), mainMap.getSpatialReference()), SpatialReferences.getWgs84());
                for (SolarProject s : installed_projects) {
                    if (s.p != null) {
                        Point k = s.p;

                        //check to see if the point touched is close to any of the solar objects
                        if ((k.getX() < cp.getX() + wgsTolerance && k.getX() > cp.getX() - wgsTolerance) && (k.getY() < cp.getY() + wgsTolerance && k.getY() > cp.getY() - wgsTolerance)) {
                            // create a TextView to display field values

                            TextView calloutContent = new TextView(getContext());

                            // Sets textView setting
                            calloutContent.setTextColor(Color.BLACK);
                            calloutContent.setSingleLine(false);
                            calloutContent.setVerticalScrollBarEnabled(true);
                            calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                            calloutContent.setMovementMethod(new ScrollingMovementMethod());
                            calloutContent.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhiteText));

                            //add each of the elements from the solar project object to the display view
                            calloutContent.append("Project Title : " + s.title + "\n");
                            calloutContent.append("Desc : " + s.des + "\n");
                            calloutContent.append("Link : " + s.ulink + "\n");
                            calloutContent.append("Date : " + s.upd + "\n");
                            calloutContent.append("Coordinate : " + "(" + s.p.getX() + "," + s.p.getY() + ")");

                            //display the callout
                            mCallout.setLocation(clickPoint);
                            mCallout.setContent(calloutContent);
                            mCallout.show();
                            break;

                        }
                    }
                }

                // Gets feature attributes. Change made HERE, making the select feature call on the service is incorrect.
                final ListenableFuture<FeatureQueryResult> future = ((FeatureLayer) mainMap.getOperationalLayers().get(2)).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                future.addDoneListener(() -> {
                    try {
                        // Result
                        FeatureQueryResult result = future.get();

                        Iterator<Feature> iterator = result.iterator();
                        // create a TextView to display field values

                        while (iterator.hasNext()) {
                            Feature feature = iterator.next();

                            // create a Map of all available attributes as name value pairs
                            Map<String, Object> attr = feature.getAttributes();
                            Set<String> keys = attr.keySet();

                            // Get object ID for setting interest
                            Object objectID = null;

                            for (String key : keys) {
                                Object value = (attr.get(key) == null) ? "N/A" : attr.get(key);
                                if (key.equals("Bldg_Name"))
                                    key = "Building Name";
                                else if (key.equals("OBJECTID")) {
                                    key = "Object ID";
                                    objectID = value;
                                }
                            }

                            Object finalObjectID = objectID;
                            (new HTTPAsyncTask() {
                                @Override

                                protected void onPostExecute(String result) {

                                    // Dialog content message
                                    String dialogContent = "";
                                    String optimalInfo = "";
                                    String moderateInfo = "";
                                    String flatVal = "";
                                    String objectID = "";

                                    if (isAdded()) {
                                        // center the mapview on selected feature
                                        Envelope envelope1 = feature.getGeometry().getExtent();

                                        mainMapView.setViewpointGeometryWithPaddingAsync(envelope1, 200);

                                        try {

                                            JSONObject data = new JSONObject(result);
                                            JSONArray arrayData = data.getJSONArray("features");
                                            JSONObject attributesData = new JSONObject(String.valueOf(arrayData.get(0)));
                                            JSONObject attributes = (JSONObject) attributesData.get("attributes");
                                            Object OptimalData = attributes.get("VALUE_2");
                                            Object ModerateData = attributes.get("VALUE_1");
                                            Object FlatValue = attributes.get("flat_pct");

//                                            BitmapDrawable d = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.red_pin);
//                                            final PictureMarkerSymbol marker = new PictureMarkerSymbol(d);
//                                            Point p = feature.getGeometry().getExtent().getCenter();
//
//                                            Graphic graphic = new Graphic(p, marker);
//                                            mapMarkersOverlay.getGraphics().add(graphic);

                                            objectID = String.valueOf(finalObjectID);
                                            optimalInfo = OptimalData.toString();
                                            moderateInfo = ModerateData.toString();
                                            flatVal = FlatValue.toString();

                                            // Concatenate data string to the dialog's message text
                                            if (OptimalData != null) dialogContent = dialogContent.concat(OptimalData.toString() + " square meters of optimal suitability\n");
                                            else dialogContent = dialogContent.concat("Optimal Solar Area: N/A\n");

                                            if (ModerateData != null) dialogContent = dialogContent.concat(ModerateData.toString() + " square meters of moderate suitability\n");
                                            else dialogContent = dialogContent.concat("Moderate Solar Area: N/A\n");

                                            if (others_locations.containsKey(String.valueOf(finalObjectID))) dialogContent = dialogContent.concat(others_locations.get(String.valueOf(finalObjectID)) + " people like this\n");
                                            else dialogContent = dialogContent.concat("0 people like this\n");

                                        } catch (JSONException E) {
                                            System.out.println("Error: " + E);
                                        }

                                        Boolean found = false;

                                        for (SolarProject s : installed_projects) {
                                            if (s.p != null) {
                                                Point k = s.p;
                                                if ((k.getX() < cp.getX() + wgsTolerance && k.getX() > cp.getX() - wgsTolerance) && (k.getY() < cp.getY() + wgsTolerance && k.getY() > cp.getY() - wgsTolerance)) {
                                                    found = true;
                                                }
                                            }
                                        }

                                        if (!found) {

                                            // Action when dismiss the dialog
                                            CallbackFunction dismissAction = new CallbackFunction() {
                                                @Override
                                                public void onPostExecute(Object result) {

                                                    SolarAccountManager.appAccountManager().getListOfInterestedLocation(new CallbackFunction() {
                                                        @Override
                                                        public void onPostExecute(Object result) {
                                                            lets_update();
                                                        }
                                                    });
                                                }
                                            };

                                            // Display the info dialog of the selected location
                                            SolarRooftopDialog.displaySolarDialog(getActivity(),
                                                    dialogContent,
                                                    optimalInfo,
                                                    moderateInfo,
                                                    flatVal,
                                                    objectID,
                                                    dismissAction);
                                        }
                                    }
                                }
                            }).execute("http://services.arcgis.com/8df8p0NlLFEShl0r/ArcGIS/rest/services/foot_dlh_5k/FeatureServer/0/query?where=&objectIds=" + attr.get("OBJECTID") + "&time=&geometry=&geometryType=esriGeometryEnvelope&inSR=&spatialRel=esriSpatialRelIntersects&resultType=standard&distance=&units=esriSRUnit_Meter&outFields=OBJECTID%2CBldg_Name%2CBldg_ID%2C+Parcel%2C+BuildingType%2C+created_user%2Ccreated_date%2Clast_edited_user%2Clast_edited_date%2CBuildingNumber+%2Cfidnum+%2COBJECTID_1+%2COBJECTID_12+%2CVALUE_0+%2CVALUE_1+%2CVALUE_2+%2COBJECTID_12_13+%2COBJECTID_12_13_14+%2CVALUE_01+%2CVALUE_12+%2Csol_700k+%2Csol_1000k+%2Cflat+%2Cflat_pct+&returnGeometry=false&returnCentroid=false&multipatchOption=&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&returnExtentOnly=false&returnDistinctValues=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&resultOffset=&resultRecordCount=&returnZ=false&returnM=false&quantizationParameters=&sqlFormat=standard&f=pjson&token=", "GET");
                        }
                    } catch (Exception e1) {
                        Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e1.getMessage());
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
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
                Viewpoint vp = new Viewpoint(mainMapView.getLocationDisplay().getLocation().getPosition(), 4000.0);
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

    public void lets_update(){
        SolarAccountManager.appAccountManager().getListOfInterestedLocation(new CallbackFunction() {
            @Override
            public void onPostExecute(Object result) {

                if (isAdded()) {

                    // Clear all markers and update again
                    interestedLocationsOverlay.getGraphics().clear();

                    //Hash map that contains the building ids and the interest in each of these locations
                    HashMap<String, Integer> public_location_map = (HashMap<String, Integer>) result;
                    others_locations = public_location_map;

                    // for each entry go through and query to find the location on the map and place a marker on it
                    for (Map.Entry<String, Integer> entry : public_location_map.entrySet()) {
                        String key = entry.getKey();
                        Integer value = entry.getValue();

                        //set up query
                        QueryParameters query = new QueryParameters();
                        query.setReturnGeometry(true);
                        query.setWhereClause("upper(OBJECTID) LIKE '%" + key + "%'");

                        //fire off query (async)
                        final ListenableFuture<FeatureQueryResult> future2 = ((FeatureLayer) mainMap.getOperationalLayers().get(2)).getFeatureTable().queryFeaturesAsync(query);

                        //mark locations that come back from query with magenta diamond
                        future2.addDoneListener(() -> {
                            try {
                                FeatureQueryResult queryResult = future2.get();
                                Iterator<Feature> iterator = queryResult.iterator();

                                BitmapDrawable z = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.green_marker);
                                final PictureMarkerSymbol marker = new PictureMarkerSymbol(z);

                                while (iterator.hasNext()) {
                                    Feature feature = iterator.next();
                                    Point p = feature.getGeometry().getExtent().getCenter(); //place in middle of rooftop
                                    Graphic graphic = new Graphic(p, marker);
                                    interestedLocationsOverlay.getGraphics().add(graphic);
                                }
                            } catch (Exception e) {
                                Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }
}

