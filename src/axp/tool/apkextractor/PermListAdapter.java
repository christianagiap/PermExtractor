package axp.tool.apkextractor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PermListAdapter extends RecyclerView.Adapter<PermListAdapter.ViewHolder> {

    private List<PermItem> listItems;
    private PermissionActivity context;

    public PermListAdapter(List<PermItem> listItems, PermissionActivity context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public PermListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.perm_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PermListAdapter.ViewHolder viewHolder, final int i) {
        final PermItem permItem = listItems.get(i);

        viewHolder.txtPermName.setText(permItem.getName());

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permItem.getName().equals("NO PERMISSIONS USED BY THIS APP") == false) {
                    Intent intent = new Intent(context, SpecificPermActivity.class);
                    intent.putStringArrayListExtra("permissions", permItem.getPermList());
                    intent.putExtra("name", permItem.getName());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtPermName;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPermName = (TextView)itemView.findViewById(R.id.txtPermName);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.card_view2);

        }
    }
}
