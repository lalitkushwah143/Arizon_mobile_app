package com.genius.appdesign2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.genius.appdesign2.data.DataManual;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ManualActivity extends AppCompatActivity {

    private Button bForward, bBack;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<DataManual> arrayList=new ArrayList<>();
    private TabLayout tabLayout;
    private TextView tvPage;

    private static final String CDA_TOKEN="ds7P8ue7X0PmwaS-pSYAIZqbCZ1ZhrwO4iASYCmsTJY";
    private static final String SPACE_ID="h7copa94aofe";

    private final CDAClient client= CDAClient
            .builder()
            .setToken(CDA_TOKEN)
            .setSpace(SPACE_ID)
            .setEnvironment("master")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_manual_tvTitle);
        textView.setText("Manual");
        ImageView imageView = findViewById(R.id.activity_manual_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bBack=findViewById(R.id.activity_manual_bBack);
        bForward=findViewById(R.id.activity_manual_bForward);
        tabLayout = findViewById(R.id.activity_manual_indicator);
        tvPage = findViewById(R.id.activity_manual_tvPage);

        viewPager=findViewById(R.id.activity_manual_viewpager);
        arrayList=new ArrayList<>();

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!=0){
                    int current=viewPager.getCurrentItem();
                    viewPager.setCurrentItem(current-1);
                }
            }
        });
        bForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!=arrayList.size()-1){
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tvPage.setText((tabLayout.getSelectedTabPosition()+1) + "/" + tabLayout.getTabCount());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Toast.makeText(this, "Loading Content", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {

        client
                .observe(CDAEntry.class)
                .withContentType("batch")
                .all()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<CDAArray>() {
                    @Override public void accept(CDAArray entries) {

                        arrayList = new ArrayList<>();

                        final List<String> entryDescriptions = new ArrayList<>();
                        for (final CDAResource resource : entries.items()) {

                            final CDAEntry entry = (CDAEntry) resource;

                            double id = entry.getField("id");
                            String step = entry.getField("step");
                            String type = entry.getField("type");
                            String title = entry.getField("title");
                            CDAAsset asset = entry.getField("image");
                            String url = "";
                            if (asset!= null){
                                url = asset.url();
                            }
                            String desc = entry.getField("desc");

                            Log.e("This", step);
                            arrayList.add(arrayList.size(), new DataManual(id, step, type, title, url, desc));
                        }

                        Collections.sort(arrayList, new Comparator<DataManual>() {
                            @Override
                            public int compare(DataManual manual, DataManual manual1) {
                                return Double.valueOf(manual.getId()).compareTo(Double.valueOf(manual1.getId()));
                            }
                        });
                        viewPagerAdapter=new ViewPagerAdapter(getApplicationContext(), arrayList);
                        viewPagerAdapter.notifyDataSetChanged();
                        viewPager.setAdapter(viewPagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });

        super.onResume();
    }


    public class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<DataManual> dataManuals;

        public ViewPagerAdapter(Context context, ArrayList<DataManual> dataManuals) {
            this.context = context;
            this.dataManuals= dataManuals;
        }

        @Override
        public int getCount() {
            return dataManuals.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.list_manual, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.list_manual_imageview);
            TextView tvTitle=view.findViewById(R.id.list_manual_tvName);
            TextView tvDesc = view.findViewById(R.id.list_manual_tvDesc);
            VideoView videoView = view.findViewById(R.id.list_manual_videoView);
            TextView bAudio = view.findViewById(R.id.list_manual_bAudio);

            bAudio.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            tvTitle.setText(dataManuals.get(position).getTitle());
            tvDesc.setText(dataManuals.get(position).getDesc());
            if (!dataManuals.get(position).getUrl().equals("")){
                Picasso.get().load("https:" + dataManuals.get(position).getUrl()).into(imageView);
            }
            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);

            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }
    }


}