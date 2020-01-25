package co.prestoapp.www.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import co.prestoapp.www.Models.VendorDetailsModel;

import java.util.ArrayList;

public class CartSQLiteAdapter {

    SQLiteHelper sqLiteHelper;
    public CartSQLiteAdapter(Context context){
        sqLiteHelper = new SQLiteHelper(context);
    }

    public long insert(Integer ProductId,String VendorId,String VendorName,String Type,String ProductName,String ProductPrice,String ProductSize,String ProductQuantity,String ProductDescription,String OrderType){
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.ID,ProductId);
        contentValues.put(SQLiteHelper.VENDOR_ID,VendorId);
        contentValues.put(SQLiteHelper.VENDOR_NAME,VendorName);
        contentValues.put(SQLiteHelper.TYPE,Type);
        contentValues.put(SQLiteHelper.PRODUCT_NAME,ProductName);
        contentValues.put(SQLiteHelper.PRODUCT_PRICE,ProductPrice);
        contentValues.put(SQLiteHelper.PRODUCT_SIZE,ProductSize);
        contentValues.put(SQLiteHelper.PRODUCT_QUANTITY,ProductQuantity);
        contentValues.put(SQLiteHelper.PRODUCT_DESCRIPTION,ProductDescription);
        long id =sqLiteDatabase.insert(SQLiteHelper.TABLE_NAME,null,contentValues);
        return id;
    }


    public int update(Integer id,String newProductQuantity){
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.PRODUCT_QUANTITY,newProductQuantity);
        String[] whereArgs= {String.valueOf(id)};
        //int count = sqLiteDatabase.update(SQLiteHelper.TABLE_NAME,contentValues,SQLiteHelper.VENDOR_NAME+"=? and "+SQLiteHelper.TYPE+"=? and "+SQLiteHelper.PRODUCT_NAME+"=?",whereArgs);
        int count = sqLiteDatabase.update(SQLiteHelper.TABLE_NAME,contentValues,SQLiteHelper.ID+"=?",whereArgs);
        return count;
    }


    public int delete(Integer id){
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        //String[] whereArgs= {VendorName,Type,ProductName};
        //int count = sqLiteDatabase.delete(SQLiteHelper.TABLE_NAME,SQLiteHelper.VENDOR_NAME+"=? and "+SQLiteHelper.TYPE+"=? and "+SQLiteHelper.PRODUCT_NAME+"=?",whereArgs);
        String[] whereArgs= {String.valueOf(id)};
        int count = sqLiteDatabase.delete(SQLiteHelper.TABLE_NAME,SQLiteHelper.ID+"=?",whereArgs);
        return count;
    }

    public int deleteCustomOrder(String ProductName){
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        //String[] whereArgs= {VendorName,Type,ProductName};
        //int count = sqLiteDatabase.delete(SQLiteHelper.TABLE_NAME,SQLiteHelper.VENDOR_NAME+"=? and "+SQLiteHelper.TYPE+"=? and "+SQLiteHelper.PRODUCT_NAME+"=?",whereArgs);
        String[] whereArgs= {ProductName};
        int count = sqLiteDatabase.delete(SQLiteHelper.TABLE_NAME,SQLiteHelper.PRODUCT_NAME+"=?",whereArgs);
        return count;
    }

    public void deleteAll(){
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+ SQLiteHelper.TABLE_NAME);

    }

    public ArrayList<VendorDetailsModel> get(){
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        String[] columns = {SQLiteHelper.ID,SQLiteHelper.VENDOR_ID,SQLiteHelper.VENDOR_NAME,SQLiteHelper.TYPE,SQLiteHelper.PRODUCT_NAME,SQLiteHelper.PRODUCT_PRICE,SQLiteHelper.PRODUCT_SIZE,SQLiteHelper.PRODUCT_QUANTITY,SQLiteHelper.PRODUCT_DESCRIPTION};
        Cursor cursor = sqLiteDatabase.query(SQLiteHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();
        ArrayList<VendorDetailsModel> vendorDetailsModelList=new ArrayList<VendorDetailsModel>();
        while (cursor.moveToNext())
        {
            Integer id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.ID));
            String vendor_id = cursor.getString(cursor.getColumnIndex(SQLiteHelper.VENDOR_ID));
            String vendor = cursor.getString(cursor.getColumnIndex(SQLiteHelper.VENDOR_NAME));
            String type =  cursor.getString(cursor.getColumnIndex(SQLiteHelper.TYPE));
            String name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PRODUCT_NAME));
            String price = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PRODUCT_PRICE));
            String size = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PRODUCT_SIZE));
            String quantity = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PRODUCT_QUANTITY));
            String description =cursor.getString(cursor.getColumnIndex(SQLiteHelper.PRODUCT_DESCRIPTION));

            vendorDetailsModelList.add(new VendorDetailsModel(id,vendor_id,vendor,name,price,size,description,type,quantity));
        }
    return vendorDetailsModelList;
    }


    public String search(Integer id) {

        String quantity="";
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        String[] whereArgs= {String.valueOf(id)};
//        Cursor cursor = sqLiteDatabase.query(SQLiteHelper.TABLE_NAME,
//                new String[]{SQLiteHelper.PRODUCT_QUANTITY},
//                SQLiteHelper.VENDOR_NAME + " =? AND " +SQLiteHelper.TYPE + " =? AND " +SQLiteHelper.PRODUCT_NAME + " =?",
//                whereArgs, null, null, null, null);

        Cursor cursor = sqLiteDatabase.query(SQLiteHelper.TABLE_NAME,
                new String[]{SQLiteHelper.PRODUCT_QUANTITY}, SQLiteHelper.ID + " =?", whereArgs, null, null, null, null);

        if(cursor.getCount() > 0){
            // means search has returned data

            if (cursor.moveToFirst()) {
                do {
                    quantity = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PRODUCT_QUANTITY));

                } while (cursor.moveToNext());
            }

        }
        return quantity;
    }


    public long  counter() {
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(sqLiteDatabase,SQLiteHelper.TABLE_NAME);
        sqLiteDatabase.close();
        return count;
    }




    static class SQLiteHelper extends SQLiteOpenHelper{

        private static final String DATABASE_NAME = "CartDataBase";    // Database Name
        private static final String TABLE_NAME = "Orders";   // Table Name
        private static final Integer DATABASE_Version = 1;    // Database Version
        private static final String ID="productId";
        private static final String VENDOR_ID="VendorId";
        private static final String VENDOR_NAME = "VendorName";
        private static final String TYPE= "Type";
        private static final String PRODUCT_NAME = "productName";
        private static final String PRODUCT_PRICE = "productPrice";
        private static final String PRODUCT_SIZE = "productSize";
        private static final String PRODUCT_QUANTITY = "ProductQuantity";
        private static final String PRODUCT_DESCRIPTION = "ProductDescription";

        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID+" VARCHAR(255) ,"+VENDOR_ID+"  VARCHAR(255) ,"+VENDOR_NAME+" VARCHAR(255) ,"+ TYPE+" VARCHAR(255) ,"+ PRODUCT_NAME+" VARCHAR(255) ,"
        +PRODUCT_PRICE+" VARCHAR(255) ,"+PRODUCT_SIZE+" VARCHAR(255) ,"+PRODUCT_QUANTITY+" VARCHAR(255) ,"+PRODUCT_DESCRIPTION+" VARCHAR(255));";

        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public SQLiteHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_Version);
            this.context = context;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            }
            catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                db.execSQL(DROP_TABLE);
            }
            catch (Exception e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
