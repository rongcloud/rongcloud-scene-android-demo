package cn.rc.community.channel.editor;

public interface IDragCallback {
    boolean itemTouchOnMove(int from, int to);
    void onMoveComplete();
}
