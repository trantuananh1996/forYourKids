package koiapp.pr.com.koiapp.utils.realm;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.realm.RealmList;

public class IntegerRealmListConverter implements JsonSerializer<RealmList<RealmInteger>>,
        JsonDeserializer<RealmList<RealmInteger>> {

    @Override
    public JsonElement serialize(RealmList<RealmInteger> src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonArray jArr = new JsonArray();
        for (RealmInteger tag : src) {
            jArr.add(context.serialize(tag));
        }
        return jArr;
    }

    @Override
    public RealmList<RealmInteger> deserialize(JsonElement json, Type typeOfT,
                                               JsonDeserializationContext context)
            throws JsonParseException {
        RealmList<RealmInteger> tags = new RealmList<>();
        JsonArray ja = json.getAsJsonArray();
        for (JsonElement je : ja) {
            tags.add(new RealmInteger((Integer) context.deserialize(je, Integer.class)));
        }
        return tags;
    }

}