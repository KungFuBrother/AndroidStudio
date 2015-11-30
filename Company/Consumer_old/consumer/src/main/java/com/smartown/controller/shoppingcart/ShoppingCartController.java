package com.smartown.controller.shoppingcart;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiger on 2015-11-27.
 */
public class ShoppingCartController {

    public static ShoppingCartController instance;

    private SQLiteDatabase db;

    private Cursor cursor;

    private void close() {
        if (db != null) {
            db.close();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static ShoppingCartController getInstance() {
        if (instance == null) {
            instance = new ShoppingCartController();
        }
        return instance;
    }

    public boolean hasProduct(String tableName, String productId) {
        if (productId == null) {
            return false;
        }
        db = DataBaseHelper.getInstance().getReadableDatabase();
        cursor = db.query(tableName, null, ModelShoppingCart.columnProductId + "=?", new String[]{productId}, null, null, null);
        boolean hasProduct = cursor.moveToFirst();
        close();
        return hasProduct;
    }

    public void addProduct(String tableName, boolean isSelected, int buyCount, String providerId, String providerName, String productId, String productObject) {
        db = DataBaseHelper.getInstance().getReadableDatabase();
        ContentValues values = new ContentValues();
        int selection = 0;
        if (isSelected) {
            selection = 1;
        }
        values.put(ModelShoppingCart.columnIsSelected, selection);
        values.put(ModelShoppingCart.columnBuyCount, buyCount);
        values.put(ModelShoppingCart.columnProviderId, providerId);
        values.put(ModelShoppingCart.columnProviderName, providerName);
        values.put(ModelShoppingCart.columnProductId, productId);
        values.put(ModelShoppingCart.columnProductObject, productObject);
        db.insert(tableName, null, values);
        close();
    }

    public void saveChangedShoppingCart(String tableName, List<ModelShoppingCart> shoppingCarts) {
        removeAllProduct(tableName);
        for (int i = 0; i < shoppingCarts.size(); i++) {
            addProduct(tableName, shoppingCarts.get(i).isSelected(), shoppingCarts.get(i).getBuyCount(), shoppingCarts.get(i).getProviderId(), shoppingCarts.get(i).getProviderName(), shoppingCarts.get(i).getProductId(), shoppingCarts.get(i).getProductObject());
        }
    }

    public void removeAllProduct(String tableName) {
        db = DataBaseHelper.getInstance().getReadableDatabase();
        db.delete(tableName, null, null);
        close();
    }

    public void removeProducts(String tableName, List<String> productIds) {
        if (productIds == null) {
            return;
        }
        db = DataBaseHelper.getInstance().getReadableDatabase();
        for (int i = 0; i < productIds.size(); i++) {
            db.delete(tableName, ModelShoppingCart.columnProductId + " =?", new String[]{productIds.get(i)});
        }
        close();
    }

    public void removeSelectedProducts(String tableName) {
        db = DataBaseHelper.getInstance().getReadableDatabase();
        db.delete(tableName, ModelShoppingCart.columnIsSelected + " =?", new String[]{"1"});
        close();
    }

    public List<ModelShoppingCart> getAllProducts(String tableName) {
        db = DataBaseHelper.getInstance().getReadableDatabase();
        List<ModelShoppingCart> shoppingCarts = new ArrayList<>();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ModelShoppingCart shoppingCart = new ModelShoppingCart();
                shoppingCart.setBuyCount(cursor.getInt(cursor.getColumnIndex(ModelShoppingCart.columnBuyCount)));
                shoppingCart.setIsSelected(cursor.getInt(cursor.getColumnIndex(ModelShoppingCart.columnIsSelected)) == 1);
                shoppingCart.setProviderId(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProviderId)));
                shoppingCart.setProviderName(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProviderName)));
                shoppingCart.setProductId(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProductId)));
                shoppingCart.setProductObject(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProductObject)));
                shoppingCarts.add(shoppingCart);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return shoppingCarts;
    }

    public List<ModelShoppingCart> getSelectedProducts(String tableName) {
        db = DataBaseHelper.getInstance().getReadableDatabase();
        List<ModelShoppingCart> shoppingCarts = new ArrayList<>();
        Cursor cursor = db.query(tableName, null, ModelShoppingCart.columnIsSelected + " =?", new String[]{"1"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ModelShoppingCart shoppingCart = new ModelShoppingCart();
                shoppingCart.setBuyCount(cursor.getInt(cursor.getColumnIndex(ModelShoppingCart.columnBuyCount)));
                shoppingCart.setIsSelected(cursor.getInt(cursor.getColumnIndex(ModelShoppingCart.columnIsSelected)) == 1);
                shoppingCart.setProviderId(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProviderId)));
                shoppingCart.setProviderName(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProviderName)));
                shoppingCart.setProductId(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProductId)));
                shoppingCart.setProductObject(cursor.getString(cursor.getColumnIndex(ModelShoppingCart.columnProductObject)));
                shoppingCarts.add(shoppingCart);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return shoppingCarts;
    }
}
