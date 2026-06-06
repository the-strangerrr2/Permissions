package com.example.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final int REQUEST_PERMISSIONS = 100;

    private final List<PermissionItem> permissionItems = new ArrayList<>();
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPermissions();
        buildUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPermissionList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            refreshPermissionList();
            Toast.makeText(this, "وضعیت دسترسی‌ها به‌روزرسانی شد", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPermissions() {
        permissionItems.add(new PermissionItem(
                Manifest.permission.CAMERA,
                "دوربین",
                "برای نمایش نمونه‌ی درخواست دسترسی به Camera"));
        permissionItems.add(new PermissionItem(
                Manifest.permission.ACCESS_FINE_LOCATION,
                "موقعیت مکانی",
                "برای نمایش نمونه‌ی دسترسی حساس Location"));
        permissionItems.add(new PermissionItem(
                Manifest.permission.READ_CONTACTS,
                "مخاطبین",
                "برای نمایش دسترسی به اطلاعات شخصی کاربر"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionItems.add(new PermissionItem(
                    Manifest.permission.POST_NOTIFICATIONS,
                    "اعلان‌ها",
                    "در اندروید ۱۳ به بعد باید جداگانه درخواست شود"));
        }
    }

    private void buildUi() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(247, 248, 250));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(26), dp(20), dp(22));
        header.setBackgroundResource(R.drawable.bg_header);
        root.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(this);
        title.setText("مدیریت Permission ها");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.RIGHT);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("نمونه‌ی درس موبایل ۲: بررسی، درخواست و راهنمایی دسترسی‌های runtime در اندروید");
        subtitle.setTextColor(Color.rgb(225, 245, 238));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.RIGHT);
        subtitle.setPadding(0, dp(8), 0, 0);
        header.addView(subtitle);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setPadding(dp(16), dp(16), dp(16), dp(8));
        root.addView(controls);

        Button requestAllButton = createPrimaryButton("درخواست همه دسترسی‌ها");
        requestAllButton.setOnClickListener(v -> requestMissingPermissions());
        controls.addView(requestAllButton);

        Button settingsButton = createSecondaryButton("باز کردن تنظیمات برنامه");
        settingsButton.setOnClickListener(v -> openAppSettings());
        LinearLayout.LayoutParams settingsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        settingsParams.topMargin = dp(10);
        controls.addView(settingsButton, settingsParams);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(dp(16), dp(8), dp(16), dp(18));
        root.addView(listContainer);

        setContentView(scrollView);
        refreshPermissionList();
    }

    private void refreshPermissionList() {
        if (listContainer == null) {
            return;
        }
        listContainer.removeAllViews();
        for (PermissionItem item : permissionItems) {
            listContainer.addView(createPermissionCard(item));
        }
    }

    private View createPermissionCard(PermissionItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackgroundResource(R.drawable.bg_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(row);

        TextView status = new TextView(this);
        boolean granted = isGranted(item.permission);
        status.setText(granted ? "داده شده" : "داده نشده");
        status.setTextColor(granted ? Color.rgb(21, 99, 66) : Color.rgb(162, 42, 42));
        status.setTextSize(13);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(10), dp(5), dp(10), dp(5));
        status.setBackgroundResource(granted ? R.drawable.bg_status_granted : R.drawable.bg_status_denied);
        row.addView(status);

        TextView name = new TextView(this);
        name.setText(item.title);
        name.setTextColor(Color.rgb(31, 37, 45));
        name.setTextSize(18);
        name.setGravity(Gravity.RIGHT);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        row.addView(name, nameParams);

        TextView description = new TextView(this);
        description.setText(item.description);
        description.setTextColor(Color.rgb(88, 96, 108));
        description.setTextSize(14);
        description.setGravity(Gravity.RIGHT);
        description.setPadding(0, dp(8), 0, dp(10));
        card.addView(description);

        Button button = createPrimaryButton(granted ? "قبلا مجاز شده" : "درخواست این دسترسی");
        button.setEnabled(!granted);
        button.setOnClickListener(v -> requestPermissions(new String[]{item.permission}, REQUEST_PERMISSIONS));
        card.addView(button);

        if (!granted && shouldShowRequestPermissionRationale(item.permission)) {
            TextView rationale = new TextView(this);
            rationale.setText("این دسترسی برای اجرای کامل نمونه لازم است. اگر رد شود، برنامه فقط وضعیت آن را نمایش می‌دهد.");
            rationale.setTextColor(Color.rgb(120, 88, 38));
            rationale.setTextSize(13);
            rationale.setGravity(Gravity.RIGHT);
            rationale.setPadding(0, dp(8), 0, 0);
            card.addView(rationale);
        }

        return card;
    }

    private void requestMissingPermissions() {
        List<String> missing = new ArrayList<>();
        for (PermissionItem item : permissionItems) {
            if (!isGranted(item.permission)) {
                missing.add(item.permission);
            }
        }

        if (missing.isEmpty()) {
            Toast.makeText(this, "همه دسترسی‌ها قبلا داده شده‌اند", Toast.LENGTH_SHORT).show();
            return;
        }

        requestPermissions(missing.toArray(new String[0]), REQUEST_PERMISSIONS);
    }

    private boolean isGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15);
        button.setBackgroundResource(R.drawable.bg_button);
        return button;
    }

    private Button createSecondaryButton(String text) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(Color.rgb(31, 122, 92));
        button.setTextSize(15);
        button.setBackgroundColor(Color.TRANSPARENT);
        return button;
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static class PermissionItem {
        final String permission;
        final String title;
        final String description;

        PermissionItem(String permission, String title, String description) {
            this.permission = permission;
            this.title = title;
            this.description = description;
        }
    }
}
