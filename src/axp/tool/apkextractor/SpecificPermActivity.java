package axp.tool.apkextractor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpecificPermActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<PermItem> permList;
    private TextView permTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_perm);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); //each item has its fixed size on recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        permList = new ArrayList<>();

        Intent intent = getIntent();
        ArrayList<String> permissions = intent.getStringArrayListExtra("permissions");
        String name = intent.getStringExtra("name");

        permTitle = (TextView) findViewById(R.id.permTitle);
        permTitle.setText(name + " Permissions");

        launchPermissions(permissions);

        SpecPermListAdapter adapter = new SpecPermListAdapter(permList, this);
        recyclerView.setAdapter(adapter);
    }

    public void launchPermissions(ArrayList<String> permissions){
        for(int i = 0; i<permissions.size(); i++){
            PermItem permItem = new PermItem(permissions.get(i), "");
            permList.add(permItem);
        }
    }
}
