package com.example.jacob.eatapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jacob on 2016-05-08.
 */
public class EventAdapter extends ArrayAdapter<EventItem> {

    private final Context context;
    private final ArrayList<EventItem> eventItemsArrayList;

    public EventAdapter(Context context, ArrayList<EventItem> eventItemsArrayList) {
        super(context, R.layout.eventrow, eventItemsArrayList);

        this.context = context;
        this.eventItemsArrayList = eventItemsArrayList;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final EventItem event = eventItemsArrayList.get(position);


        // 1. Skapa inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Hämta row från inflater
        final View rowView = inflater.inflate(R.layout.eventrow, parent, false);

        // 3. Hämtar alla TextViews från layout_row xlm
        TextView dateView = (TextView) rowView.findViewById(R.id.date_start);
        TextView nopView = (TextView) rowView.findViewById(R.id.numOfPeople);
        TextView titleView = (TextView) rowView.findViewById(R.id.eventTitle);
        TextView descView = (TextView) rowView.findViewById(R.id.description);
        TextView distanceView = (TextView) rowView.findViewById(R.id.location);
        TextView priceView = (TextView) rowView.findViewById(R.id.event_price);
        TextView ageView = (TextView) rowView.findViewById(R.id.age);



        // 5. Formaterar distance värdet
        String distanceString = "";
        try {
            Double distance = event.getDistance();
            if (distance < 0.5) {
                distanceString = "< 0,5 km";
            } else {
                distanceString = String.format("%.2f", distance) + " km";
            }
        } catch (NullPointerException e) {
            Log.e("Distance", "Is not stored on event (SessionUsers own event)");
            distanceString = "Your event";
        }

        // Formatera datum & tid

        try {
            // Från String till Date
            String d_start = event.getDate_Start();
            Long format = event.getTime(d_start);

            // Från Date to String
            Date newDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM\nHH:mm");
            newDate = new Date();
            String datetime = dateFormat.format(format);

            // Sätter dateview text till nytt formaterat datum
            dateView.setText(datetime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Integer accepted_participants = event.getParticipants().size(); // antal deltagare redan

        // 6. Sätter värden på textviewsen
        nopView.setText(accepted_participants + "/" + event.getNumOfPeople());
        titleView.setText(event.getTitle());
        descView.setText(event.getDescription());
        priceView.setText("€ " + event.getPrice().toString());
        distanceView.setText(distanceString);

        // 7. sätter klickare på rowView
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //När någon rad blir klickad på
                final Context context = parent.getContext();
                Bundle args = new Bundle();

                if (event != null) {
                    args.putSerializable(EventDetail.EVENT, event);
                    Log.e("pos", "is valid");
                } else {
                    Log.e("pos", "is null");
                }

                // Handle fragment transition and back stacking
                EventDetail edf = new EventDetail();

                edf.setArguments(args);
                FragmentManager fm = ((Activity) context).getFragmentManager();
                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.mainFrameContainer, edf)
                        .commit();
            }
        });

        return rowView;
    }

}
