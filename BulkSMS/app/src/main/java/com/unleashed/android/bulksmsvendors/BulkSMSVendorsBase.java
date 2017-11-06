package com.unleashed.android.bulksmsvendors;

import android.content.Context;

/**
 * Created by sudhanshu on 04/11/17.
 */

public abstract class BulkSMSVendorsBase {


    public abstract void SendMessage(final Context mContext, final String[] phoneNumbers, final String message);

}
