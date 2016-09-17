package com.qmatica.arsen.aglistview;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class A {

    public static final int NO_ERROR        = 0;
    public static final int SUCCESS_SENT    = NO_ERROR;
    public static final int AUTH_ERROR      = 1;
    public static final int ADDRESS_ERROR   = 2;
    public static final int SEND_ERROR      = 3;
    public static final int MESSAGE_ERROR   = 4;

    public static final int UNKNOWN_ERROR   = 9;

    public static final String NO_ERROR_MESSAGE         = "Order sent";
    public static final String SUCCESS_MESSAGE          = NO_ERROR_MESSAGE;
    public static final String AUTH_ERROR_MESSAGE       = "Authentication failed. Verify Fulcrum username and password.";
    public static final String ADDRESS_ERROR_MESSAGE    = "Illegal email address: ";
    public static final String SEND_ERROR_MESSAGE       = "Invalid email address: ";

    public static final String NETWORK_ERROR_MESSAGE    = "Network error. Verify internet connection settings.";

    public static final int INTERNAL_MODE     = 0;
    public static final int EXTERNAL_MODE     = 1;

    public static final int ENABLED_COLOR = Color.BLACK;
    public static final int DISABLED_COLOR = Color.GRAY;

    public static final int SEARCH_COLOR = Color.BLACK;
    public static final int SEARCH_BG_COLOR = Color.WHITE;
    public static final int SEARCH_HINT_COLOR = Color.GRAY;

    public static final int COLOR_NEW = Color.RED;
    public static final int COLOR_OLD = Color.BLACK;

    public static final int TYPE_ITEM    = 1;                                           // TODO implicit 0 for list read from DB (Depends on CSV)
    public static final int TYPE_SECTION = 0;
    public static final int CHECKED_COLOR = Color.RED;

    public static final int CHECKED_BG_COLOR = Color.LTGRAY;
    public static final int UNCHECKED_BG_COLOR = Color.TRANSPARENT;

    public static final int SECTION_BG_COLOR = Color.DKGRAY;
    public static final int SECTION_END_COLOR = Color.TRANSPARENT;
    public static final int SECTION_COLOR = Color.WHITE;

    public static final int PRODUCT_COLOR = Color.BLACK;
    public static final int PRODUCT_BG_COLOR = Color.TRANSPARENT;
    public static final int SELECTED_BG_COLOR = Color.LTGRAY;

// TODO move strings to strings.xml
    public static final String PRODUCT_LIST = "Selected_Products";
    public static final String INTENT_FINISH = "Finish_Activity";

    public static final int DB_RECORD_EXISTS = -2;                                      // TODO whimsical (db.insert returns -1 as ERROR and row ID if successful)

    public static final int CATEGORY_GROUP_ID = 71407410;                               // TODO whimsical
    public static final int CATEGORY_ID = 0;
    public static final int CATEGORY_ID_OFFSET = 1;                                     // ALL PRODUCTS CATEGORY_ID is 0
}


// TODO stateListAnimator
// TODO ImageButton
// TODO swipe orders    and/or use long click
// TODO getString(R.string.XXXX)
// TODO finish and destroy activities on exit
// TODO clean int vs long for db classes
// TODO finish secondary activity and start Main?
// TODO make Detail extend Order?
// TODO delete selected History records

// TODO silvercup.coffee@gmail.com g33giHVPik!j

// TODO finish ProductActivity if OrderActivity is finished (putExtra?)

/* Icon sizes

192 - 288   384

:1.3333333

144 - 216   288

:2

96  - 144   192

: 2.666667

72  - 108   144

:4

48  - 72    96


240
180
120
90
60

 */