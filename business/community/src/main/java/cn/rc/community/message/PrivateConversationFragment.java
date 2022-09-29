//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.rc.community.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

import io.rong.imkit.subconversationlist.SubConversationListFragment;
import io.rong.imlib.model.Conversation;

public class PrivateConversationFragment extends SubConversationListFragment {
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // 通过反射 赋值mConversationType属性
        try {
            Class<?> c = this.getClass().getSuperclass();
            Field field = c.getDeclaredField("mConversationType");
            field.setAccessible(true);
            field.set(this, Conversation.ConversationType.PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
