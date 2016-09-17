package com.qmatica.arsen.aglistview;

public class KRESingleton {
    private static String s;
    static {
        s = "a";
    }

    private KRESingleton() {}

    public static String getKRESingleton()
    {
        return s;
    }
    // KRESingleton.getKRESingleton();
    //
}
