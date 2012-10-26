package org.ocpteam.component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

public class JSONSerializer implements ISerializer {

	@Override
	public byte[] serialize(Serializable s) throws Exception {
		String result = "{";
		Class<? extends Serializable> c = s.getClass();
		String classname = c.getSimpleName();
		result += "\"class\": \"" + classname + "\", ";
		result += "\"attributes\": {";
		boolean isfirst = true;
		for (Field f : c.getFields()) {
			if (!isfirst) {
				result += ", ";
			}
			isfirst = false;
			Object content = f.get(s);
			String type = f.getType().getSimpleName().toLowerCase();
			if (content instanceof String || content instanceof Integer) {
				content = "\"" + content + "\"";
			} else if (content instanceof byte[]) {
				System.out.println("is a byte[]");
				content = Base64.encodeBase64URLSafeString((byte[]) content);
				content = "\"" + content + "\"";
			} else if (f.getType().getSimpleName().equals("List")
					&& content != null) {
				ParameterizedType p = (ParameterizedType) f.getGenericType();
				type = p.getActualTypeArguments()[0].toString();
				type = type.substring(type.lastIndexOf('.') + 1);
				String str = "[";
				boolean isFirst2 = true;
				@SuppressWarnings("unchecked")
				List<Serializable> list = (List<Serializable>) content;
				for (Serializable se : list) {
					if (!isFirst2) {
						str += ", ";
					}
					isFirst2 = false;
					str += serialize(se);
				}
				str += "]";
				content = str;
			} else if (content instanceof Serializable) {
				content = serialize((Serializable) content);
			}

			result += "\"" + f.getName() + "\": [\"" + type + "\", " + content
					+ "]";
		}

		result += "} }";
		return result.getBytes();
	}

	@Override
	public Serializable deserialize(byte[] input) throws Exception {
		Serializable result = null;
		String json = new String(input);
		JSONObject j = new JSONObject(json);
		String classname = j.getString("class");
		JLG.debug(classname);
		String pack = Address.class.getPackage().getName();
		JLG.debug(pack);
		classname = pack + "." + classname;
		JLG.debug(classname);
		result = (Serializable) Class.forName(classname).newInstance();
		JSONObject attributes = new JSONObject(j.getString("attributes"));
		JLG.debug(attributes.toString());
		@SuppressWarnings("unchecked")
		Iterator<? extends Object> it = attributes.keys();
		while (it.hasNext()) {
			String attrname = (String) it.next();
			JLG.debug(attrname);
			JSONArray jarray = attributes.getJSONArray(attrname);
			JLG.debug(jarray.toString());
			String type = jarray.getString(0);
			JLG.debug(type);
			Field f = result.getClass().getField(attrname);
			Object val = jarray.get(1);
			JLG.debug("val=" + val);
			JLG.debug("val class=" + val.getClass());
			if (type.equals("string")) {
				f.set(result, jarray.getString(1));
			} else if (type.equals("int")) {
				String value = jarray.getString(1);
				int i = Integer.parseInt(value);
				f.set(result, i);
			} else if (type.equals("byte[]")) {
				String value = jarray.getString(1);
				byte[] i = Base64.decodeBase64(value);
				f.set(result, i);
			} else if (jarray.get(1) instanceof JSONArray) {
				List<Serializable> list = new ArrayList<>();
				JSONArray a = jarray.getJSONArray(1);
				for (int i = 0; i < a.length(); i++) {
					JSONObject jo = a.getJSONObject(i);
					Serializable s = deserialize(jo.toString().getBytes());
					list.add(s);
				}
				f.set(result, list);
			} else {
				String js = jarray.getString(1);
				if (!js.equals("null")) {
					Serializable s = deserialize(js.getBytes());
					f.set(result, s);
				}					
			}
		}
		return result;
	}

}
