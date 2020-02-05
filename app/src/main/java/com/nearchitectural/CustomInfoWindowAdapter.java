package com.nearchitectural;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private final Context mContext;
    private FirebaseFirestore db;
    private final String TAG = "CustomInfoWindowAdapter";
    private ImageView pic;

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    private String thumbnailURL;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        db = FirebaseFirestore.getInstance();
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_panel,null);
    }

    private void renderWindowText(Marker marker, final View view){
        String title = marker.getTitle();
        TextView textViewTitle = view.findViewById(R.id.title);


        db.collection("locations").whereEqualTo("name",marker.getTitle())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                thumbnailURL = (String) document.getData().get("thumbnail");

                                setThumbnailURL(thumbnailURL);

                                GlideApp.with(view.getContext())
                                        .load(getThumbnailURL())
                                        .override(300, 300)
                                        .error(R.drawable.ic_launcher_background)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .into((ImageView) view.findViewById(R.id.picture));
                            }
                        } else {
                            /* If this block executes, either no document was found
                             * matching the search name or some other error occurred*/
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

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
