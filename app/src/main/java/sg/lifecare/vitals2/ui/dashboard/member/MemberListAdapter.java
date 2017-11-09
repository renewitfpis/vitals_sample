package sg.lifecare.vitals2.ui.dashboard.member;

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

class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<AssistsedEntityResponse.Data> mMembers = new ArrayList<>();

    private OnItemClickListener mListener;

    MemberListAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_member, parent, false);


        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    void replaceMembers(List<AssistsedEntityResponse.Data> members) {
        mMembers.clear();

        if (members != null) {
            mMembers.addAll(members);
        }

        notifyDataSetChanged();
    }


    class MemberViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.name_text)
        TextView mNameText;

        @BindView(R2.id.profile_image)
        CircleImageView mProfileImage;

        public MemberViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
            AssistsedEntityResponse.Data member = mMembers.get(position);

            mNameText.setText(member.getName());

            String avatar = member.getDefaultMediaUrl();

            if (TextUtils.isEmpty(avatar)) {

            } else {
                Glide.with(itemView.getContext())
                        .load(avatar)
                        .into(mProfileImage);
            }

            if (mListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(position);
                    }
                });
            }
        }
    }
}
