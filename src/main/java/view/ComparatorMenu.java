package view;

import java.util.Comparator;

public class ComparatorMenu implements Comparator<Object[]> {


    @Override
    public int compare(Object[] o, Object[] t1) {
        int i=0;

        String a = o[1].toString().toUpperCase();
        String b = t1[1].toString().toUpperCase();

        while(i<a.length()&&i<b.length()&&(a.charAt(i)==b.charAt(i))) {
            i++;
        }
        if(i==a.length()&&i==b.length())
            return 0;
        if(i>=a.length())
            return -1;
        else if(i>=b.length())
            return 1;


        return a.charAt(i)-b.charAt(i);
    }
}


