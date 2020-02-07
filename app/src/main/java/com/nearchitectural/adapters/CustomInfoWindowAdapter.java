package com.nearchitectural.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context mContext;
    private FirebaseFirestore db;
    private final String TAG = "CustomInfoWindowAdapter";
    private ImageView pic;

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    private void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    private String thumbnailURL;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        db = FirebaseFirestore.getInstance();
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_panel, null);
    }

    private void renderWindowText(final Marker marker, final View view) {
        String title = marker.getTitle();
        TextView textViewTitle = view.findViewById(R.id.title);
        pic = view.findViewById(R.id.picture);
        final Context context = view.getContext();


        if (marker.getTitle() == null || marker.getTitle().equals("") || marker.getTitle().equals("Unknown")) {
            db.collection("locations").whereEqualTo("summary", marker.getSnippet())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    thumbnailURL = (String) document.getData().get("thumbnail");

                                    setThumbnailURL(thumbnailURL);

                                    GlideApp.with(context)
                                            .load(thumbnailURL)
                                            .override(300, 300)
                                            .error(R.drawable.ic_launcher_background)
                                            .signature(new ObjectKey(thumbnailURL.hashCode()))
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    if (marker.isInfoWindowShown()) {
                                                        marker.showInfoWindow();
                                                    }
                                                    return false;
                                                }
                                            })
                                            .into(pic);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            db.collection("locations").whereEqualTo("name", marker.getTitle())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {

                                    thumbnailURL = (String) document.getData().get("thumbnail");

                                    setThumbnailURL(thumbnailURL);

                                    GlideApp.with(context)
                                            .load(thumbnailURL)
                                            .override(500, 500)
                                            .error(R.drawable.ic_launcher_background)
                                            .signature(new ObjectKey(thumbnailURL.hashCode()))
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    if (marker.isInfoWindowShown()) {
                                                        marker.showInfoWindow();
                                                    }
                                                    return false;
                                                }
                                            })
                                            .into(pic);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


        if (title != null && !title.equals("")) {
            textViewTitle.setText(title);
        } else {
            textViewTitle.setText("");
        }
        ;

        String snippet = marker.getSnippet();
        TextView textViewSnippet = view.findViewById(R.id.snippet);

        if (snippet != null && !snippet.equals("")) {
            textViewSnippet.setText(snippet);
        } else {
            textViewSnippet.setText("");
        }
        ;

        pic.setContentDescription(title);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
