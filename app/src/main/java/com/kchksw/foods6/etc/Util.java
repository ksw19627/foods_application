package com.kchksw.foods6.etc;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by KimChanHyup on 2016-07-13.
 */

public class Util {
    public static final int CALL_BY_MYINFO = 0;
    public static final int CALL_BY_MYFRINED = 1;
    public static final int CALL_BY_MYGROUP = 2;
    public static final String SERVER_ADDRESS = "http://14.47.214.173:8080/foods6/";

    public static final String[] category = new String[]
            {"치킨", "피자", "분식", "족발", "보쌈", "찜,탕", "일식", "회", "중화요리", "양식", "기타", "한식", "빵", "디저트", "고기", "안주류"};

    public static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }


    public static void groupArrayListSort(ArrayList<Group> groupArrayList) {
        Collections.sort(groupArrayList, new Comparator<Group>() {

            @Override
            public int compare(Group o1, Group o2) { // TODO

                String en1 = o1.getGroups_name();
                String en2 = o2.getGroups_name();
                return en1.compareTo(en2);

            }

        });

    }

    public static void userArrayListSort(ArrayList<User> userArrayList) {
        Collections.sort(userArrayList, new Comparator<User>() {

            @Override
            public int compare(User o1, User o2) { // TODO

                String en1 = o1.getName();
                String en2 = o2.getName();
                return en1.compareTo(en2);

            }

        });

    }
}
