package com.tes.theengineeringsolutions.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ColorUtils {

    public static String getColors() {
        ArrayList<String> colorList = new ArrayList<>(Arrays.asList(
                "#E97939",
                "#607F55",
                "#3C3B1D",
                "#596164",
                "#838294",
                "#252831",
                "#F8C9B5",
                "#79CBE8",
                "#D1A38B",
                "#B4DDF6",
                "#FFD8D8",
                "#BDD8AF"
        ));
        Collections.shuffle(colorList);
        return colorList.get(0);
    }
}
