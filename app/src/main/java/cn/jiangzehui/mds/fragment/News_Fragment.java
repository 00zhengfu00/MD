package cn.jiangzehui.mds.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jiangzehui.mds.adapter.NewsRecyclerViewAdapter;
import cn.jiangzehui.mds.R;
import cn.jiangzehui.mds.retrofit.Api;
import cn.jiangzehui.mds.retrofit.HttpService;
import cn.jiangzehui.mds.WebActivity;
import cn.jiangzehui.mds.util.T;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by quxianglin on 16/11/5.
 */
public class News_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View view;
    @InjectView(R.id.rv)
    RecyclerView rv;
    NewsRecyclerViewAdapter adapter;
    @InjectView(R.id.fresh)
    SwipeRefreshLayout fresh;
    View footView;
    TextView tv;
    ProgressBar pb;


    public static News_Fragment newInstance(String type) {
        News_Fragment newsFragment = new News_Fragment();
        Bundle bd = new Bundle();
        bd.putString("type", type);
        newsFragment.setArguments(bd);
        return newsFragment;
    }

    String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_news, container, false);
        }
        ButterKnife.inject(this, view);
        fresh.setColorSchemeResources(R.color.main, android.R.color.holo_orange_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        fresh.setOnRefreshListener(this);

        Bundle bundle = getArguments();
        type = bundle.getString("type");
        getData(type);
        Log.i(type, "onCreateView");


        return view;
    }


    private void getData(String type) {
        Api.getInstance().getService().Get_news(type).enqueue(new Callback<HttpService.Result>() {
            @Override
            public void onResponse(Call<HttpService.Result> call, final Response<HttpService.Result> response) {
                updateUi(response.body());
            }

            @Override
            public void onFailure(Call<HttpService.Result> call, Throwable t) {
                T.show(getActivity(), "网络链接失败");
                if (fresh != null) {
                    fresh.setRefreshing(false);
                }
            }
        });
    }


    /**
     * 拿到数据更新界面
     *
     * @param result
     */
    private void updateUi(final HttpService.Result result) {
        if (fresh != null) {
            fresh.setRefreshing(false);
        }
        if (result.getError_code() == 0) {


            if (adapter == null) {
                footView = LayoutInflater.from(getActivity()).inflate(R.layout.item_footview, null);
                footView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv = (TextView) footView.findViewById(R.id.tv);
                pb = (ProgressBar) footView.findViewById(R.id.pb);
                adapter = new NewsRecyclerViewAdapter(getActivity(), result.getResult().getData(), footView);
                adapter.setOnItemClickLitener(new NewsRecyclerViewAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        T.open(getActivity(), WebActivity.class, "url", result.getResult().getData().get(position).getUrl());
                    }
                });
                if (rv != null) {
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));


                    rv.setAdapter(adapter);
                }

            } else {
                adapter.setList(result.getResult().getData());
            }
        } else {
            T.show(getActivity(), result.getReason());
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        Log.i(type, "onDestroyView");
    }


    @Override
    public void onRefresh() {
        getData(type);
    }
}
