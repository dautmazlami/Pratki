package daut.mazlami.pratki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.model.TrackingData;

public class TrackNrDetailsAdapter extends RecyclerView.Adapter<TrackNrDetailsAdapter.TrackingDetailsViewHolder> {

    private Context context;
    private ArrayList<TrackingData> list;
    private LayoutInflater inflater;

    public TrackNrDetailsAdapter(Context context, ArrayList<TrackingData> list){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TrackingDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_trackingdata_details,parent,false);
        return new TrackingDetailsViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingDetailsViewHolder holder, int position) {
        TrackingData trackingData = list.get(position);

        holder.IdNumber.setText(trackingData.getID());
        holder.beginingNumber.setText(trackingData.getBegining());
        holder.endNumber.setText(trackingData.getEnd());
        holder.dateAndTime.setText(trackingData.getDate());
        holder.notice.setText(trackingData.getNotice());
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TrackingDetailsViewHolder extends RecyclerView.ViewHolder{

        private TextView IdNumber;
        private TextView beginingNumber;
        private TextView endNumber;
        private TextView dateAndTime;
        private TextView notice;
        private TimelineView timelineView;

        public TrackingDetailsViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            IdNumber = itemView.findViewById(R.id.ID_text_view);
            beginingNumber = itemView.findViewById(R.id.begining_text_view);
            endNumber = itemView.findViewById(R.id.end_text_view);
            dateAndTime = itemView.findViewById(R.id.date_text_view);
            notice = itemView.findViewById(R.id.notice_text_view);
            timelineView = itemView.findViewById(R.id.timeLineView);
            timelineView.initLine(viewType);
        }
    }
}
