package org.ocpteam.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.misc.Structure;

public class JSONMarshaler implements IMarshaler {

	@Override
	public byte[] marshal(Structure s) throws Exception {
		return toJson(s).toString(4).getBytes();
	}

	@Override
	public Structure unmarshal(byte[] array) throws Exception {
		return fromJson(new JSONObject(new String(array)));
	}

	private Structure fromJson(JSONObject json) {
		try {
			Structure result = new Structure(json.getString("name"));
			JSONObject jsonFields = json.getJSONObject("fields");
			@SuppressWarnings("unchecked")
			Iterator<Object> it = jsonFields.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				JSONArray jsonArray = jsonFields.getJSONArray(key);
				String type = jsonArray.getString(0);
				if (type.equals("bytes")) {
					byte[] value = Base64.decodeBase64(jsonArray.getString(1));
					result.setField(key, type, value);
				} else if (type.equals("int")) {
					int value = jsonArray.getInt(1);
					result.setField(key, type, value);
				} else if (type.equals("substruct")) {
					Structure value = fromJson(jsonArray.getJSONObject(1));
					result.setField(key, type, value);
				} else if (type.equals("list")) {
					JSONArray ja = jsonArray.getJSONArray(1);
					List<Structure> l = new ArrayList<Structure>();
					for (int i = 0; i < ja.length(); i++) {
						JSONObject j = ja.getJSONObject(i);
						Structure s = fromJson(j);
						l.add(s);
					}
					result.setField(key, type, l);
				} else if (type.equals("decimal")) {
					double value = jsonArray.getDouble(1);
					result.setField(key, type, value);
				} else if (type.equals("string")) {
					String value = jsonArray.getString(1);
					result.setField(key, type, value);
				}

			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject toJson(Structure s) {
		try {
			JSONObject result = new JSONObject();
			result.put("name", s.getName());
			JSONObject map = new JSONObject();
			for (String name : s.getFields().keySet()) {
				JSONArray field = new JSONArray();
				String type = s.getFields().get(name).getType();
				field.put(type);
				if (type.equals("list")) {
					JSONArray list = new JSONArray();
					@SuppressWarnings("unchecked")
					List<Structure> value = (List<Structure>) s
							.getFields().get(name).getValue();
					for (Structure o : value) {
						JSONObject json = toJson(o);
						list.put(json);
					}
					field.put(list);
				} else if (type.equals("substruct")) {
					Structure value = (Structure) s.getFields()
							.get(name).getValue();
					field.put(toJson(value));
				} else if (type.equals("bytes")) {
					byte[] array = (byte[]) s.getFields().get(name).getValue();
					field.put(Base64.encodeBase64URLSafeString(array));
				} else {
					field.put(s.getFields().get(name).getValue());
				}
				map.put(name, field);
			}
			result.put("fields", map);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
