package com.newstee.helper;

import com.newstee.model.data.News;

import java.util.Comparator;

/**
 * Created by Arnold on 09.08.2016.
 */
public class NewsComparator implements Comparator<News> {

    @Override
    public int compare(News lhs, News rhs) {
        long lhsTime = 0;
        long rhsTime = 0;
        try {
            lhsTime = Long.parseLong(lhs.getAdditionTime().trim());
            rhsTime = Long.parseLong(rhs.getAdditionTime().trim());
        } catch (NumberFormatException e) {

        }
        return lhsTime > rhsTime ? -1 : 1;
    }
}
