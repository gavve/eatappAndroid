package com.example.jacob.eatapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jacob on 2016-05-28.
 */
public class ParticipantsAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final ArrayList<User> participants;

    public ParticipantsAdapter(Context context, ArrayList<User> participants) {
        super(context, R.layout.participantrow, participants);

        this.context = context;
        this.participants = participants;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        // 1. Skapa inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Hämta row från inflater
        final View rowView = inflater.inflate(R.layout.participantrow, parent, false);

        // 3. Hämtar views from xml participantrow.xlm

        TextView name = (TextView) rowView.findViewById(R.id.participant_name);
        TextView age = (TextView) rowView.findViewById(R.id.participant_age);
        final ImageView profilePic = (ImageView) rowView.findViewById(R.id.participant_profilePic);

        final User u = participants.get(position);
        name.setText(u.getFirst_name());
        age.setText(String.valueOf(u.getAge()) + " years");
        // kolla om bilden redan finns annars ladda ner den i bakgrunden och sätt till imageView
        if (u.getProfile_pic() != null) {
            profilePic.setImageBitmap(u.getProfile_pic());
        } else {
            DownloadImageTask downloadImageTask = new DownloadImageTask(new AsyncInterface() {

                @Override
                public void processFinish(String output) {
                    // gör ingenting här när det är Bitmap som ska bearbetas
                }

                @Override
                public void bitMapFinished(Bitmap output) throws IOException {
                    u.savePictureToInternalStorage(output, rowView.getContext());
                    u.setImageFromStorage();
                    u.setProfile_pic(output);
                    profilePic.setImageBitmap(output);
                }
            });
            downloadImageTask.execute(u.getProfile_pic_url());
        }

        // 7. sätter klickare på rowView
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //När någon rad blir klickad på
                final Context context = parent.getContext();
                GetUserInfoTask getData = new GetUserInfoTask(new AsyncInterface() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                        UserManager um = UserManager.getInstance();
                        User u = um.parseFromStringToUser(new JSONObject(output));
                        um.addUser(u);
                        Bundle args = new Bundle();
                        args.putInt(UserDetails.USER_PK, u.getId());
                        Log.e("pos", "is valid");
                        System.out.println("KLicka på deltagare, måste lägga till fragment för användare");
                        // Handle fragment transition and back stacking
                        UserDetails edf = new UserDetails();

                        edf.setArguments(args);
                        FragmentManager fm = ((Activity) context).getFragmentManager();
                        fm.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.mainFrameContainer, edf)
                                .commit();
                    }

                    @Override
                    public void bitMapFinished(Bitmap output) throws IOException {

                    }
                }, u.getId());
                getData.execute();
            }
        });

        return rowView;
    }
}
