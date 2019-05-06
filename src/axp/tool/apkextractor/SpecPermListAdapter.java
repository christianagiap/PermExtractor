package axp.tool.apkextractor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecPermListAdapter extends RecyclerView.Adapter<SpecPermListAdapter.ViewHolder> {

    private List<PermItem> listItems;
    private SpecificPermActivity context;

    public SpecPermListAdapter(List<PermItem> listItems, SpecificPermActivity context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public SpecPermListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.perm_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecPermListAdapter.ViewHolder viewHolder, int i) {
        final PermItem permItem = listItems.get(i);

        if(permItem.getModName().equals("")){
            viewHolder.txtPermName.setText("No permissions found");
        }else {
            viewHolder.txtPermName.setText(permItem.getModName());
        }

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDescr(permItem.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public String findDescr(final String permission) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DESCR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String str = jsonObject.getString("description");

                    Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("perm_name", permission);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        return "Success";
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
