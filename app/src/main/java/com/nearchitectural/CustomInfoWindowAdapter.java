package com.nearchitectural;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private final Context mContext;
    private FirebaseFirestore db;
    private ImageView pic;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        db = FirebaseFirestore.getInstance();
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_panel,null);
    }

    private void renderWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView textViewTitle = view.findViewById(R.id.title);
//        pic = view.findViewById(R.id.picture);
//
////        pic.setImageDrawable();

        if(!title.equals("")){
            textViewTitle.setText(title);
        };

        String snippet = marker.getSnippet();
        TextView textViewSnippet = view.findViewById(R.id.snippet);

        if(!snippet.equals("")){
            textViewSnippet.setText(snippet);
        };
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
    }
}
