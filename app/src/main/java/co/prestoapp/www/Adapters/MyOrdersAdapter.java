package co.prestoapp.www.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.prestoapp.www.Models.MyOrdersModel;
import co.prestoapp.www.R;

import co.prestoapp.www.Views.StatusActivity;
import co.prestoapp.www.WebServices.Models.Order;

import java.util.ArrayList;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.viewHolder>{


    private ArrayList<MyOrdersModel> myOrdersModelList;
    private ArrayList<Order> ordersModelList;
    ArrayList<String> colors;
    private Context context;
    public static class viewHolder extends RecyclerView.ViewHolder{

        private TextView Date;
        private TextView Status;
        private TextView Price;
        private TextView Address;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Date = (TextView)itemView.findViewById(R.id.my_orders_text_Date);
            Status = (TextView)itemView.findViewById(R.id.my_orders_text_Status);
            Price = (TextView)itemView.findViewById(R.id.my_orders_text_Price);
            Address = (TextView)itemView.findViewById(R.id.my_orders_text_Address);

        }
    }

    public MyOrdersAdapter(ArrayList<MyOrdersModel> myOrdersModelList, ArrayList<Order> ordersModelList,ArrayList<String> colors, Context context){
        this.myOrdersModelList=myOrdersModelList;
        this.ordersModelList = ordersModelList;
        this.colors = colors;
        this.context=context;
    }
    @NonNull
    @Override
    public MyOrdersAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_orders_item,viewGroup,
                false);

        MyOrdersAdapter.viewHolder myViewHolder = new MyOrdersAdapter.viewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.viewHolder holder, final int position) {

        holder.Date.setText(myOrdersModelList.get(position).getDate());
        holder.Status.setText(myOrdersModelList.get(position).getStatus());
        holder.Status.setTextColor(Color.parseColor(colors.get(position)));
        holder.Price.setText(myOrdersModelList.get(position).getPrice());
        holder.Address.setText(myOrdersModelList.get(position).getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order details =ordersModelList.get(position);
                Intent intent = new Intent(context, StatusActivity.class);
                intent.putExtra("DATE",myOrdersModelList.get(position).getDate());
                intent.putExtra("ORDER_DETAILS",details);
                intent.putExtra("COME_FROM","my_orders");

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myOrdersModelList.size();
    }
}
