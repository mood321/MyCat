package com.mood.framework.minicat.container;

import com.mood.framework.minicat.servlet.HttpFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterContainer {
    public static  final List<HttpFilter> FILTER_CONTAINER=new ArrayList<>();

    public static void pushFilter(HttpFilter filter){
        FILTER_CONTAINER.add(filter);
    }
}
