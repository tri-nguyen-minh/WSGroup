package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;
import dev.wsgroup.main.views.boxes.DialogBoxConfirm;

public class ProfileTab extends Fragment {

    private TextView txtProfileTabUsername;
    private LinearLayout layoutAccountInfo, layoutLogout;

    private SharedPreferences sharedPreferences;
    private String username, token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_main_profile_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtProfileTabUsername = view.findViewById(R.id.txtProfileTabUsername);
        layoutAccountInfo = view.findViewById(R.id.layoutAccountInfo);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        token = sharedPreferences.getString("TOKEN", "");

        APIUserCaller.findUserByToken(token, getActivity().getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user) {
                super.onUserFound(user);
                setUpUsername(user);
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                super.onFailedAPICall(errorCode);
            }
        });

        layoutAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountInfoIntent = new Intent(getActivity().getApplicationContext(), AccountInformationActivity.class);
                accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm confirmLogoutBox = new DialogBoxConfirm(getActivity(), StringUtils.MES_CONFIRM_LOG_OUT) {
                    @Override
                    public void onYesClicked() {
                        sharedPreferences.edit().clear().commit();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                };
                confirmLogoutBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmLogoutBox.show();
            }
        });
    }

    private void setUpUsername(User user) {
        txtProfileTabUsername.setText(user.getFirstName() + " " + user.getLastName());
    }
}