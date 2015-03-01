package json;

import org.json.JSONException;
import org.json.JSONObject;

import datamodels.User;

public class UserHandler {
    private String response;

    public UserHandler(String response) {
        this.response = response;
    }

    public User handle() {
        try {
            JSONObject jsonObject = new JSONObject(response);
            User user = handleUser(jsonObject);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private User handleUser(JSONObject jsonObject) {
        User user;
        try {
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String typeId = jsonObject.getString("type_id");
            String typeName = jsonObject.getString("type_name");
            String genId = jsonObject.getString("gen_id");

            user = new User(id)
                    .setName(name)
                    .setType(new User.Type(typeId).setName(typeName))
                    .setGenId(genId);
        } catch (JSONException e) {
            user = null;
            e.printStackTrace();
        }

        return user;
    }
}
