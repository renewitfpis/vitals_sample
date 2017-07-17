package sg.lifecare.vitals2.ui.dashboard.nurse;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;

class NurseListAdapter extends RecyclerView.Adapter<NurseListAdapter.NurseViewHolder> {

    private ArrayList<AssistsedEntityResponse.Data> mNurses = new ArrayList<>();

    @Override
    public NurseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_nurse, parent, false);


        return new NurseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NurseViewHolder holder, int position) {
        holder.bindView(mNurses.get(position));
    }

    @Override
    public int getItemCount() {
        return mNurses.size();
    }

    void replaceNurses(List<AssistsedEntityResponse.Data> nurses) {
        mNurses.clear();

        if (nurses != null) {
            mNurses.addAll(nurses);
        }

        notifyDataSetChanged();
    }

    class NurseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.name_text)
        TextView mNameText;

        @BindView(R2.id.profile_image)
        CircleImageView mProfileImage;

        NurseViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindView(AssistsedEntityResponse.Data nurse) {

            mNameText.setText(nurse.getName());

            String avatar = nurse.getDefaultMediaUrl();

            if (TextUtils.isEmpty(avatar)) {

            } else {
                Glide.with(itemView.getContext())
                        .load(avatar)
                        .into(mProfileImage);
            }
        }
    }
}
