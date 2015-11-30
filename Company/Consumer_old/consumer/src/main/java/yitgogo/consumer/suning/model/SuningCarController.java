package yitgogo.consumer.suning.model;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SuningCarController {

    static SuningCarDataManager dataManager;

    public static void init(Context context) {
        dataManager = new SuningCarDataManager(context);
    }

    public static List<ModelSuningCar> getSuningCars() {
        List<ModelSuningCar> suningCars = new ArrayList<>();
        List<ContentValues> contentValues = dataManager.getDatas();
        if (contentValues != null) {
            for (int i = 0; i < contentValues.size(); i++) {
                suningCars.add(new ModelSuningCar(
                        contentValues.get(i).getAsString(SuningCarDataManager.column_object),
                        contentValues.get(i).getAsInteger(SuningCarDataManager.column_count),
                        contentValues.get(i).getAsInteger(SuningCarDataManager.column_selection)));
            }
        }
        return suningCars;
    }

    public static boolean containProduct(String sku) {
        return dataManager.containData(sku);
    }

    public static boolean addProduct(ModelProductDetail productDetail) {
        if (!containProduct(productDetail.getSku())) {
            ContentValues values = new ContentValues();
            values.put(SuningCarDataManager.column_sku, productDetail.getSku());
            values.put(SuningCarDataManager.column_object, productDetail.getJsonObject().toString());
            values.put(SuningCarDataManager.column_count, 1);
            values.put(SuningCarDataManager.column_selection, 1);
            dataManager.insertData(values);
            return true;
        }
        return false;
    }

    public static void addCount(String sku) {
        ContentValues contentValues = dataManager.getData(sku);
        if (contentValues != null) {
            ContentValues values = new ContentValues();
            values.put(SuningCarDataManager.column_sku, contentValues.getAsString(SuningCarDataManager.column_sku));
            values.put(SuningCarDataManager.column_object, contentValues.getAsString(SuningCarDataManager.column_object));
            values.put(SuningCarDataManager.column_count, contentValues.getAsInteger(SuningCarDataManager.column_count) + 1);
            values.put(SuningCarDataManager.column_selection, contentValues.getAsInteger(SuningCarDataManager.column_selection));
            dataManager.updateData(values, sku);
        }
    }

    public static void deleteCount(String sku) {
        ContentValues contentValues = dataManager.getData(sku);
        if (contentValues != null) {
            if (contentValues.getAsInteger(SuningCarDataManager.column_count) > 1) {
                ContentValues values = new ContentValues();
                values.put(SuningCarDataManager.column_sku, contentValues.getAsString(SuningCarDataManager.column_sku));
                values.put(SuningCarDataManager.column_object, contentValues.getAsString(SuningCarDataManager.column_object));
                values.put(SuningCarDataManager.column_count, contentValues.getAsInteger(SuningCarDataManager.column_count) - 1);
                values.put(SuningCarDataManager.column_selection, contentValues.getAsInteger(SuningCarDataManager.column_selection));
                dataManager.updateData(values, sku);
            }
        }
    }

    public static void select(String sku) {
        ContentValues contentValues = dataManager.getData(sku);
        if (contentValues != null) {
            ContentValues values = new ContentValues();
            values.put(SuningCarDataManager.column_sku, contentValues.getAsString(SuningCarDataManager.column_sku));
            values.put(SuningCarDataManager.column_object, contentValues.getAsString(SuningCarDataManager.column_object));
            values.put(SuningCarDataManager.column_count, contentValues.getAsInteger(SuningCarDataManager.column_count));
            int selection = contentValues.getAsInteger(SuningCarDataManager.column_selection);
            if (selection == 0) {
                values.put(SuningCarDataManager.column_selection, 1);
            } else {
                values.put(SuningCarDataManager.column_selection, 0);
            }
            dataManager.updateData(values, sku);
        }
    }

    public static boolean isAllSelected() {
        List<ModelSuningCar> suningCars = getSuningCars();
        for (int i = 0; i < suningCars.size(); i++) {
            for (int j = 0; j < suningCars.size(); j++) {
                if (!suningCars.get(i).isSelected()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void selectAll() {
        if (isAllSelected()) {
            dataManager.selectAll(0);
        } else {
            dataManager.selectAll(1);
        }
    }

    public static void deleteSelectedCars() {
        dataManager.deleteSelectedProducts();
    }

    public static void delete(String sku) {
        dataManager.delete(sku);
    }

    public static List<ModelSuningCar> getSelectedCars() {
        List<ModelSuningCar> suningCars = new ArrayList<>();
        List<ContentValues> contentValues = dataManager.geteSelectedProducts();
        for (int i = 0; i < contentValues.size(); i++) {
            suningCars.add(new ModelSuningCar(
                    contentValues.get(i).getAsString(SuningCarDataManager.column_object),
                    contentValues.get(i).getAsInteger(SuningCarDataManager.column_count),
                    contentValues.get(i).getAsInteger(SuningCarDataManager.column_selection)));
        }
        return suningCars;
    }

}
