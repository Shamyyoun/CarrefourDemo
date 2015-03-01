package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datamodels.Category;

public class CategoriesHandler {
    private String response;

    public CategoriesHandler(String response) {
        this.response = response;
    }

    public Category[] handle() {
        try {
            JSONArray jsonArray = new JSONArray(response);
            Category[] categories = new Category[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Category category = handleCategory(jsonObject);

                categories[i] = category;
            }
            return categories;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Category handleCategory(JSONObject jsonObject) {
        Category category;
        try {
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");

            category = new Category(id)
                    .setName(name);
        } catch (JSONException e) {
            category = null;
            e.printStackTrace();
        }

        return category;
    }
}
