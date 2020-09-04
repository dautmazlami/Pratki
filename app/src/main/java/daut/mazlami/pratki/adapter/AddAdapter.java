package daut.mazlami.pratki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.interfaces.OnTrackNumberClick;
import daut.mazlami.pratki.model.TrackNumber;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.AddTrackingViewHolder> {

    private Context context;
    private List<TrackNumber> list;
    private LayoutInflater inflater;
    private OnTrackNumberClick onTrackNumberClick;


    public AddAdapter(Context context, List<TrackNumber> list, OnTrackNumberClick onTrackNumberClick){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.onTrackNumberClick = onTrackNumberClick;
    }

    @NonNull
    @Override
    public AddTrackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_add_new_tn,parent,false);
        return new AddTrackingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddTrackingViewHolder holder, final int position) {
        if (list != null){
            TrackNumber trackNumber = list.get(position);
            holder.IdNumber.setText(trackNumber.getTrackNumber());
            holder.title.setText(trackNumber.getTitle());

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrackNumber deleteItem = list.get(position);
                    list.remove(position);
                    deleteItem.delete();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,getItemCount());

                }
            });

        }else{
            holder.title.setText(R.string.no_trNr_added);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AddTrackingViewHolder extends RecyclerView.ViewHolder {

        private TextView IdNumber;
        private TextView title;
        private ImageButton delete;

        public AddTrackingViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.saved_tracking_title);
            IdNumber = itemView.findViewById(R.id.saved_tracking_number);
            delete = itemView.findViewById(R.id.delete_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTrackNumberClick != null){
                        onTrackNumberClick.onTrackNumberClick(list.get(getAdapterPosition()));
                   }
                }
            });
        }

    }
}
