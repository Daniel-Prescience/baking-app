package com.udacity.bakingapp.utilities;

import android.content.Context;
import android.widget.Toast;

public class UserInterfaceUtils {

    private static Toast toastMessage;

    public static void ShowToastMessage(String message, Context context) {
        if (toastMessage!= null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toastMessage.show();
    }
}
