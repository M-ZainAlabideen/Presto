package co.prestoapp.www.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.prestoapp.www.Views.CategoryActivity;
import co.prestoapp.www.PicassoTrustAll;
import co.prestoapp.www.Views.VendorDetailsActivity;

import java.util.ArrayList;

import co.prestoapp.www.Models.CategoryModel;
import co.prestoapp.www.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder>{

    private ArrayList<CategoryModel> categoryModelList;
    private Context context;

    public static class viewHolder extends RecyclerView.ViewHolder{

        private ImageView Banner;
        private TextView Name;



        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Banner = (ImageView)itemView.findViewById(R.id.category_image_Banner);
            Name = (TextView)itemView.findViewById(R.id.category_text_Name);


        }
    }

    public CategoryAdapter(ArrayList<CategoryModel> categoryModelList,Context context){
        this.categoryModelList=categoryModelList;
        this.context=context;
    }



    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

             view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item,viewGroup,
                    false);
             viewHolder myViewHolder = new viewHolder(view);
             return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

        if(!categoryModelList.get(position).getBannerUrl().equals(""))
        {
            PicassoTrustAll.getInstance(context)
                    .load(categoryModelList.get(position).getBannerUrl()).fit().centerCrop().into(holder.Banner);
        }



        holder.Name.setText(categoryModelList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VendorDetailsActivity.class);
                intent.putExtra("VENDOR_ID",categoryModelList.get(position).getId());
                intent.putExtra("VENDOR_TYPE", CategoryActivity.Title.getText().toString());
                intent.putExtra("VENDOR_NAME",categoryModelList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }
}
