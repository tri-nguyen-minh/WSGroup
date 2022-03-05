package dev.wsgroup.main.views.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class CampaignFragment extends Fragment {

    private RelativeLayout layoutNoCampaign, layoutLoading;
    private RecyclerView recViewCampaignView;

    private String campaignStatus;
    private Product product;
    private String productId;
    private List<Campaign> campaignList;
    private RecViewCampaignListAdapter adapter;

    public CampaignFragment(String campaignStatus) {
        if (campaignStatus.toLowerCase().equals("ongoing")) {
            this.campaignStatus = "active";
        } else if (campaignStatus.toLowerCase().equals("finished")) {
            this.campaignStatus = "done";
        } else if (campaignStatus.toLowerCase().equals("upcoming")) {
            this.campaignStatus = "upcoming";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_campaign, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutNoCampaign = view.findViewById(R.id.layoutNoCampaign);
        recViewCampaignView = view.findViewById(R.id.recViewCampaignView);

        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoCampaign.setVisibility(View.INVISIBLE);
        recViewCampaignView.setVisibility(View.INVISIBLE);

        product = (Product) getActivity().getIntent().getSerializableExtra("PRODUCT");
        productId = product.getProductId();
        APICampaignCaller.getCampaignListByProductId(productId, campaignStatus,
                    campaignList, getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> foundCampaignList) {
                        campaignList = foundCampaignList;
                        if (campaignList.size() > 0) {
                            layoutLoading.setVisibility(View.INVISIBLE);
                            layoutNoCampaign.setVisibility(View.INVISIBLE);
                            recViewCampaignView.setVisibility(View.VISIBLE);
                            setupCampaignList();
                        } else {
                            layoutLoading.setVisibility(View.INVISIBLE);
                            layoutNoCampaign.setVisibility(View.VISIBLE);
                            recViewCampaignView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onNoJSONFound() {
                        layoutLoading.setVisibility(View.INVISIBLE);
                        layoutNoCampaign.setVisibility(View.VISIBLE);
                        recViewCampaignView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        layoutLoading.setVisibility(View.INVISIBLE);
                        layoutNoCampaign.setVisibility(View.VISIBLE);
                        recViewCampaignView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void setupCampaignList() {
        adapter = new RecViewCampaignListAdapter(getContext()) {
            @Override
            public void executeOnCampaignSelected(Campaign campaign) {
                getActivity().getIntent().putExtra("CAMPAIGN_SELECTED", campaign);
                getActivity().getIntent().putExtra("REQUEST_CODE", IntegerUtils.REQUEST_SELECT_CAMPAIGN);
                getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
                getActivity().finish();
            }
        };
        adapter.setCampaignList(campaignList);
        recViewCampaignView.setAdapter(adapter);
        recViewCampaignView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }
}