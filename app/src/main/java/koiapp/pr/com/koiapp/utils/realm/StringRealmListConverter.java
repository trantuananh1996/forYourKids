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

public class StringRealmListConverter implements JsonSerializer<RealmList<RealmString>>,
        JsonDeserializer<RealmList<RealmString>> {

    @Override
    public JsonElement serialize(RealmList<RealmString> src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonArray ja = new JsonArray();
        for (RealmString tag : src) {
            ja.add(context.serialize(tag));
        }
        return ja;
    }

    @Override
    public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT,
                                              JsonDeserializationContext context)
            throws JsonParseException {
        RealmList<RealmString> tags = new RealmList<>();
        JsonArray ja = json.getAsJsonArray();
        for (JsonElement je : ja) {
            tags.add(new RealmString((String) context.deserialize(je, String.class)));
        }
        return tags;
    }

}