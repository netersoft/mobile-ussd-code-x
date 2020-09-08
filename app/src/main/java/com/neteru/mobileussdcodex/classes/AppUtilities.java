package com.neteru.mobileussdcodex.classes;

@SuppressWarnings("unused, WeakerAccess")
public class AppUtilities {

    public static String[][] merge(String[][] a, String[][] b){
        String[][] result = new String[a.length+b.length][];

        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);

        return result;
    }

}
