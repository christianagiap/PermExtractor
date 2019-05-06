package axp.tool.apkextractor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class RecomListAdapter extends RecyclerView.Adapter<RecomListAdapter.ViewHolder> {

    private List<AppItem> listItems;
    private RecommendActivity context;

    public RecomListAdapter(List<AppItem> listItems, RecommendActivity context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recom_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecomListAdapter.ViewHolder viewHolder, final int i) {
        final AppItem appItem = listItems.get(i);

        viewHolder.txtApp.setText(appItem.getPackage_name());
        new DownloadImageTask(viewHolder.imgApp).execute(appItem.icon);

        viewHolder.layoutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(appItem.url));
                context.startActivity(viewIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtApp;
        public LinearLayout layoutApp;
        public ImageView imgApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtApp = (TextView)itemView.findViewById(R.id.txtApp);
            imgApp = (ImageView) itemView.findViewById(R.id.imgApp);
            layoutApp = (LinearLayout)itemView.findViewById(R.id.card_view3);

        }
    }
}
