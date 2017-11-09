package sg.lifecare.vitals2.ui.dashboard.member;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class MemberListPresenter<V extends MemberListMvpView> extends BasePresenter<V>
        implements MemberListMvpPresenter<V> {

    @Inject
    public MemberListPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public List<AssistsedEntityResponse.Data> getMembers() {
        return getDataManager().getMembersEntity();
    }
}
