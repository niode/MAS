package Grads;

import Ares.World.Info.*;

public class GradCommunicator
{
  public static String format(SurroundInfo info)
  {
    return encode(new Surround(info));
  }

  public static String encode(Surround payload){
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(WorldObjectInfo.class, new WorldObjectAdapter<WorldObjectInfo>());
		String json = gson.create().toJson(payload);
		//return Surround.getClass().getName()+"#"+json;
    return Surround.class.getName() + "#" + json;
	}
	
	public static Surround decode(String str){
		  
		  String[] array = str.split("#");
		  if( array.length != 2 )
		  	throw new IllegalArgumentException("format of payload is wrong");
		  	
		  String clzName = array[0];
		  String json = array[1];
		  
		  GsonBuilder gson = new GsonBuilder();
		  gson.registerTypeAdapter(WorldObjectInfo.class, new WorldObjectAdapter<WorldObjectInfo>());
		  
		  try{
		   Class<?> clz = Class.forName(clzName);
		   Surround payload = (Surround) gson.create().fromJson(json, clz);
		   return payload;
		  }
		  catch(Exception ex){
			  ex.printStackTrace();
		   	throw new IllegalArgumentException("decode message wrong");
		  }
	}
}
