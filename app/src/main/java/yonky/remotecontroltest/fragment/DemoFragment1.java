package yonky.remotecontroltest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yonky.remotecontroltest.DraggableInfo;
import yonky.remotecontroltest.MainActivity;
import yonky.remotecontroltest.R;
import yonky.remotecontroltest.Tools;

/**
 * Created by Administrator on 2018/3/27.
 */

public class DemoFragment1 extends Fragment implements View.OnLongClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.iv_offered_cursor).setTag(new DraggableInfo("", R.mipmap.offered_cursor, 0, 2));
        view.findViewById(R.id.iv_offered_playloop).setTag(new DraggableInfo("", R.mipmap.offered_playloop, 0, 0));
        view.findViewById(R.id.iv_offered_random).setTag(new DraggableInfo("", R.mipmap.offered_random, 0, 0));
        view.findViewById(R.id.iv_offered_channel).setTag(new DraggableInfo("", R.mipmap.offered_channel, 0, 3));
        view.findViewById(R.id.iv_offered_vol).setTag(new DraggableInfo("", R.mipmap.offered_vol, 0, 3));

        view.findViewById(R.id.iv_offered_cursor).setOnLongClickListener(this);
        view.findViewById(R.id.iv_offered_playloop).setOnLongClickListener(this);
        view.findViewById(R.id.iv_offered_random).setOnLongClickListener(this);
        view.findViewById(R.id.iv_offered_channel).setOnLongClickListener(this);
        view.findViewById(R.id.iv_offered_vol).setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Tools.startDrag(v);
        ((MainActivity) getActivity()).setDragInfo((DraggableInfo) v.getTag());
        return false;
    }
}
