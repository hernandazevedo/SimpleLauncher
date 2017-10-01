package br.com.buy4.simplelauncher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsListActivity extends Activity {

    private PackageManager manager;
    private List<AppDetail> apps;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        loadApps();
        loadListView();
        addClickListener();
    }


    private void loadApps(){
        manager = getPackageManager();


        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        //load apps from html folder MY_APP/appname/index.html
        //MY_APP/appname/icon.png

        requestForPermission();//Request sdcard permission
        apps = listHtmlApps(new File("/sdcard/MY_APP"));


        List<AppDetail> apks = new ArrayList<>();
        //Load apks
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            AppDetail app = new AppDetail();
            app.setLabel(ri.loadLabel(manager));
            app.setName(ri.activityInfo.packageName);
            app.setIcon(ri.activityInfo.loadIcon(manager));
            apks.add(app);
        }


        // Sorting
        Collections.sort(apks, new Comparator<AppDetail>() {
            @Override
            public int compare(AppDetail app1, AppDetail app2)
            {
                return  app1.getLabel().toString().compareTo(app2.getLabel().toString());
            }
        });

        apps.addAll(apks);
    }


    public List<AppDetail> listHtmlApps(final File folder) {
        List<AppDetail> appDetailList = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                AppDetail app = new AppDetail();
                app.setLabel(fileEntry.getName());
                app.setName(fileEntry.getAbsolutePath() + "/index.html");
                app.setIcon(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(fileEntry.getAbsolutePath() + "/icon.jpg")));
                appDetailList.add(app);
            }
        }

        return appDetailList;
    }




    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;



    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }

    private void loadListView(){
        list = (ListView)findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.list_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).getIcon());

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).getLabel());

//                TextView appName = (TextView)convertView.findViewById(R.id.item_app_name);
//                appName.setText(apps.get(position).getName());

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                String name = apps.get(pos).getName().toString();

                if(name.contains("html")){
                    //TODO call an activity to open this html file in a webview
                    Intent intent = new Intent(AppsListActivity.this,WebViewActivity.class);
                    intent.putExtra(WebViewActivity.EXTRA_URL,name);
                    startActivity(intent);
                }else {
                    Intent i = manager.getLaunchIntentForPackage(apps.get(pos).getName().toString());
                    AppsListActivity.this.startActivity(i);
                }
            }
        });
    }

}
