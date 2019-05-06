package axp.tool.apkextractor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecommendActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<AppItem> final_packages;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        final_packages = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); //each item has its fixed size on recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        message = (TextView) findViewById(R.id.message);

        ArrayList<String> names = getIntent().getStringArrayListExtra("names");
        ArrayList<String> icons = getIntent().getStringArrayListExtra("icons");
        ArrayList<String> urls = getIntent().getStringArrayListExtra("urls");

        if (names.isEmpty() && icons.isEmpty() && urls.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
            for (int i = 0; i < names.size(); i++) {
                AppItem appItem = new AppItem(names.get(i), icons.get(i), urls.get(i));
                final_packages.add(appItem);
            }
            RecomListAdapter adapter = new RecomListAdapter(final_packages, this);
            recyclerView.setAdapter(adapter);
        }

    }

}