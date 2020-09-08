package com.neteru.mobileussdcodex.classes;

import android.os.Build;

import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import static com.neteru.mobileussdcodex.classes.AppUtilities.merge;
import static com.neteru.mobileussdcodex.classes.Resources.BN_MOOV;
import static com.neteru.mobileussdcodex.classes.Resources.BN_MTN;
import static com.neteru.mobileussdcodex.classes.Resources.CI_MOOV;
import static com.neteru.mobileussdcodex.classes.Resources.CI_MTN;
import static com.neteru.mobileussdcodex.classes.Resources.CM_ORANGE;
import static com.neteru.mobileussdcodex.classes.Resources.HTC;
import static com.neteru.mobileussdcodex.classes.Resources.MA_ORANGE;
import static com.neteru.mobileussdcodex.classes.Resources.NG_AIRTEL;
import static com.neteru.mobileussdcodex.classes.Resources.NG_ETISALAT;
import static com.neteru.mobileussdcodex.classes.Resources.NG_GLO;
import static com.neteru.mobileussdcodex.classes.Resources.NG_MTN;
import static com.neteru.mobileussdcodex.classes.Resources.NI_MOOV;
import static com.neteru.mobileussdcodex.classes.Resources.NI_ORANGE;
import static com.neteru.mobileussdcodex.classes.Resources.SAMSUNG;
import static com.neteru.mobileussdcodex.classes.Resources.SN_ORANGE;
import static com.neteru.mobileussdcodex.classes.Resources.TG_MOOV;
import static com.neteru.mobileussdcodex.classes.Resources.TG_TOGOCEL;
import static com.neteru.mobileussdcodex.classes.Resources.UTILITIES;

public class InitValues {
    private DatabaseManager manager;
    private String[][] fusion;

    private String[] fragments = new String[]{"bnMoov", "bnMtn", "niMoov", "niOrange", "maOrange", "ciMoov", "ciMtn",
                            "ngAirtel", "ngEtisalat", "ngGlo", "ngMtn", "snOrange", "cmOrange", "tgMoov", "tgTogocel", "utilities"},

                     fragmentsTxt = new String[]{"Moov - Bénin", "Mtn - Bénin", "Moov - Niger",
                                                 "Orange - Niger", "Orange - Mali", "Moov - CI",
                                                 "Mtn - CI", "Airtel - Nigeria", "Etisalat - Nigeria",
                                                 "Glo - Nigeria", "Mtn - Nigeria", "Orange - Senegal",
                                                 "Orange - Cameroun", "Moov - Togo", "Togocel", "Codes Utiles"};

    private InitValues(){}

    private InitValues(DatabaseManager databaseManager){
        manager = databaseManager;

        if (Build.MANUFACTURER.toUpperCase().equals("SAMSUNG")){

            fusion = merge(UTILITIES, SAMSUNG);

        }else if (Build.MANUFACTURER.toUpperCase().equals("HTC")){

            fusion = merge(UTILITIES, HTC);

        }else {

            fusion = UTILITIES;

        }

    }

    public static InitValues getInstance(){
        return new InitValues();
    }

    public static InitValues getInstance(DatabaseManager databaseManager){
        return new InitValues(databaseManager);
    }

    public void setValues(){
        String[][][] arrays = new String[][][]{BN_MOOV, BN_MTN, NI_MOOV, NI_ORANGE, MA_ORANGE, CI_MOOV, CI_MTN,
                               NG_AIRTEL, NG_ETISALAT, NG_GLO, NG_MTN, SN_ORANGE, CM_ORANGE, TG_MOOV, TG_TOGOCEL, fusion};

        int x = 0;

        for (String[][] array : arrays) {
            for (String[] elm : array) {
                manager.db_insertValue(new Ussd(elm[0], elm[1], fragments[x], (elm.length > 2 ? elm[2] : null), true, false));
            }
            x++;
        }
    }

    public String[] getFragments(){
        return fragments;
    }

    public String[] getFragmentsTxt(){
        return fragmentsTxt;
    }

}
