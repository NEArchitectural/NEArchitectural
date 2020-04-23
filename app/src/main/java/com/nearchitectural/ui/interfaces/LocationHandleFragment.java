package com.nearchitectural.ui.interfaces;

/* Author:  Mark Lumb
 * Since:   02/04/20
 * Version: 1.0
 * Purpose: Allows an active fragment to be alerted when a change in location permissions
 *          or location services has occurred and perform fragment specific operations
 *          if needed
 */
public interface LocationHandleFragment {
    /* Informs fragment of a change in location permissions/services
       and whether both have been granted */
    void handleLocation(boolean permissionGranted);
}
