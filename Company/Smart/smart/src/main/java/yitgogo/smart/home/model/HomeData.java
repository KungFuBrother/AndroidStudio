package yitgogo.smart.home.model;

import java.util.ArrayList;
import java.util.List;

public class HomeData {

	private List<ModelHomeBrand> brands = new ArrayList<ModelHomeBrand>();
	private List<ModelClass> classes = new ArrayList<ModelClass>();
	private List<ModelSaleTime> saleTimes = new ArrayList<ModelSaleTime>();
	private List<ModelSaleTheme> saleThemes = new ArrayList<ModelSaleTheme>();
	private List<ModelLocalStore> localStores = new ArrayList<ModelLocalStore>();
	private int brandSelection = 0;
	private int classSelection = 0;
	private int saleTimeSelection = 0;
	private int saleThemeSelection = 0;
	private int storeSelection = 0;
	public static HomeData instance;
	public static final int columWidth = 260;
	public static final int imageHeight = 256;

	public static HomeData getInstance() {
		if (instance == null) {
			instance = new HomeData();
		}
		return instance;
	}

	public List<ModelHomeBrand> getBrands() {
		return brands;
	}

	public void setBrands(List<ModelHomeBrand> brands) {
		this.brands = brands;
	}

	public List<ModelClass> getClasses() {
		return classes;
	}

	public void setClasses(List<ModelClass> classes) {
		this.classes = classes;
	}

	public List<ModelSaleTime> getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(List<ModelSaleTime> saleTimes) {
		this.saleTimes = saleTimes;
	}

	public List<ModelSaleTheme> getSaleThemes() {
		return saleThemes;
	}

	public void setSaleThemes(List<ModelSaleTheme> saleThemes) {
		this.saleThemes = saleThemes;
	}

	public List<ModelLocalStore> getLocalStores() {
		return localStores;
	}

	public void setLocalStores(List<ModelLocalStore> localStores) {
		this.localStores = localStores;
	}

	public int getBrandSelection() {
		return brandSelection;
	}

	public void setBrandSelection(int brandSelection) {
		this.brandSelection = brandSelection;
	}

	public int getClassSelection() {
		return classSelection;
	}

	public void setClassSelection(int classSelection) {
		this.classSelection = classSelection;
	}

	public int getSaleTimeSelection() {
		return saleTimeSelection;
	}

	public void setSaleTimeSelection(int saleTimeSelection) {
		this.saleTimeSelection = saleTimeSelection;
	}

	public int getSaleThemeSelection() {
		return saleThemeSelection;
	}

	public void setSaleThemeSelection(int saleThemeSelection) {
		this.saleThemeSelection = saleThemeSelection;
	}

	public int getStoreSelection() {
		return storeSelection;
	}

	public void setStoreSelection(int storeSelection) {
		this.storeSelection = storeSelection;
	}

}
