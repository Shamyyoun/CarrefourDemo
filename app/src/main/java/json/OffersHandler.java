package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datamodels.Category;
import datamodels.Offer;

public class OffersHandler {
    private String response;

    public OffersHandler(String response) {
        this.response = response;
    }

    public Offer[] handle() {
        try {
            JSONArray jsonArray = new JSONArray(response);
            Offer[] offers = new Offer[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Offer offer = handleOffer(jsonObject);

                offers[i] = offer;
            }
            return offers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Offer handleOffer(JSONObject jsonObject) {
        Offer offer;
        try {
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");
            double priceBefore = 0;
            try {
                priceBefore = Double.parseDouble(jsonObject.getString("price_before"));
            } catch (Exception e) {
            }
            double priceAfter = 0;
            try {
                priceAfter = Double.parseDouble(jsonObject.getString("price_after"));
            } catch (Exception e) {
            }
            String imageUrl = jsonObject.getString("img_url");
            String categoryId = jsonObject.getString("category_id");
            String categoryName = jsonObject.getString("category_name");
            String createDate = jsonObject.getString("created_at");
            String updateDate = jsonObject.getString("updated_at");
            String expireDate = jsonObject.getString("expires_at");

            offer = new Offer(id)
                    .setName(name)
                    .setDescription(description)
                    .setPriceBefore(priceBefore)
                    .setPriceAfter(priceAfter)
                    .setImageUrl(imageUrl)
                    .setCategory(new Category(categoryId).setName(categoryName))
                    .setCreateDate(createDate)
                    .setUpdateDate(updateDate)
                    .setExpireDate(expireDate);
        } catch (JSONException e) {
            offer = null;
            e.printStackTrace();
        }

        return offer;
    }
}
