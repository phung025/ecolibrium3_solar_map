package umd.solarmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.esri.arcgisruntime.mapping.Viewpoint;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import umd.solarmap.AccountManager.SolarAccountManager;
import umd.solarmap.SolarData.SolarLocation;
import umd.solarmap.UtilitiesClasses.CallbackFunction;

/**
 * Created by Someone on 11/28/2016.
 */

public class SavedLocationsFragment extends ListFragment
{
    //This instance method will likely be removed later
    Map<String, Integer> myUserLocationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_locations, container,
                false);

        //get all of the saved locations for this user and add them to the list
        //need a method to get private locations from SolarAccountManager

        /*REMOVE AT SOME POINT
        //Test Locations;
        //public SolarLocation(String locationID, String locationName, double longitude, double latitude)
        SolarLocation myLocation1 = new SolarLocation("0000000","MyLocation",46.7867, -92.1005);
        SolarLocation myLocation2 = new SolarLocation("0000001","MyLocation2",52.7867, -92.1005);
        SolarLocation myLocation3 = new SolarLocation("0000002","MyLocation3",25.7867, -86.1005);
        myUserPrivateLocationList = new LinkedList<>();
        myUserPrivateLocationList.add(myLocation1);
        myUserPrivateLocationList.add(myLocation2);
        myUserPrivateLocationList.add(myLocation3);
        REMOVE AT SOME POINT */

        List<String> locationNameList = new LinkedList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, locationNameList);

        //Get the list
        SolarAccountManager.appAccountManager().getListOfInterestedLocation(new CallbackFunction()
        {
            @Override
            public void onPostExecute()
            {
                if (isAdded())
                {
                    //Sort the List according to popularity and assign it
                    myUserLocationList = locationPopularitySort((Map<String, Integer>) this.getResult());

                    for (int i = 0; i < myUserLocationList.size(); ++i)
                    {
                        String curLocationName = (String) myUserLocationList.entrySet().toArray()[i];

                        adapter.add(curLocationName);

                        System.out.print("Current Location: " + curLocationName);

                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });


        setListAdapter(adapter);

        return rootView;
    }

    /**
     * Called when an item in the list is selected; moves to selected item's
     * location on the map fragment
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the listView
     * @param position The position of the view in the list
     * @param id the row id of the item that was clicked (not needed here)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {


        //remember to change this when the SolarAccountManager method is implemented
        SolarLocation solarLocation = null; //myUserLocationList.get(position);

        ((MainActivity)getActivity()).savedLocationFragmentSwitch(solarLocation.getLocationLongitude(),
                solarLocation.getLocationLatitude(),50000.00);
    }

    /**
     * Sorts a list of SolarLocations based on how many shares the location has
     * @param myList The list to be sorted
     * @return A sorted list, according to popularity
     */
    public Map<String, Integer> locationPopularitySort (Map<String, Integer> myList){

        List list = new LinkedList(myList.entrySet());
        Collections.sort(list, new Comparator()
        {
            @Override
            public int compare(Object o, Object t1)
            {
                return ((Comparable) ((Map.Entry) (o)).getKey())
                        .compareTo(((Map.Entry) (t1)).getKey());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();){
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }
}