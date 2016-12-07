package umd.solarmap;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import umd.solarmap.AccountManager.SolarAccountManager;
import umd.solarmap.UtilitiesClasses.CallbackFunction;

/**
 * Created by Someone on 11/28/2016.
 */

public class SavedLocationsFragment extends ListFragment
{
    //This instance method will likely be removed later
    Map<String, Integer> myUserLocationMap;

    List<String> adapterNameList;
    List<String> locationNameList;
    ArrayAdapter<String> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_saved_locations, container,
                false);

        adapterNameList = new LinkedList<>();
        locationNameList = new LinkedList<>();

        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, adapterNameList);

        //Get the list
        SolarAccountManager.appAccountManager().getListOfInterestedLocation(new CallbackFunction()
        {
            @Override
            public void onPostExecute()
            {
                //Sort the List according to popularity and assign it
                myUserLocationMap = sortByValue((Map<String, Integer>) this.getResult());

                for (String key : myUserLocationMap.keySet())
                {
                    Integer userCount = myUserLocationMap.get(key);
                    locationNameList.add(key);

                    adapter.add(key + "\t Popularity: " + userCount);

                }
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Called when an item in the list is selected; moves to selected item's
     * location on the map fragment
     *
     * @param l        The ListView where the click happened
     * @param v        The view that was clicked within the listView
     * @param position The position of the view in the list
     * @param id       the row id of the item that was clicked (not needed here)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        //get the location id of the list item that was clicked
        String myId = locationNameList.get(position);

        ((MainActivity) getActivity()).savedLocationFragmentSwitch(myId);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}