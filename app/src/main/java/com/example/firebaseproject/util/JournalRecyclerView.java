package com.example.firebaseproject.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseproject.JournalModel;
import com.example.firebaseproject.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalRecyclerView extends RecyclerView.Adapter<JournalRecyclerView.ViewHolder> {





    private Context context;
    private List<JournalModel> arrayList;

    public JournalRecyclerView(Context context, List<JournalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_content, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JournalModel model = arrayList.get(position);
        holder.userNameq.setText(model.getUserName());
        String imageUrl = model.getImageUrl();
        // using picasso to set image url content into imageView2
        Picasso.get().load(imageUrl).placeholder(R.drawable.download).fit().into(holder.imageView2); // placeholder serve as default image to be loaded if any error
        holder.titleText2.setText(model.getTitle());
        holder.thoughtText2.setText(model.getThought());
        long time = (model.getTimeAdded().getSeconds());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timeAdded = sfd.format(new Date(time));
        holder.timeAddedText.setText(timeAdded);



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userNameq)
        TextView userNameq;
        @BindView(R.id.imageView2)
        ImageView imageView2;
        @BindView(R.id.time_added_text)
        TextView timeAddedText;
        @BindView(R.id.titleText2)
        TextView titleText2;
        @BindView(R.id.thoughtText2)
        TextView thoughtText2;

        @BindView(R.id.linearLayout2)
        LinearLayout linearLayout2;
        // since we cannot bind mwny view in many acticities we do the normal R.findView.byId;
        private AlertDialog dialog;
        private AlertDialog.Builder builder;

        ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
