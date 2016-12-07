package umd.solarmap.UtilitiesClasses;

import android.os.AsyncTask;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import umd.solarmap.SolarProject;

/**
 * Reaches out to an xml file by its url and parses it as a georss feed, takes in a url
 * Created by Cody Seavey
 */
public abstract class SolarProjectsXMLParser extends AsyncTask<String, Integer, List<SolarProject>> {
    protected List<SolarProject> doInBackground(String... params) {
        URL url;
        List<SolarProject> a = new ArrayList<>();
        try {
            url = new URL(params[0]);

            //parsing utility for xml
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            //open xml file
            xpp.setInput(url.openStream(), null);

            // go through xml file and generate a solar project object for each entry
            SolarProject d = new SolarProject();
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    String s = xpp.getName();
                    if (s.equals("title")) {
                        d = new SolarProject(); //new object being created so open item
                        d.title = xpp.nextText();
                    } else if (s.equals("description")) {
                        d.des = xpp.nextText();
                    } else if (s.equals("link")) {
                        d.ulink = xpp.getAttributeValue(null, "href");
                        if (d.ulink == null) {
                            d.ulink = xpp.nextText();
                        }
                    } else if (s.equals("pubDate") || s.equals("updated")) {
                        d.upd = xpp.nextText();
                    } else if (s.equals("georss:point")) {
                        String posi = xpp.nextText();
                        String[] strings = posi.split(" ");
                        double lat = Double.valueOf(strings[0]);
                        double lon = Double.valueOf(strings[1]);
                        d.p = new Point(lon, lat, SpatialReferences.getWgs84());
                        a.add(d); //last point that should be added so close off item
                    }
                } else if (xpp.getEventType() == XmlPullParser.END_TAG) {

                }
                xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return a list of the solarproject objects
        return a;
    }

    // add the graphic to the map
    protected abstract void onPostExecute(List<SolarProject> results);
}