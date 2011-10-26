package org.moss;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

public class MossConfigs extends ListActivity  {
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.act_configs);

        /**
         * TODO: move to database.
         */
        configs = new ArrayList<Config>();
        Config c1 = new Config();
        c1.name = "Basic Configuration";
        c1.desc = "This is a small showcase of different system monitors.";
        c1.filepath = "default.conf";
        c1.asset = true;

        Config c2 = new Config();
        c2.name = "Network Emphasis";
        c2.desc = "This is the first configuration 2";
        c2.filepath = "network.conf";
        c2.asset = true;

        Config c3 = new Config();
        c3.name = "Process Emphasis";
        c3.desc = "This is the first configuration 3";
        c3.filepath = "process.conf";
        c3.asset = true;

        Config c4 = new Config();
        c4.name = "Cherries";
        c4.desc = "Cherry configuration file";
        c4.filepath = "/sdcard/moss/cherries/mossrc";
        c4.asset = false;

        Config c5 = new Config();
        c5.name = "Gold and Grey";
        c5.desc = "Gold and Grey's configuration file.";
        c5.filepath = "/sdcard/moss/goldgrey/mossrc";
        c5.asset = false;

        Config c6 = new Config();
        c6.name = "Black Diamond";
        c6.desc = "Blackdiamond...";
        c6.filepath = "/sdcard/moss/blackdiamond/mossrc";
        c6.asset = false;

        configs.add(c1);
        configs.add(c2);
        configs.add(c3);
        configs.add(c4);
        configs.add(c5);
        configs.add(c6);

        this.inflater = LayoutInflater.from(this);
        this.setListAdapter(
                new ConfigAdapter(this, configs));

        ListView list = this.getListView();
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                if (position >= configs.size()) {
                    /* TODO: report error */
                    return;
                }
                Config c = configs.get(position);
                SharedPreferences prefs =
                    MossConfigs.this.getSharedPreferences(MossPaper.SHARED_PREFS_NAME, 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("config_list", c.name);
                if (c.asset) {
                    /* Default config */
                    edit.putString("sample_config_file", c.filepath);
                    edit.putString("config_file", null);
                } else {
                    /* External config */
                    edit.putString("sample_config_file", "CUSTOM");
                    edit.putString("config_file", c.filepath);
                }
                edit.commit();
                finish();
            }
        });

        EditText customPath = (EditText) findViewById(R.id.custom_path);
        if (null != customPath) {
        }
    }

    class Config {
        String name;
        String desc;
        boolean asset;
        String filepath;
    }

    class ConfigAdapter extends ArrayAdapter<Config> {

        public ConfigAdapter(Context context, List<Config> configs) {
            super(context, R.layout.item_config, configs);
            this.configs = configs;
        }

        @Override
        public View getView(int position,
                            View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView =
                    inflater.inflate(R.layout.item_config, null, false);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(android.R.id.text1);
                holder.desc = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Config config = configs.get(position);
            if (config == null) {
                Log.e("DeviceAdapter", "Roku is null!");

                holder.name.setText("Error during lookup");
                return convertView;
            }

            holder.name.setText(config.name);
            holder.desc.setText(config.name);

            Context context = convertView.getContext();
            holder.name.setTextAppearance(context,
                    android.R.attr.textAppearanceLarge);
            holder.desc.setTextAppearance(context,
                    android.R.attr.textAppearanceSmall);

            return convertView;
        }

        class ViewHolder {
            private TextView name;
            private TextView desc;
        }

        private List<Config> configs;
    }

    private LayoutInflater inflater = null;
    private List <Config> configs;
}
