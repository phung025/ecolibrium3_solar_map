package umd.solarmap.SolarData;

import android.graphics.drawable.Icon;

/**
 * Created by user on 11/15/16.
 */

public class SolarAchievement {

    // Maybe an icon?
    private String achievement_name;
    private boolean isGained;

    private SolarAchievement(){};

    public SolarAchievement(String name, boolean gained) {

        achievement_name = name;
        isGained = gained;
    }
}
