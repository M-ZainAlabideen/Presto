package co.prestoapp.www.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import co.prestoapp.www.Views.CartActivity;
import co.prestoapp.www.Models.VendorDetailsModel;
import co.prestoapp.www.R;
import co.prestoapp.www.Views.VendorDetailsActivity;

import java.util.ArrayList;

public class VendorDetailsAdapter extends RecyclerView.Adapter<VendorDetailsAdapter.viewHolder>{

    private ArrayList<VendorDetailsModel> vendorDetailsModelList;
    private ArrayList<VendorDetailsModel> vendorDetailsSelectedModelList;
    private Context context;
    private String QuantityStr;
    private int QuantityInt;
    private CartSQLiteAdapter cartSQLiteAdapter;
    private String quantityReceiveSQLite;


    public VendorDetailsAdapter(ArrayList<VendorDetailsModel> vendorDetailsModelList,Context context){
        this.vendorDetailsModelList=vendorDetailsModelList;
        this.context=context;
        cartSQLiteAdapter = new CartSQLiteAdapter(context);
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        private TextView Price;
        private TextView Name;
        private ImageView Add;
        private ImageView Remove;
        private TextView Quantity;
        private TextView Size;
        private TextView Description;



        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Price = (TextView) itemView.findViewById(R.id.vendor_details_text_Price);
            Name = (TextView)itemView.findViewById(R.id.vendor_details_text_Name);
            Add = (ImageView)itemView.findViewById(R.id.vendor_details_image_Add);
            Remove = (ImageView)itemView.findViewById(R.id.vendor_details_image_Remove);
            Quantity = (TextView)itemView.findViewById(R.id.vendor_details_text_Quantity);
            Size = (TextView) itemView.findViewById(R.id.vendor_details_text_Size);
            Description = (TextView)itemView.findViewById(R.id.vendor_details_text_Description);

        }
    }


    @NonNull
    @Override
    public VendorDetailsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vendor_details_item,viewGroup,
                false);

         VendorDetailsAdapter.viewHolder myViewHolder = new VendorDetailsAdapter.viewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final VendorDetailsAdapter.viewHolder holder, final int position) {

        if(vendorDetailsModelList.get(position).getPrice().equals("0"))
        {
            holder.Name.setText(vendorDetailsModelList.get(position).getName());
            holder.Remove.setVisibility(View.VISIBLE);
            holder.Price.setText("EGP00.00");
            holder.Size.setVisibility(View.INVISIBLE);
            holder.Description.setText(vendorDetailsModelList.get(position).getDescription());
            holder.Description.setTextColor(Color.parseColor("#E86C5B"));
            holder.Quantity.setVisibility(View.INVISIBLE);
            holder.Add.setVisibility(View.INVISIBLE);

        }
        else {
            holder.Name.setText(vendorDetailsModelList.get(position).getName());
            if(!vendorDetailsModelList.get(position).getPrice().contains("EGP"))
                holder.Price.setText("EGP"+vendorDetailsModelList.get(position).getPrice());
            else
                holder.Price.setText(vendorDetailsModelList.get(position).getPrice());
            String sizeStr = vendorDetailsModelList.get(position).getSize();
            if (sizeStr != null) {
                holder.Size.setVisibility(View.VISIBLE);
                holder.Size.setText("  " + vendorDetailsModelList.get(position).getSize() + "  ");
            }
            String descStr = vendorDetailsModelList.get(position).getDescription();
            if (descStr != null) {
                holder.Description.setText(vendorDetailsModelList.get(position).getDescription());
            }
            holder.Quantity.setText(vendorDetailsModelList.get(position).getNumberOfOrders());

            if (vendorDetailsModelList.get(position).getNumberOfOrders().equals(""))
                holder.Remove.setVisibility(View.INVISIBLE);
            else
                holder.Remove.setVisibility(View.VISIBLE);

            vendorDetailsSelectedModelList = cartSQLiteAdapter.get();

            for (int i = 0; i < vendorDetailsSelectedModelList.size(); i++) {
                if (vendorDetailsModelList.get(position).getId() == vendorDetailsSelectedModelList.get(i).getId()) {
                    holder.Quantity.setText(vendorDetailsSelectedModelList.get(i).getQuantity() + "x");
                    holder.Remove.setVisibility(View.VISIBLE);
                }

            }

        }

        holder.Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.Remove.setVisibility(View.VISIBLE);

                if(!context.getClass().getSimpleName().equals("CartActivity")) {
                    VendorDetailsActivity.CheckOrder.setVisibility(View.VISIBLE);
                    VendorDetailsActivity.AddProduct.setVisibility(View.VISIBLE);
                }
                else if(context.getClass().getSimpleName().equals("CartActivity"))
                {
                    CartActivity.PromoCode.setVisibility(View.VISIBLE);
                    CartActivity.RightPromoCode.setVisibility(View.GONE);
                }

                QuantityStr = holder.Quantity.getText().toString();
                if(QuantityStr.matches("") || QuantityStr==null)
                {
                    QuantityInt=1;
                    holder.Quantity.setText(1+"x");
                    QuantityStr = 1+"x";

                }
                else
                {
                    char[] QuantityChars = QuantityStr.toCharArray();
                    QuantityStr="";
                    for (int i=0;i<QuantityChars.length-1;i++)
                    {
                        QuantityStr = QuantityStr+QuantityChars[i];
                    }
                    QuantityInt = Integer.parseInt(QuantityStr);
                    QuantityStr = ++QuantityInt+"x";
                    holder.Quantity.setText(QuantityStr);
                }
                vendorDetailsModelList.get(position).setNumberOfOrders(QuantityStr);

                quantityReceiveSQLite = cartSQLiteAdapter.search(vendorDetailsModelList.get(position).getId());
                if(quantityReceiveSQLite.matches("") || quantityReceiveSQLite==null)
                {
                    cartSQLiteAdapter.insert(vendorDetailsModelList.get(position).getId(),vendorDetailsModelList.get(position).getVendor_id()
                            ,VendorDetailsActivity.Title.getText().toString(),vendorDetailsModelList.get(position).getType(),vendorDetailsModelList.get(position).getName()
                            ,vendorDetailsModelList.get(position).getPrice(),vendorDetailsModelList.get(position).getSize(),String.valueOf(QuantityInt),vendorDetailsModelList.get(position).getDescription(),"false");
                    if(context.getClass().getSimpleName().equals("CartActivity"))
                    {
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition( 0, 0);
                        Intent intent = new Intent(context, CartActivity.class);
                        intent.putExtra("POSITION",position);
                        ((Activity)context).startActivity(intent);
                        ((Activity)context).overridePendingTransition( 0, 0);
                    }
                    if(context.getClass().getSimpleName().equals("VendorDetailsActivity"))
                    {
                        VendorDetailsActivity.Badge.setVisibility(View.VISIBLE);
                        VendorDetailsActivity.OrdersCounter.setVisibility(View.VISIBLE);
                        VendorDetailsActivity.OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
                    }
                }

                else
                {
                        cartSQLiteAdapter.update(vendorDetailsModelList.get(position).getId(),String.valueOf(QuantityInt));
                    if(context.getClass().getSimpleName().equals("CartActivity"))
                    {
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition( 0, 0);
                        Intent intent = new Intent(context, CartActivity.class);
                        intent.putExtra("POSITION",position);
                        ((Activity)context).startActivity(intent);
                        ((Activity)context).overridePendingTransition( 0, 0);
                    }

                        if(context.getClass().getSimpleName().equals("VendorDetailsActivity"))
                        {

                            VendorDetailsActivity.OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));


                    }
                }

            }
        });

        holder.Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vendorDetailsModelList.get(position).getPrice().equals("0") )
                {
                    cartSQLiteAdapter.deleteCustomOrder(vendorDetailsModelList.get(position).getName());
                    if(context.getClass().getSimpleName().equals("CartActivity"))
                    {
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition( 0, 0);
                        Intent intent = new Intent(context, CartActivity.class);
                        intent.putExtra("POSITION",position);
                        ((Activity)context).startActivity(intent);
                        ((Activity)context).overridePendingTransition( 0, 0);
                        CartActivity.PromoCode.setVisibility(View.VISIBLE);
                        CartActivity.RightPromoCode.setVisibility(View.GONE);

                    }

                }
                else
                {
                    QuantityStr = holder.Quantity.getText().toString();
                if (QuantityStr.equals("1x")) {
                    holder.Remove.setVisibility(View.INVISIBLE);
                    holder.Quantity.setText("");
                    QuantityStr = "";
                    QuantityInt = 0;
                } else {
                    char[] QuantityChars = QuantityStr.toCharArray();
                    QuantityStr = "";
                    for (int i = 0; i < QuantityChars.length - 1; i++) {
                        QuantityStr = QuantityStr + QuantityChars[i];
                    }
                    QuantityInt = Integer.parseInt(QuantityStr);
                    QuantityInt = --QuantityInt;
                    QuantityStr = QuantityInt + "x";
                    holder.Quantity.setText(QuantityStr);
                }

                vendorDetailsModelList.get(position).setNumberOfOrders(QuantityStr);
                quantityReceiveSQLite = cartSQLiteAdapter.search(vendorDetailsModelList.get(position).getId());
                char[] QuantityChars = quantityReceiveSQLite.toCharArray();
                quantityReceiveSQLite = "";
                for (int i = 0; i < QuantityChars.length; i++) {
                    quantityReceiveSQLite = quantityReceiveSQLite + QuantityChars[i];
                }
                if (!quantityReceiveSQLite.matches("") && quantityReceiveSQLite!=null)
                {
                    int quantityReceiveSQLiteInt = Integer.parseInt(quantityReceiveSQLite);
                    if(quantityReceiveSQLiteInt==1)
                    {
                        cartSQLiteAdapter.delete(vendorDetailsModelList.get(position).getId());
                        if(cartSQLiteAdapter.get().size()==0)
                        {
                            if(context.getClass().getSimpleName().equals("VendorDetailsActivity")) {
                                VendorDetailsActivity.AddProduct.setVisibility(View.INVISIBLE);
                                VendorDetailsActivity.CheckOrder.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(context.getClass().getSimpleName().equals("CartActivity"))
                        {
                            ((Activity)context).finish();
                            ((Activity)context).overridePendingTransition( 0, 0);
                            Intent intent = new Intent(context, CartActivity.class);
                            intent.putExtra("POSITION",position);
                            ((Activity)context).startActivity(intent);
                            ((Activity)context).overridePendingTransition( 0, 0);
                        }


                            if(context.getClass().getSimpleName().equals("VendorDetailsActivity"))
                            {
                                if(cartSQLiteAdapter.counter()==0)
                                {
                                    VendorDetailsActivity.Badge.setVisibility(View.INVISIBLE);
                                    VendorDetailsActivity.OrdersCounter.setVisibility(View.INVISIBLE);

                        }
                        else
                                {
                                    VendorDetailsActivity.OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
                                }
                            }

                    }

                    else if(quantityReceiveSQLiteInt>1)
                    {
                        cartSQLiteAdapter.update(vendorDetailsModelList.get(position).getId(),String.valueOf(QuantityInt));
                        if(context.getClass().getSimpleName().equals("CartActivity"))
                        {
                            ((Activity)context).finish();
                            ((Activity)context).overridePendingTransition( 0, 0);
                            Intent intent = new Intent(context, CartActivity.class);
                            intent.putExtra("POSITION",position);
                            ((Activity)context).startActivity(intent);
                            ((Activity)context).overridePendingTransition( 0, 0);
                        }


                            if(context.getClass().getSimpleName().equals("VendorDetailsActivity"))
                            {
                                VendorDetailsActivity.OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
                            }

                    }
                }

            }}
        });

    }


    @Override
    public int getItemCount() {
        return vendorDetailsModelList.size();
    }
}
