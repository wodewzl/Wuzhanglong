package com.bs.bsims.model;

import java.util.Comparator;

public class SortComparatorDepVo implements Comparator<ContactDepTabVO> {

    public int compare(ContactDepTabVO o1, ContactDepTabVO o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}