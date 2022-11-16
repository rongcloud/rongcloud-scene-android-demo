package cn.rongcloud.gameroom.ui.gamelist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.provider.user.Sex;
import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.api.GameApi;
import cn.rongcloud.gameroom.model.FilterOption;

/**
 * @author gyn
 * @date 2022/5/13
 */
public class GameListViewModel extends ViewModel {
    private MutableLiveData<List<RCGameInfo>> gameInfoList;

    private List<FilterOption<String>> genderOptionList;
    private List<FilterOption<RCGameInfo>> gameOptionList = new ArrayList<>();

    private MutableLiveData<FilterOption<String>> genderFilter;
    private MutableLiveData<FilterOption<RCGameInfo>> gameFilter;

    public MutableLiveData<List<RCGameInfo>> getGameInfoList() {
        if (gameInfoList == null) {
            gameInfoList = new MutableLiveData<>();
        }
        return gameInfoList;
    }

    public MutableLiveData<FilterOption<String>> getGenderFilter() {
        if (genderFilter == null) {
            genderFilter = new MutableLiveData<>();
        }
        return genderFilter;
    }

    public MutableLiveData<FilterOption<RCGameInfo>> getGameFilter() {
        if (gameFilter == null) {
            gameFilter = new MutableLiveData<>();
        }
        return gameFilter;
    }

    public void loadGameList() {
        OkApi.get(GameApi.GAME_LIST, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<RCGameInfo> gameInfoList = result.getList(RCGameInfo.class);
                if (gameInfoList != null) {
                    getGameInfoList().setValue(gameInfoList);
                }
                gameOptionList.clear();
                gameOptionList.add(new FilterOption<RCGameInfo>(UIKit.getContext().getString(R.string.game_no_limit_game), true, new RCGameInfo()));
                for (RCGameInfo gameInfo : gameInfoList) {
                    gameOptionList.add(new FilterOption<RCGameInfo>(gameInfo.getGameName(), false, gameInfo));
                }
            }
        });
    }

    public List<FilterOption<String>> getGenderOptionList() {
        if (genderOptionList == null) {
            genderOptionList = new ArrayList<FilterOption<String>>() {{
                add(new FilterOption<String>(UIKit.getContext().getString(R.string.game_no_limit_gender), true, ""));
                add(new FilterOption<String>(UIKit.getContext().getString(R.string.game_male), false, Sex.man.getSex() + ""));
                add(new FilterOption<String>(UIKit.getContext().getString(R.string.game_female), false, Sex.woman.getSex() + ""));
            }};
        }
        return genderOptionList;
    }

    public List<FilterOption<RCGameInfo>> getGameOptionList() {
        return gameOptionList;
    }

    public String getGender() {
        if (genderFilter != null && genderFilter.getValue() != null && genderFilter.getValue().getData() != null) {
            return genderFilter.getValue().getData();
        }
        return "";
    }

    public String getGameId() {
        if (gameFilter != null && gameFilter.getValue() != null && gameFilter.getValue().getData() != null) {
            return gameFilter.getValue().getData().getGameId();
        }
        return "";
    }
}
