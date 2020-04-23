package com.nearchitectural.ui.interfaces;

/* Author:  Mark Lumb
 * Since:   29/03/20
 * Version: 1.0
 * Purpose: Allows an active fragment to be alerted when the back button is pressed
 *          perform fragment-specific behaviour if needed. Idea taken and adapted
 *          from the following page:
 *          https://stackoverflow.com/questions/5448653/how-to-implement-onbackpressed-in-fragments
 */
public interface BackHandleFragment {
    /* Informs fragment that back button has been pressed and returns boolean
     * indicating whether fragment has made use of back press */
    boolean onBackPressed();
}
