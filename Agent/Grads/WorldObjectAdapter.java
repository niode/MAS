package Grads;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WorldObjectAdapter<T> implements JsonSerializer<T>,
		JsonDeserializer<T> {

	@Override
	public T deserialize(JsonElement json, Type arg1,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
	    String type = jsonObject.get("type").getAsString();
	    JsonElement element = jsonObject.get("properties");

	    try {            
	        return context.deserialize(element, Class.forName(type));
	    } catch (ClassNotFoundException cnfe) {
	        throw new JsonParseException("Unknown element type: " + type, cnfe);
	    }
		
	}

	@Override
	public JsonElement serialize(T src, Type arg1,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
	    result.add("type", new JsonPrimitive(src.getClass().getName()));
	    result.add("properties", context.serialize(src, src.getClass())); 
	    return result;
	}

}
