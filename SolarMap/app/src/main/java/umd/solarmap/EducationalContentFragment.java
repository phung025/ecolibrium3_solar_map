package umd.solarmap;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by Someone on 11/28/2016.
 */

public class EducationalContentFragment extends Fragment
{

    private WebView myWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_educational_content, container, false);

        myWebView = (WebView) v.findViewById(R.id.edu_webView);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String htmlInfo = "<p><strong>Solar and Energy Effeciency</strong></p>\n" +
                "<p>If you are interested in solar, you are also interested in energy efficiency, " +
                "as efficiency measures typically cost less and allow your solar array to account " +
                "for more of your annual power needs! Read the " +
                "<a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=9\" " +
                "target=\"_self\">Conservation and Efficiency section of the UMN Extension guide for Solar Electricity</a>, " +
                "and check out your local&nbsp;options for building energy audits in the resources " +
                "section ( ) to learn about what specific actions you can take to qualify for solar " +
                "rebates and loans. <br /><br /><strong>Residential and Commercial Incentives</strong>" +
                "</p>\n <p>The following incentives are the primary financial tax incentives and rebates " +
                "available in Duluth. A full list of residential and commercial solar incentives may " +
                "be found at <a href=\"http://www.dsireusa.org/\">www.dsireusa.org</a> by entering " +
                "your postal zipcode.</p>\n <p><a href=\"http://programs.dsireusa.org/system/program/detail/1235\" " +
                "target=\"_blank\">R-Federal Residential 30% Tax Credit</a> <br />" +
                "<a href=\"http://mnpower.com/Environment/SolarThermalWaterHeating\" " +
                "target=\"_blank\">R-Minnesota Power Solar PV Rebates<br />R-Made in Minnesota Solar " +
                "Rebates<br />both - Minnesota Power Solar Thermal Rebates</a> <br />" +
                "<a href=\"http://programs.dsireusa.org/system/program/detail/658\" target=\"_blank\">" +
                "C-Federal Commercial 30% Tax Credit</a><br />" +
                "<a href=\"http://programs.dsireusa.org/system/program/detail/676\" target=\"_blank\">" +
                "C-Accelerated Depreciation Program</a></p>\n" +
                "<p><br /><strong>Residential and Commercial Loans</strong><br />The following " +
                "State-backed loan products areavailable in Duluth. Private banks and credit unions " +
                "may also qualify loan products for solar installations.&nbsp;Additionally, solar " +
                "installers may work with a finance partner to assist in paying for products. It is " +
                "recommended that a potential buyer assess a number of financial products prior to " +
                "selection. <br /><a href=\"https://www.mncee.org/find-financing-incentives/home-energy-loan-program/\" " +
                "target=\"_blank\">R-Center for Energy and the Environment Home loans</a> <br />" +
                "<a href=\"http://www.mnhousing.gov/wcs/Satellite?c=Page&amp;cid=1358904985835&amp;pagename=External%2FPage%2FEXTStandardLayout\" " +
                "target=\"_blank\">R-Minnesota Housing Finance Agency Fix Up Loan</a>(standard secured " +
                "or unsecured only) <br /><a href=\"http://www.sppa.com/wp-content/uploads/2014/03/newPACE2014.pdf\" " +
                "target=\"_self\">C-Property Assessed Clean Energy (PACE) Financing</a><br />" +
                "<a href=\"https://www.mncee.org/find-financing-incentives/cee-commercial-energy-efficiency-loan-program/\" " +
                "target=\"_blank\">C-Center for Energy and the Environment Commerical Energy Efficency loans</a>" +
                " <br /> <br />Solar Installers in Duluth<br />See this" +
                "<a href=\"http://umd-cla-gis04.d.umn.edu/DuluthSolar/docs/Northlandinstallers.pdf\" " +
                "target=\"_blank\"> list of recommended local installers</a> and tips for " +
                "<a href=\"http://umd-cla-gis04.d.umn.edu/DuluthSolar/docs/hiring-renewable-energy-installer.pdf\" " +
                "target=\"_blank\">hiring a renewable energy installer </a>to get started. It is " +
                "recommended that customers interested in solar receive 2-3 bids to compare designs " +
                "and costs. If you know an installer that should be added to this list, please notify " +
                "<a href=\"http://umd-cla-gis04.d.umn.edu/DuluthSolar/ecolibrium3.org\" target=\"_blank\">" +
                "Ecolibrium3</a>. A full list of MN solar installers can be found at " +
                "<a href=\"http://www.mnseia.org/installers\" target=\"_self\">MN SEIA</a>.</p>\n" +
                "<p><strong>More Resources:</strong></p>\n <p><strong>General Solar Information " +
                "</strong><br /><a href=\"http://www.cleanenergyresourceteams.org/technology/solar\">" +
                "CERTs solar technology page</a><br />" +
                "<a href=\"http://www.ecolibrium3.org/programs/solar-market-pathways\">Ecolibrium3 in " +
                "Duluth</a><br /><a href=\"https://mn.gov/commerce/consumers/your-home/energy-info/solar/\">" +
                "Department of Commerce Residential Information</a><br />" +
                "<a href=\"http://mnpower.com/Environment/Solar\">Solar at Minnesota Power</a><br />" +
                "<a href=\"http://www.mnrenewables.org/\">Minnesota Renewable Energy Society</a><br />" +
                "<a href=\"https://www.midwestrenew.org/home\">Midwest Renewable Energy Association</a>" +
                "<br /><a href=\"http://www.cleanenergyresourceteams.org/solargardens\">Community Solar " +
                "Gardens</a><br /><a href=\"http://energytransition.umn.edu/minnesota-energy-storage-alliance-mesa/about-mesa/\">" +
                "Minnesota Energy Storage Alliance (MESA)</a><br /><br /><strong>Implementing Solar " +
                "Electricity at your home (UMN Extension Guide) :<br /></strong>" +
                "<a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=6\">" +
                "Building Site and Assessment</a><strong><br /></strong>" +
                "<a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=9\">" +
                "Conservation and Efficiency</a><strong><br /></strong>" +
                "<a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=14\">" +
                "System Components</a><br /><a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=19\">" +
                "System Sizing</a><br /><a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=23\">" +
                "Costs</a><br /><a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=25\">" +
                "Installation</a><br /><a href=\"http://mn.gov/commerce-stat/pdfs/solar-electricity-for-the-home-farm-and-business.pdf#page=28\">" +
                "Electricity Use Worksheet</a><br /><br /><strong>More about Energy Efficiency</strong><br />" +
                "<a href=\"http://www.duluthenergy.org/\">Georgetown University Energy Prize</a><br />" +
                "<a href=\"https://mn.gov/commerce/consumers/your-home/energy-info/home-energy-guide/\">" +
                "Department of Commerce handbook</a><br /><a href=\"http://mnpower.com/EnergyConservation/HomeEnergyAnalysis\">" +
                "Minnesota Power Home Energy Analysis</a><br /><a href=\"http://www.duluthenergy.org/solar/\">Duluth Energy Campaign</a>" +
                "<br /><a href=\"http://aceee.org/\">American Council for an Energy-Efficient Economy</a>" +
                "<br /><strong><br />Energy Efficiency Programs and Audits<br /></strong>" +
                "<a href=\"http://www.ecolibrium3.org/programs/deep\">Eco3 Duluth Energy Efficiency Program (DEEP)</a>" +
                "<strong><br /></strong></p>\n <p>Home Energy Audit (HEA) &ndash; A residential walkthrough " +
                "audit by an energy expert with direct installs and recommendations, provided for free in by " +
                "<a href=\"http://mnpower.com/EnergyConservation/HomeEnergyAnalysis\">Minnesota Power</a> or " +
                "<a href=\"http://www.comfortsystemsduluth.com/conservation/home-energy-loans/\">ComfortSystems</a></p>\n" +
                "<p>Advanced HEA &ndash; A residential audit using a spectrum of diagnostic tools, including " +
                "infrared imaging, blower door testing, and a gas safety examination. This is a fee " +
                "for service product that is available for rebates. For more information and contractor " +
                "providers, see<a href=\"http://comfortsystemsduluth.com/media/363878/AHEAP-INFO-SHEET.pdf\">" +
                "ComfortSystems</a> or <a href=\"http://mnpower.com/EnergyConservation/HEAAuditors\">Minnesota Power</a>.</p>\n" +
                "<p>Free Commerical <a href=\"http://mnpower.com/EnergyConservation/CommercialEnergyAnalysis\">" +
                "combined gas and electric commercial audits</a> available at Minnesota Power</p>";
        myWebView.loadDataWithBaseURL("", htmlInfo, mimeType, encoding, "");

        // Enable Javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        myWebView.setWebViewClient(new WebViewClient());

        // Inflate the layout for this fragment
        return v;
    }

}