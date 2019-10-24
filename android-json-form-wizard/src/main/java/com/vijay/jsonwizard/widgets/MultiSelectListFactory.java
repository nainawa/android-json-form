package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.adapter.MultiSelectListAdapter;
import com.vijay.jsonwizard.adapter.MultiSelectListSelectedAdapter;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class MultiSelectListFactory implements FormWidgetFactory {
    private MultiSelectListSelectedAdapter multiSelectListSelectedAdapter;
    private MultiSelectListAdapter multiSelectListAdapter;
    private List<MultiSelectItem> selectedData = new ArrayList<>();
    private List<MultiSelectItem> listData = new ArrayList<>();
    private JSONObject jsonObject = new JSONObject();
    private AlertDialog alertDialog;
    private JsonFormFragment jsonFormFragment;
    private String stepName;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(String stepName, final Context context, final JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) {
        Timber.i("stepName %s popup %s listener %s", stepName, popup, listener);
        this.jsonObject = jsonObject;
        this.jsonFormFragment = formFragment;
        this.stepName = stepName;
        this.alertDialog = setUpDialog(context);
        updateSelectedData(prepareSelectedData(), true);
        updateListData(prepareListData(), true);
        List<View> views = new ArrayList<>();
        Button button = createButton(context);
        button.setText(jsonObject.optString("buttonText"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDataDialog();
            }
        });
        RecyclerView recyclerView = createSelectedRecylerView(context);
        View underBar = createUnderBar(context);
        views.add(recyclerView);
        views.add(button);
        views.add(underBar);
        return views;
    }

    public List<MultiSelectItem> getSelectedData() {
        return selectedData;
    }

    public List<MultiSelectItem> getListData() {
        return listData;
    }

    public List<MultiSelectItem> prepareSelectedData() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.VALUE);
            List<MultiSelectItem> multiSelectItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                multiSelectItems.add(new MultiSelectItem(jsonObject1.getString(JsonFormConstants.KEY), jsonObject1.has(JsonFormConstants.VALUE) ? jsonObject1.getString(JsonFormConstants.VALUE) : null));
            }
            return multiSelectItems;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public List<MultiSelectItem> prepareListData() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            List<MultiSelectItem> multiSelectItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                multiSelectItems.add(new MultiSelectItem(jsonObject1.getString(JsonFormConstants.KEY), jsonObject1.has(JsonFormConstants.VALUE) ? jsonObject1.getString(JsonFormConstants.VALUE) : null));
            }
            return multiSelectItems;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public void updateSelectedData(List<MultiSelectItem> selectedData, boolean clearData) {
        if (clearData) {
            getSelectedData().clear();
        }
        getSelectedData().addAll(selectedData);
        getMultiSelectListSelectedAdapter().notifyDataSetChanged();
    }

    public void updateListData(List<MultiSelectItem> listData, boolean clearData) {
        if (clearData) {
            getListData().clear();
        }
        getListData().addAll(listData);
        getMultiSelectListAdapter().notifyDataSetChanged();
    }

    private void showListDataDialog() {
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    private AlertDialog setUpDialog(final Context context) {
        if (jsonFormFragment == null) {
            return null;
        }
        LayoutInflater inflater = jsonFormFragment.getLayoutInflater();
        View view = inflater.inflate(R.layout.multiselectlistdialog, null);
        ImageView imgClose = view.findViewById(R.id.multiSelectListCloseDialog);
        TextView txtMultiSelectListDialogTitle = view.findViewById(R.id.multiSelectListDialogTitle);
        txtMultiSelectListDialogTitle.setText(jsonObject.optString("dialogTitle"));
        SearchView searchViewMultiSelect = view.findViewById(R.id.multiSelectListSearchView);
        searchViewMultiSelect.setQueryHint(jsonObject.optString("searchHint"));
        final RecyclerView recyclerView = view.findViewById(R.id.multiSelectListRecyclerView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialogStyle);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        multiSelectListAdapter = getMultiSelectListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(multiSelectListAdapter);
        searchViewMultiSelect.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                multiSelectListAdapter.getFilter().filter(newText);
                return true;
            }
        });
        multiSelectListAdapter.setOnClickListener(new MultiSelectListAdapter.ClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildLayoutPosition(view);
                MultiSelectItem multiSelectItem = multiSelectListAdapter.getItemAt(position);
                handleClickEventOnListData(multiSelectItem);
                Utils.showToast(context, multiSelectItem.getKey() + " Added");
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    protected void handleClickEventOnListData(MultiSelectItem multiSelectItem) {
        try {
            jsonFormFragment.getJsonApi().writeValue(stepName, jsonObject.optString(JsonFormConstants.KEY), multiSelectItem.toJson().toString(), "", "", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateSelectedData(Arrays.asList(multiSelectItem), false);
    }

    protected RecyclerView createSelectedRecylerView(Context context) {
        multiSelectListSelectedAdapter = getMultiSelectListSelectedAdapter();
        RecyclerView recyclerView = new RecyclerView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(multiSelectListSelectedAdapter);
        return recyclerView;
    }

    protected Button createButton(Context context) {
        Button button = new Button(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutParams);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.button_text_size));
        button.setGravity(Gravity.START);
        button.setTextColor(context.getResources().getColor(R.color.opensrp_accent));
        button.setHeight(context.getResources().getDimensionPixelSize(R.dimen.button_height));
        button.setBackgroundResource(R.color.transparent);
        button.setAllCaps(false);
        button.setTextSize(20);
        button.setTypeface(Typeface.DEFAULT);
        return button;
    }

    protected View createUnderBar(Context context) {
        View underBar = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.extra_bottom_margin);
        underBar.setLayoutParams(layoutParams);
        underBar.setMinimumHeight(2);
        underBar.setBackgroundResource(R.color.black);
        return underBar;
    }

    public MultiSelectListSelectedAdapter getMultiSelectListSelectedAdapter() {
        if (multiSelectListSelectedAdapter == null) {
            return new MultiSelectListSelectedAdapter(getSelectedData());
        }
        return multiSelectListSelectedAdapter;
    }

    public MultiSelectListAdapter getMultiSelectListAdapter() {
        if (multiSelectListAdapter == null) {
            return new MultiSelectListAdapter(getListData());
        }
        return multiSelectListAdapter;
    }
}