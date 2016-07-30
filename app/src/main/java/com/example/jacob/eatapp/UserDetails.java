package com.example.jacob.eatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String USER_PK = "user_pk";
    private User user;
    public Boolean myProfile = false;

    // TODO: Rename and change types of parameters
    private String mParam1;


    public UserDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment UserDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetails newInstance(Integer user_pk) {
        UserDetails fragment = new UserDetails();
        Bundle args = new Bundle();
        args.putSerializable(UserDetails.USER_PK, user_pk);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get arguments for Event from ListEvent
            Bundle args = getArguments();
            if (getArguments().getInt(USER_PK) == SessionUser.getInstance().getId()) {
                myProfile = true;
            }
            else {
                user = UserManager.getInstance().getUser(getArguments().getInt(USER_PK));
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        // Göm floating action bar
        FloatingActionButton fab = ((MainActivity) getActivity()).getFloatingActionButton();
        fab.hide();

        // Hämtar TextViews från layout
        TextView userName = (TextView) view.findViewById(R.id.userDetailsName);
        TextView userAge = (TextView) view.findViewById(R.id.userDetailAge);
        final ImageView userPicture = (ImageView) view.findViewById(R.id.userDetailsProfilePicture);
        if (myProfile) {
            // OM man kollar sin egna profil så använda SessionUser
            userName.setText(SessionUser.getInstance().getFirst_name());
            userAge.setText(String.valueOf(SessionUser.getInstance().getAge()));

            // kolla om bilden redan finns annars ladda ner den i bakgrunden och sätt till imageView
            if (SessionUser.getInstance().getProfile_pic() != null) {
                userPicture.setImageBitmap(SessionUser.getInstance().getProfile_pic());
            } else {
                DownloadImageTask downloadImageTask = new DownloadImageTask(new AsyncInterface() {

                    @Override
                    public void processFinish(String output) {
                        // gör ingenting här när det är Bitmap som ska bearbetas
                    }

                    @Override
                    public void bitMapFinished(Bitmap output) throws IOException {
                        SessionUser.getInstance().savePictureToInternalStorage(output, getView().getContext());
                        SessionUser.getInstance().setImageFromStorage();
                        userPicture.setImageBitmap(output);
                    }
                });
                downloadImageTask.execute(SessionUser.getInstance().getProfile_pic_url());
            }

            // Sätt users firstname som actionbar header title
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(SessionUser.getInstance().getFirst_name());

        } else {
            // Annars hämta usern från managern
            userName.setText(user.getFirst_name());
            userAge.setText(String.valueOf(user.getAge()));

            // kolla om bilden redan finns annars ladda ner den i bakgrunden och sätt till imageView
            if (user.getProfile_pic() != null) {
                userPicture.setImageBitmap(user.getProfile_pic());
            } else {
                DownloadImageTask downloadImageTask = new DownloadImageTask(new AsyncInterface() {

                    @Override
                    public void processFinish(String output) {
                        // gör ingenting här när det är Bitmap som ska bearbetas
                    }

                    @Override
                    public void bitMapFinished(Bitmap output) throws IOException {
                        user.savePictureToInternalStorage(output, getView().getContext());
                        user.setImageFromStorage();
                        userPicture.setImageBitmap(output);
                    }
                });
                downloadImageTask.execute(user.getProfile_pic_url());
            }

            // Sätt users firstname som actionbar header title
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(user.getFirst_name());
        }


        return view;
    }


}
