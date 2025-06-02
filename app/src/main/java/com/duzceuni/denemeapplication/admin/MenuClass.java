package com.duzceuni.denemeapplication.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import com.duzceuni.denemeapplication.R;

public class MenuClass {

    public void logout(MenuItem menuItem, Context context, SharedPreferences sharedPref)
    {
        if (menuItem.getItemId()== R.id.snv_kitaplik)
        {
            Intent intent = new Intent(context,AdminHomeActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.activity_anim,R.anim.activity_anim_exit);
        }
        if (menuItem.getItemId()==R.id.snv_kitapekle)
        {
            Intent intent = new Intent(context,AdminBookAddActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.activity_anim,R.anim.activity_anim_exit);
        }
        if (menuItem.getItemId()==R.id.snv_blocked)
        {
            Intent intent = new Intent(context,AdminBlockedUsersActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.activity_anim,R.anim.activity_anim_exit);
        }
        if (menuItem.getItemId()==R.id.snv_logout)
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(context,AdminLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
        if (menuItem.getItemId()==R.id.snv_settings)
        {
            Intent intent = new Intent(context,AdminSettingsActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.activity_anim,R.anim.activity_anim_exit);
        }
        if (menuItem.getItemId()==R.id.snv_users)
        {
            Intent intent = new Intent(context, AdminUsersActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.activity_anim,R.anim.activity_anim_exit);
        }

    }
}
