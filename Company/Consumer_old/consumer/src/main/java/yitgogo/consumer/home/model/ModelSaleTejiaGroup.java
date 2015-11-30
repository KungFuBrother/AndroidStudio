package yitgogo.consumer.home.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class ModelSaleTejiaGroup {

	List<ModelSaleTejiaProduct> tejiaProducts = new ArrayList<ModelSaleTejiaProduct>();
	int page = 0;
	int pageSize = 1;

	public ModelSaleTejiaGroup(int pageSize) {
		tejiaProducts = new ArrayList<ModelSaleTejiaProduct>();
		this.pageSize = pageSize;
		for (int i = 0; i < pageSize; i++) {
			tejiaProducts.add(new ModelSaleTejiaProduct());
		}
	}

	public ModelSaleTejiaGroup(JSONArray array, int pageSize)
			throws JSONException {
		tejiaProducts = new ArrayList<ModelSaleTejiaProduct>();
		this.pageSize = pageSize;
		if (array != null) {
			for (int i = 0; i < array.length(); i++) {
				tejiaProducts.add(new ModelSaleTejiaProduct(array
						.optJSONObject(i)));
			}
		}
	}

	public List<ModelSaleTejiaProduct> getTejiaProducts() {
		return tejiaProducts;
	}

	public List<ModelSaleTejiaProduct> getNextPageTejiaProducts() {
		List<ModelSaleTejiaProduct> pagedTejiaProducts = new ArrayList<ModelSaleTejiaProduct>();
		if (pageSize == 1) {
			if (tejiaProducts.size() == 0) {
				pagedTejiaProducts.add(new ModelSaleTejiaProduct());
			} else {
				pagedTejiaProducts.add(tejiaProducts.get(page
						% tejiaProducts.size()));
			}
		} else {
			if (tejiaProducts.size() == 0) {
				for (int i = 0; i < pageSize; i++) {
					pagedTejiaProducts.add(new ModelSaleTejiaProduct());
				}
			} else if (tejiaProducts.size() == 1) {
				for (int i = 0; i < pageSize; i++) {
					pagedTejiaProducts.add(tejiaProducts.get(0));
				}
			} else if (tejiaProducts.size() > 1) {
				int startIndex = (pageSize * page) % tejiaProducts.size();
				int endIndex = (pageSize * (page + 1)) % tejiaProducts.size();
				if (tejiaProducts.size() - startIndex < pageSize) {
					List<ModelSaleTejiaProduct> tejiaProducts1 = tejiaProducts
							.subList(startIndex, tejiaProducts.size());
					List<ModelSaleTejiaProduct> tejiaProducts2 = tejiaProducts
							.subList(0, endIndex);
					pagedTejiaProducts.addAll(tejiaProducts1);
					pagedTejiaProducts.addAll(tejiaProducts2);
				} else if (tejiaProducts.size() - startIndex > pageSize) {
					pagedTejiaProducts = tejiaProducts.subList(startIndex,
							endIndex);
				} else {
					pagedTejiaProducts = tejiaProducts.subList(startIndex,
							tejiaProducts.size());
				}
			}
		}
		page++;
		return pagedTejiaProducts;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	@Override
	public String toString() {
		return "ModelSaleTejiaGroup [tejiaProducts=" + tejiaProducts
				+ ", page=" + page + ", pageSize=" + pageSize + "]";
	}

}
