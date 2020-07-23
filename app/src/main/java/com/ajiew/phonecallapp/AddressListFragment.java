package com.ajiew.phonecallapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 归属地拦截页面
 */
public class AddressListFragment extends Fragment {

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_more)
    TextView tv_more;

    private MyAddressItemRecyclerViewAdapter adapter = new MyAddressItemRecyclerViewAdapter();
    private PromptDialog dialog;
    private List<Address> addressList;

    public AddressListFragment() {
    }

    public static AddressListFragment newInstance() {
        AddressListFragment fragment = new AddressListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData(null);
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setItemLongClickListener(new ItemLongClickListener() {   //长按删除屏蔽地址
            @Override
            public boolean onLongClickListener(View v, int position) {
                Address address = addressList.get(position);
                PromptDialog promptDialog = PromptDialog.newInstance(getActivity(), "", "删除屏蔽地址 : " + address.getName() + "?");
                promptDialog.setPromptButtonClickedListener(new PromptDialog.OnPromptButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked(String msg) {
                        initData(Observable.just(msg)
                                .subscribeOn(Schedulers.io())
                                .map(new Function<String, String>() {
                                    @Override
                                    public String apply(String s) throws Exception {
                                        AppDatabase.getInstance(getActivity()).addressDao().delete(address);
                                        return "";
                                    }
                                }));

                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
                promptDialog.show();

                return false;
            }
        });
        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddress();
            }
        });
    }

    private void addAddress() {
        if (dialog == null) {
            dialog = new PromptDialog(getActivity(), "添加屏蔽地址", "", true);
            dialog.setPromptButtonClickedListener(new PromptDialog.OnPromptButtonClickedListener() {
                @Override
                public void onPositiveButtonClicked(String msg) {
                    if (TextUtils.isEmpty(msg)){
                        return;
                    }
                    Toast.makeText(getActivity(), "将拦截" + msg + "的电话", Toast.LENGTH_LONG).show();
                    initData(Observable.just(msg)
                            .subscribeOn(Schedulers.io())
                            .map(new Function<String, String>() {
                                @Override
                                public String apply(String s) throws Exception {
                                    AppDatabase.getInstance(getActivity()).addressDao().insertAll(new Address(s));
                                    return "";
                                }
                            }));
                }

                @Override
                public void onNegativeButtonClicked() {

                }
            });
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {

                    PromptDialog promptDialog = (PromptDialog) dialog;
                    //调用系统输入法
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


                    inputManager.showSoftInput(promptDialog.getEditText(), 0);
                }
            });
        }

        dialog.show();

    }

    /**
     * 初始化数据
     */
    private void initData(Observable observable) {
        if (observable == null) {
            observable = Observable.just("获取数据");
        }
        observable.subscribeOn(Schedulers.io()).map(new Function<String, List<Address>>() {
            @Override
            public List<Address> apply(String s) throws Exception {
                return AppDatabase.getInstance(getActivity()).addressDao().getAll();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Address>>() {
            @Override
            public void accept(List<Address> addresses) throws Exception {
                addressList = addresses;
                adapter.setmValues(addresses);
            }
        });
    }

}