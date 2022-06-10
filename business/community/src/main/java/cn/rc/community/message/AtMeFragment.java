package cn.rc.community.message;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.interfaces.IAdapte;

import cn.rc.community.OnConvertListener;
import cn.rc.community.R;
import cn.rc.community.TestUtil;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/9
 * @time 5:33 下午
 * @我的
 */
public class AtMeFragment extends AbsMessageFragment<String> implements OnConvertListener<String> {

    public static AtMeFragment getInstance() {
        AtMeFragment atMeFragment = new AtMeFragment();
        return atMeFragment;
    }

    @Override
    public IAdapte<String, RcyHolder> onSetAdapter() {
        return new MessageAdapter(activity, R.layout.item_message, this);
    }

    @Override
    public void onRefreshData(boolean wait, boolean refresh, MessageResultBack<String> resultBack) {
        if (null != resultBack) resultBack.onResult(TestUtil.getTestStringList("@Msg", 20));
    }

    @Override
    public void onConvert(RcyHolder holder, String s, int position) {
        holder.setText(R.id.tv_last, s);
    }
}
