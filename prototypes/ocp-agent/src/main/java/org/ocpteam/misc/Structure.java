package org.ocpteam.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.serializable.Address;

public class Structure {
	private String name;
	public Map<String, SField> fields = new HashMap<>();

	public Structure(String name) {
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setField(String name, String type, Object value) {
		fields.put(name, new SField(type, value));
	}

	private JSONObject toJson() {
		try {
			JSONObject result = new JSONObject();
			result.put("name", name);
			JSONObject map = new JSONObject();
			for (String name : fields.keySet()) {
				JSONArray field = new JSONArray();
				String type = fields.get(name).getType();
				field.put(type);
				if (type.equals("list")) {
					JSONArray list = new JSONArray();
					@SuppressWarnings("unchecked")
					List<IStructurable> value = (List<IStructurable>) fields
							.get(name).getValue();
					for (IStructurable o : value) {
						Structure s = o.toStructure();
						JSONObject json = s.toJson();
						list.put(json);
					}
					field.put(list);
				} else if (type.equals("substruct")) {
					IStructurable value = (IStructurable) fields.get(name).getValue();
					field.put(value.toStructure().toJson());
				} else {
					field.put(fields.get(name).getValue());
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

	@Override
	public String toString() {
		try {
			return toJson().toString(4);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object getField(String name) {
		return fields.get(name).getValue();
	}

	public Object toObject() throws Exception {
		String classname = getClassFromName();
		IStructurable result = (IStructurable) Class.forName(classname).newInstance();
		result.fromStructure(this);
		return result;
	}

	private String getClassFromName() {
		return Address.class.getName();
	}
}
