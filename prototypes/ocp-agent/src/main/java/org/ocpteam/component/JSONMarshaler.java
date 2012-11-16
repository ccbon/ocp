package org.ocpteam.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.misc.JLG;
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
				
				if (jsonArray.get(1) == JSONObject.NULL) {
					JLG.debug("JSONObject.NULL");
					result.setNullField(key, type);
				} else if (type.equals(Structure.TYPE_BYTES)) {
					byte[] value = Base64.decodeBase64(jsonArray.getString(1));
					result.setBinField(key, value);
				} else if (type.equals(Structure.TYPE_INT)) {
					int value = jsonArray.getInt(1);
					result.setIntField(key, value);
				} else if (type.equals(Structure.TYPE_SUBSTRUCT)) {
					Structure value = fromJson(jsonArray.getJSONObject(1));
					result.setStructureSubstructField(key, value);
				} else if (type.equals(Structure.TYPE_LIST)) {
					JSONArray ja = jsonArray.getJSONArray(1);
					List<Structure> l = new ArrayList<Structure>();
					for (int i = 0; i < ja.length(); i++) {
						JSONObject j = ja.getJSONObject(i);
						Structure s = fromJson(j);
						l.add(s);
					}
					result.setStructureListField(key, l);
				} else if (type.equals(Structure.TYPE_DECIMAL)) {
					double value = jsonArray.getDouble(1);
					result.setDecimalField(key, value);
				} else if (type.equals(Structure.TYPE_STRING)) {
					String value = jsonArray.getString(1);
					result.setStringField(key, value);
				} else if (type.equals(Structure.TYPE_MAP)) {
					JSONObject jo = jsonArray.getJSONObject(1);
					Map<String, Structure> map = new HashMap<String, Structure>();
					Iterator<?> it2 = jo.keys();
					while (it2.hasNext()) {
						String s = (String) it2.next();
						JSONObject j = jo.getJSONObject(s);
						Structure struct = fromJson(j);
						map.put(s, struct);
					}
					result.setStructureMapField(key, map);
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
			JSONObject obj = new JSONObject();
			for (String name : s.getFields().keySet()) {
				JSONArray field = new JSONArray();
				String type = s.getField(name).getType();
				field.put(type);
				
				if (type.equals(Structure.TYPE_LIST)) {
					JSONArray list = new JSONArray();
					@SuppressWarnings("unchecked")
					List<Structure> value = (List<Structure>) s.getField(name)
							.getValue();
					for (Structure o : value) {
						JSONObject json = toJson(o);
						list.put(json);
					}
					field.put(list);
				} else if (type.equals(Structure.TYPE_SUBSTRUCT)) {
					Structure value = (Structure) s.getField(name).getValue();
					field.put(toJson(value));
				} else if (type.equals(Structure.TYPE_BYTES)) {
					byte[] array = (byte[]) s.getFields().get(name).getValue();
					field.put(Base64.encodeBase64URLSafeString(array));
				} else if (type.equals(Structure.TYPE_MAP)) {
					JSONObject map = new JSONObject();
					@SuppressWarnings("unchecked")
					Map<String, Structure> value = (Map<String, Structure>) s
							.getField(name).getValue();
					for (String key : value.keySet()) {
						JSONObject json = toJson(value.get(key));
						map.put(key, json);
					}
					field.put(map);
				} else {
					field.put(s.getField(name).getValue());
				}
				obj.put(name, field);
			}
			result.put("fields", obj);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
