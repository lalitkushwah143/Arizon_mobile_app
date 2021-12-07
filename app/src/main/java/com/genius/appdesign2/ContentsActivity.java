package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.genius.appdesign2.data.DataManual;
import com.genius.appdesign2.data.DataManuals;
import com.genius.appdesign2.data.DataStep;
import com.genius.appdesign2.data.DataModule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGEncoder;

public class ContentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView, rcViewManual;
    private FirebaseFirestore db;
    private ModuleAdapter adapter;
    private ManualAdapter manualAdapter;
    private ArrayList<DataModule> arrayList = new ArrayList<>();
    private ArrayList<DataManuals> manualsArrayList = new ArrayList<>();
    private ImageView imageView;
    private FloatingActionButton floatingActionButton, fabSwitch;
    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;
    private ConstraintLayout layout;
    private String mTitle, mid;
    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        getSupportActionBar().hide();
        textView = findViewById(R.id.activity_contents_tvTitle);
        ImageView imageView1 = findViewById(R.id.activity_contents_imageview1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitle = getIntent().getStringExtra("machine_title");
        mid = getIntent().getStringExtra("mid");


        recyclerView = findViewById(R.id.activity_contents_rcView);
        imageView = findViewById(R.id.activity_contents_imageview);
        floatingActionButton = findViewById(R.id.activity_contents_bQR);
        layout = findViewById(R.id.activity_contents_layout2);
        rcViewManual = findViewById(R.id.activity_contents_rcViewManual);
        fabSwitch = findViewById(R.id.activity_contents_bSwitch);

        recyclerView.setVisibility(View.VISIBLE);
        rcViewManual.setVisibility(View.GONE);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    if (layout.getVisibility() == View.VISIBLE){
                    layout.setVisibility(View.GONE);
                }else {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(mid, null, QRGContents.Type.TEXT, smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        imageView.setImageBitmap(bitmap);
                        layout.setVisibility(View.VISIBLE);
                    } catch (WriterException e) {
                        Log.v("QR Code Activity", e.toString());
                    }
                }

             */

                if (recyclerView.getVisibility() == View.VISIBLE){
                    Intent intent = new Intent(ContentsActivity.this, AddModuleActivity.class);
                    intent.putExtra("mid", mid);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(ContentsActivity.this, AddManualActivity.class);
                    intent.putExtra("mid", mid);
                    startActivity(intent);
                }
            }

        });

        fabSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.VISIBLE){
                    recyclerView.setVisibility(View.GONE);
                    rcViewManual.setVisibility(View.VISIBLE);
                    textView.setText(mTitle + " - Manuals");

                }else {
                    rcViewManual.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setText(mTitle + " - Modules");

                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ModuleAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        rcViewManual.setLayoutManager(new LinearLayoutManager(this));
        rcViewManual.setHasFixedSize(true);
        manualAdapter = new ManualAdapter(this, manualsArrayList);
        rcViewManual.setAdapter(manualAdapter);


        db = FirebaseFirestore.getInstance();



    }
    public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataModule> list=new ArrayList<>();

        public ModuleAdapter(Context context, ArrayList<DataModule> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ModuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_module, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ModuleAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvValue.setText(list.get(position).getDesc().toString());

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ComponentActivity.class);

                    intent.putExtra("module_title", list.get(position).getTitle());
                    intent.putExtra("module_id", list.get(position).getKey());
                    startActivity(intent);
                }
            });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvValue;
            ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_module_tvTitle);
                tvValue = itemView.findViewById(R.id.list_module_tvDesc);
                layout = itemView.findViewById(R.id.list_module_layout);

            }
        }
    }

    public class ManualAdapter extends RecyclerView.Adapter<ManualAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataManuals> list=new ArrayList<>();

        public ManualAdapter(Context context, ArrayList<DataManuals> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ManualAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manuals, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ManualAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvDesc.setText(list.get(position).getDesc().toString());
          //  Picasso.get().load("https:" + list.get(position).getUrl()).into(imageView);


            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StepsActivity.class);

                    intent.putExtra("manual_title", list.get(position).getTitle());
                    intent.putExtra("manual_id", list.get(position).getKey());
                    startActivity(intent);
                }
            });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvDesc;
            ConstraintLayout layout;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_manuals_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_manuals_tvDesc);
                layout = itemView.findViewById(R.id.list_manuals_layout);
            }
        }
    }


 /*   public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataContent> list=new ArrayList<>();

        public ContentsAdapter(Context context, ArrayList<DataContent> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ContentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contents, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ContentsAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvCreated.setText(list.get(position).getCreatedAt());

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StepsActivity.class);

                    intent.putExtra("content_title", list.get(position).getTitle());
                    intent.putExtra("cid", list.get(position).getKey());
                    startActivity(intent);
                }
            });


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvDesc, tvCreated;
            ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_contents_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_contents_tvDesc);
                tvCreated = itemView.findViewById(R.id.list_contents_tvCreated);
                layout = itemView.findViewById(R.id.list_contents_layout);

            }
        }
    }

  */

    @Override
    protected void onResume() {
        super.onResume();

        if (mTitle!= null && mid != null){

            textView.setText(mTitle + " - Modules");

            db.collection("moduleData")
                    .whereEqualTo("mid", mid)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null &&
                                        snapshot.getData().get("mid") != null) {
                                    arrayList.add(new DataModule(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString()));
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Contents", "Missing Parameters");
                                }
                            }
                        }
                    });

            db.collection("manualData")
                    .whereEqualTo("mid", mid)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            manualsArrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("mid") != null) {
                                    manualsArrayList.add(new DataManuals(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString()));
                                    manualAdapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Contents", "Missing Parameters");
                                }
                            }

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("MachineActivity", "New machine: " + dc.getDocument().getData());
                                        break;
                                    case MODIFIED:
                                        Log.d("MachineActivity", "Modified machine: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        Log.d("MachineActivity", "Removed machine: " + dc.getDocument().getData());
                                        break;
                                }
                            }
                        }
                    });
        }
    }
}