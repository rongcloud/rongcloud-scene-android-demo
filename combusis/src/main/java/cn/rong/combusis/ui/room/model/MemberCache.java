package cn.rong.combusis.ui.room.model;

import androidx.lifecycle.MutableLiveData;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;

import java.util.ArrayList;
import java.util.List;

import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.user.UserProvider;

/**
 * @author gyn
 * @date 2021/9/27
 */
public class MemberCache {

    private final MutableLiveData<List<User>> memberList = new MutableLiveData<>(new ArrayList<>(0));
    private final MutableLiveData<List<String>> adminList = new MutableLiveData<>(new ArrayList<>(0));

    public static MemberCache getInstance() {
        return Holder.INSTANCE;
    }

    public MutableLiveData<List<User>> getMemberList() {
        return memberList;
    }

    public MutableLiveData<List<String>> getAdminList() {
        return adminList;
    }

    /**
     * 拉取房间成员和管理员
     *
     * @param roomId
     */
    public void fetchData(String roomId) {
        refreshMemberData(roomId);
        refreshAdminData(roomId);
    }

    /**
     * 拉取成员列表
     *
     * @param roomId
     */
    private void refreshMemberData(String roomId) {
        OkApi.get(VRApi.getMembers(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<User> list = result.getList(User.class);
                    if (list != null) {
                        memberList.setValue(list);
                    }
                    for (User user : list) {
                        UserProvider.provider().update(user.toUserInfo());
                    }
                }
            }
        });
    }

    /**
     * 拉取管理员列表
     *
     * @param roomId
     */
    public void refreshAdminData(String roomId) {
        OkApi.get(VRApi.getAdminMembers(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<User> list = result.getList(User.class);
                    if (list != null) {
                        List<String> ids = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            ids.add(list.get(i).getUserId());
                        }
                        adminList.setValue(ids);
                    }
                }
            }
        });
    }

    /**
     * 判断一个用户是否是管理员
     *
     * @param userId
     * @return
     */
    public boolean isAdmin(String userId) {
        List<String> ids = adminList.getValue();
        if (ids != null) {
            return ids.contains(userId);
        }
        return false;
    }

    /**
     * 删除某个成员
     *
     * @param user
     */
    public void removeMember(User user) {
        List<User> list = getMembers();
        if (list.contains(user)) {
            list.remove(user);
            memberList.setValue(list);
        }
    }

    /**
     * 通过userId，拿到对应的成员
     */
    public User getMember(String userId) {
        List<User> members = getMembers();
        for (User member : members) {
            if (member.getUserId().equals(userId)) {
                return member;
            }
        }
        return null;
    }

    /**
     * 添加成员
     *
     * @param user
     */
    public void addMember(User user) {
        List<User> list = getMembers();
        if (!list.contains(user)) {
            list.add(user);
            memberList.setValue(list);
        }
    }

    public void addAdmin(String id) {
        List<String> ids = getAdminList().getValue();
        if (!ids.contains(id)) {
            ids.add(id);
            getAdminList().setValue(ids);
        }
    }

    public void removeAdmin(String id) {
        List<String> ids = getAdminList().getValue();
        if (ids.contains(id)) {
            ids.remove(id);
            getAdminList().setValue(ids);
        }
    }

    private List<User> getMembers() {
        return memberList.getValue();
    }

    private static class Holder {
        private static final MemberCache INSTANCE = new MemberCache();
    }
}
