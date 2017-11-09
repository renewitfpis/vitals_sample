package sg.lifecare.vitals2.ui.dashboard.nurse;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import sg.lifecare.data.local.database.Patient;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import timber.log.Timber;

class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientViewHolder> {

    private List<Patient> mPatients = new ArrayList<>();

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_patient, parent, false);


        return new PatientListAdapter.PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        Timber.d("onBindViewHolder: position = %d", position);
        holder.bindView(mPatients.get(position));
    }

    @Override
    public int getItemCount() {
        return mPatients.size();
    }

    void replacePatients(RealmResults<Patient> patients) {
        mPatients.clear();

        if (patients != null) {

            mPatients.addAll(patients);
            Timber.d("replacePatients size a %d  size b %d", patients.size(), mPatients.size());
        }

        notifyDataSetChanged();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.name_text)
        TextView mNameText;

        PatientViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindView(Patient patient) {
            Timber.d("bindView: id=%s", patient.getId());
            if (!TextUtils.isEmpty(patient.getId())) {
                mNameText.setText(patient.getId());
            }
        }
    }
}
