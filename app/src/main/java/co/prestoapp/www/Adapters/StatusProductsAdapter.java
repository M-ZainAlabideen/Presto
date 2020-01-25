package co.prestoapp.www.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.prestoapp.www.Models.StatusProductsModel;
import co.prestoapp.www.R;
import java.util.ArrayList;

public class StatusProductsAdapter extends RecyclerView.Adapter<StatusProductsAdapter.viewHolder>{


private ArrayList<StatusProductsModel> statusProductsList;
private Context context;
public static class viewHolder extends RecyclerView.ViewHolder{

    private TextView product_name;
    private TextView Price;
    private TextView qty;
    private ImageView Add;
    private ImageView Remove;


    public viewHolder(@NonNull View itemView) {
        super(itemView);
        product_name = (TextView)itemView.findViewById(R.id.vendor_details_text_Name);
        Price = (TextView)itemView.findViewById(R.id.vendor_details_text_Price);
        qty = (TextView)itemView.findViewById(R.id.vendor_details_text_Quantity);
        Add = (ImageView)itemView.findViewById(R.id.vendor_details_image_Add);
        Remove = (ImageView)itemView.findViewById(R.id.vendor_details_image_Remove);

    }
}

    public StatusProductsAdapter(ArrayList<StatusProductsModel> statusProductsList, Context context){
        this.statusProductsList=statusProductsList;
        this.context=context;
    }
    @NonNull
    @Override
    public StatusProductsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vendor_details_item,viewGroup,
                false);

        StatusProductsAdapter.viewHolder myViewHolder = new StatusProductsAdapter.viewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatusProductsAdapter.viewHolder holder, final int position) {

        holder.product_name.setText(statusProductsList.get(position).getProduct_name());
        holder.Price.setText("EGP"+statusProductsList.get(position).getPrice());
        holder.qty.setText(statusProductsList.get(position).getQty()+"x");
        holder.Add.setVisibility(View.INVISIBLE);
        holder.Remove.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return statusProductsList.size();
    }
}
