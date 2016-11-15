package umd.solarmap;

import com.esri.arcgisruntime.geometry.Point;

// Store the data for past solar projects
public class SolarProject {
    //Title of project
    public String title;

    //Description of project
    public String des;

    //Static link to the project
    public String ulink;

    //Publish date
    public String upd;

    //Location of the point
    public Point p;
}