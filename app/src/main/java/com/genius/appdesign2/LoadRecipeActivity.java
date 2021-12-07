package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.genius.appdesign2.data.DataLoad;
import com.genius.appdesign2.data.DataRecipe;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class LoadRecipeActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private ArrayList<DataLoad> arrayList= new ArrayList<>();
    private LoadAdapter adapter;
    public static ArrayList<DataRecipe> recipeArrayList = new ArrayList<>();
    private ImageButton fab;
    private TextView tvBlank;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_recipe);

        getSupportActionBar().hide();

        TextView tvTitle = findViewById(R.id.activity_load_recipe_tvTitle);
        tvTitle.setText("Recipes");
        ImageView imageView = findViewById(R.id.activity_load_reicpe_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("recipes");
        recyclerView = findViewById(R.id.activity_load_rcView);
        fab = findViewById(R.id.activity_load_recipe_fab);
        tvBlank = findViewById(R.id.activity_load_recipe_tvBlank);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new LoadAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoadRecipeActivity.this, LyoActivity.class));
            }
        });

        firestore.collection("recipes")
                .whereEqualTo("mid", SplashActivity.machine_id)
                .addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                            dataRecipes = getRecipeData(snapshot.getId());
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {
                                arrayList.add(new DataLoad(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("mid").toString(),
                                        dataRecipes));
                                Log.e("Sample", snapshot.getData().get("title").toString());
                            }
                        }
                        if (arrayList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            tvBlank.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvBlank.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
/*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                for (int i=0; i< snapshot.child("arrayList").getChildrenCount(); i++){
                    dataRecipes.add(i, snapshot.child("arrayList").child(String.valueOf(i)).getValue(DataRecipe.class));
                    Log.e("Recipe", dataRecipes.get(i).getStep());
                }
                arrayList.add(new DataLoad(snapshot.child("key").getValue().toString(),
                        snapshot.child("title").getValue().toString(), dataRecipes));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


 */
    }

    private ArrayList<DataRecipe> getRecipeData(String recipe_id){

        Log.e("Load", recipe_id);

        ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
        firestore.collection("recipeeData")
                .whereEqualTo("rid", recipe_id)
                .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            dataRecipes.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("index") != null && snapshot.getData().get("step") != null && snapshot.getData().get("rid") != null &&
                                    snapshot.getData().get("temp1") != null && snapshot.getData().get("time1") != null && snapshot.getData().get("time2") != null
                                    && snapshot.getData().get("pressure") != null) {

                                dataRecipes.add(new DataRecipe(Integer.parseInt(snapshot.getData().get("index").toString()),
                                        snapshot.getData().get("step").toString(),
                                        snapshot.getData().get("rid").toString(),
                                        Integer.parseInt(snapshot.getData().get("temp1").toString()),
                                        Integer.parseInt(snapshot.getData().get("time1").toString()),
                                        Integer.parseInt(snapshot.getData().get("time2").toString()),
                                        Integer.parseInt(snapshot.getData().get("pressure").toString())));
                                //   Log.e("Sample", snapshot.getData().get("index").toString() + snapshot.getData().get("step").toString());
                            }else {
                                Log.e("LYO: ", "Missing some parametere");
                            }
                        }

                            dataRecipes.sort(new Comparator<DataRecipe>() {
                                @Override
                                public int compare(DataRecipe dataRecipe, DataRecipe dataRecipe1) {
                                    return Integer.compare(dataRecipe.getIndex(), dataRecipe1.getIndex());
                                }
                            });

                        for (int i =0; i< dataRecipes.size(); i++){
                            Log.e("datarecipes ", "Index:" +dataRecipes.get(i).getIndex());
                        }
                    }
                    }
                });


        return dataRecipes;
    }

    public class LoadAdapter extends RecyclerView.Adapter<LoadAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataLoad> list=new ArrayList<>();

        public LoadAdapter(Context context, ArrayList<DataLoad> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public LoadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_load_recipe, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull LoadAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle());
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.recyclerView.getVisibility()== View.VISIBLE){
                        holder.recyclerView.setVisibility(View.GONE);
                        holder.layout.setVisibility(View.GONE);
                        holder.button.setVisibility(View.GONE);
                    }else {
                        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        holder.recyclerView.setHasFixedSize(true);
                        RecipeAdapter recipeAdapter = new RecipeAdapter(context, list.get(position).getArrayList());
                        holder.recyclerView.setAdapter(recipeAdapter);
                        recipeAdapter.notifyDataSetChanged();
                        holder.layout.setVisibility(View.VISIBLE);
                        holder.recyclerView.setVisibility(View.VISIBLE);
                        holder.button.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recipeArrayList = new ArrayList<>();
                    for (int i=0; i<list.get(position).getArrayList().size(); i++){
                        recipeArrayList.add(i, list.get(position).getArrayList().get(i));
                    }
                    Intent intent =  new Intent(LoadRecipeActivity.this, ChartActivity.class);
                    intent.putExtra("recipe_name", arrayList.get(position).getTitle());
                    intent.putExtra("recipe_id", arrayList.get(position).getKey());
                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView imageView;
            RecyclerView recyclerView;
            LinearLayout layout;
            Button button;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_load_recipe_tvTitle);
                imageView = itemView.findViewById(R.id.list_load_recipe_bShow);
                recyclerView = itemView.findViewById(R.id.list_load_recipe_rcView);
                layout = itemView.findViewById(R.id.activity_load_recipe_layout);
                button = itemView.findViewById(R.id.list_load_recipe_bLoad);
            }
        }
    }

    public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataRecipe> list=new ArrayList<>();

        public RecipeAdapter(Context context, ArrayList<DataRecipe> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lyo, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, final int position) {
            //    holder.tvKey.setText(arrayList.get(position).getKey());
            holder.tvId.setText(list.get(position).getStep());
            holder.tvTemp.setText(list.get(position).getTemp1() + "");
            holder.tvTime.setText(list.get(position).getTime1() + "");
            holder.tvTime2.setText(list.get(position).getTime2() + "");
            holder.tvPressure.setText(list.get(position).getPressure() + "");

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvId, tvTemp, tvTime, tvTime2, tvPressure;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvId = itemView.findViewById(R.id.list_lyo_tvId);
                tvTemp = itemView.findViewById(R.id.list_lyo_tvTemp);
                tvTime = itemView.findViewById(R.id.list_lyo_tvTime);
                tvTime2 = itemView.findViewById(R.id.list_lyo_tvTime2);
                tvPressure = itemView.findViewById(R.id.list_lyo_tvPressure);
            }
        }
    }

}