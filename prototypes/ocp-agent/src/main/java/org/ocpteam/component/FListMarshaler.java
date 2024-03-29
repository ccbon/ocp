package org.ocpteam.component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.Structure;

public class FListMarshaler implements IMarshaler {

	public class TabInfo {

		private static final int TABLENGTH = 3;
		public int tabeltid = 0;
		public int maxLevel = 0;
		private int maxField = 0;

		public TabInfo(Structure s) throws Exception {
			LOG.finest("Call TabInfo with s.name=" + s.getName());
			initMaxLevel(s, 0);
			init(s, 0);
			LOG.finest("maxlevel=" + maxLevel);
			tabeltid = maxLevel + 1 + maxField + 2;
		}

		private void initMaxLevel(Structure s, int level) throws Exception {
			if (s == null) {
				return;
			}
			maxLevel = Math.max(maxLevel, level);
			for (String name : s.getFields().keySet()) {
				String type = s.getField(name).getType();
				if (type.equals(Structure.TYPE_SUBSTRUCT)) {
					Structure substr = s.getStructureFromSubstructField(name);
					init(substr, level + 1);
				} else if (type.equals(Structure.TYPE_LIST)) {
					List<Structure> list = s.getStructureFromListField(name);
					if (list != null) {
						for (Structure ss : list) {
							init(ss, level + 1);
						}
					}
				} else if (type.equals(Structure.TYPE_MAP)) {
					Map<String, Structure> map = s
							.getStructureFromMapField(name);
					if (map != null) {
						for (String n : map.keySet()) {
							Structure ss = map.get(n);
							init(ss, level + 2);
						}
					}
				}
			}
		}

		private void init(Structure s, int level) throws Exception {
			if (s == null) {
				return;
			}
			LOG.finest("structure name = " + s.getName());
			if (s.getName() == null) {
				throw new Exception("Struture without name.");
			}
			maxField = Math.max((TABLENGTH * level) + s.getName().length() + 2,
					maxField);
			for (String name : s.getFields().keySet()) {
				String type = s.getField(name).getType();
				maxField = Math.max((TABLENGTH * level) + name.length() + 1
						+ type.length(), maxField);

				if (type.equals(Structure.TYPE_SUBSTRUCT)) {
					Structure substr = s.getStructureFromSubstructField(name);
					init(substr, level + 1);
				} else if (type.equals(Structure.TYPE_LIST)) {
					List<Structure> list = s.getStructureFromListField(name);
					if (list != null) {
						for (Structure ss : list) {
							init(ss, level + 1);
						}
					}
				} else if (type.equals(Structure.TYPE_MAP)) {
					Map<String, Structure> map = s
							.getStructureFromMapField(name);
					if (map != null) {
						for (String n : map.keySet()) {
							Structure ss = map.get(n);
							init(ss, level + 1);
						}
					}
				}
			}
		}
	}

	public class FListLine {

		public int level;
		public String fieldname;
		public String fieldtype;
		public String fieldvalue;
		public String fieldeltid;

		public FListLine(String buffer) {
			LOG.finest("buffer=" + buffer);
			String[] sa = buffer.split("\\s+", 5);
			level = Integer.parseInt(sa[0]);
			fieldname = sa[1];
			fieldtype = sa[2];
			fieldeltid = sa[3].substring(1, sa[3].length() - 1);
			if (sa.length > 4) {
				fieldvalue = sa[4];
			}
			LOG.finest("level=" + level);
			LOG.finest("fieldname=" + fieldname);
			LOG.finest("fieldtype=" + fieldtype);
			LOG.finest("fieldeltid=" + fieldeltid);
			LOG.finest("fieldvalue=" + fieldvalue);
		}

		public boolean hasNullValue() {
			return fieldvalue != null && fieldvalue.equals(VALUE_NULL);
		}

		public boolean hasMapNullValue() {
			return fieldvalue != null && fieldvalue.equals(MAP_NULL);
		}

		public boolean hasListNullValue() {
			return fieldvalue != null && fieldvalue.equals(LIST_NULL);
		}

	}

	public static final String NL = System.getProperty("line.separator");
	private static final String VALUE_NULL = "<NULL>";
	private static final String MAP_NULL = "<MAP_NULL>";
	private static final String LIST_NULL = "<LIST_NULL>";
	private static final int FILE_WIDTH = 80;
	private static final String BIN_PREFIX = " ";

	@Override
	public byte[] marshal(Structure s) throws Exception {
		TabInfo tabInfo = new TabInfo(s);
		return toFList(s, 0, tabInfo).getBytes();
	}

	@Override
	public Structure unmarshal(byte[] array) throws Exception {
		LOG.finest("array=" + new String(array));
		return fromFList(new String(array));
	}

	private Structure fromFList(String string) throws Exception {
		LOG.finest("string=" + JLG.NL + string);
		Structure s = new Structure();
		BufferedReader br = new BufferedReader(new StringReader(string));
		fromFList(s, br, 0);
		br.close();
		LOG.finest("structure=" + s);
		return s;

	}

	private String fromFList(Structure s, BufferedReader br, int expectedLevel)
			throws Exception {
		String nextLine = br.readLine();
		if (nextLine == null) {
			throw new Exception("An FList must have name");
		}
		String name = nextLine.substring(nextLine.indexOf('[') + 1,
				nextLine.indexOf(']'));
		s.setName(name);
		// JLG.debug("s=" + s);

		boolean bContinue = true;
		nextLine = br.readLine();
		if (nextLine == null) {
			return nextLine;
		}
		int level = Integer.parseInt(nextLine.substring(0,
				nextLine.indexOf(' ')));
		if (level != expectedLevel) {
			bContinue = false;
		}
		while (bContinue) {
			String currentLine = nextLine;
			nextLine = null;
			FListLine fl = new FListLine(currentLine);
			if (fl.fieldtype.equals(Structure.TYPE_STRING)) {
				String value = "";
				if (fl.fieldvalue.startsWith("<<EOF")) {
					int lineNbr = Integer.parseInt(fl.fieldvalue
							.substring(fl.fieldvalue.indexOf(' ') + 1));
					StringBuilder fieldvalue = new StringBuilder(lineNbr
							* FILE_WIDTH);
					String line = br.readLine();
					while (!line.equals("EOF")) {
						line = line.substring(1, line.length() - 1);
						fieldvalue.append(line);
						line = br.readLine();
					}
					value = fieldvalue.toString();
				} else if (fl.fieldvalue.equals(VALUE_NULL)) {
					value = null;
				} else {
					value = fl.fieldvalue.substring(1,
							fl.fieldvalue.length() - 1);
				}
				// JLG.debug("fieldvalue=" + value);
				s.setStringField(fl.fieldname, unescape(value));
			} else if (fl.fieldtype.equals(Structure.TYPE_BYTES)) {
				byte[] value = null;
				if (fl.fieldvalue.startsWith("<<EOF")) {
					int lineNbr = Integer.parseInt(fl.fieldvalue
							.substring(fl.fieldvalue.indexOf(' ') + 1));
					StringBuilder fieldvalue = new StringBuilder(lineNbr
							* FILE_WIDTH);
					String line = br.readLine();
					while (!line.equals("EOF")) {
						fieldvalue.append(line.substring(BIN_PREFIX.length()));
						line = br.readLine();
					}
					value = Base64.decodeBase64(fieldvalue.toString());
				} else if (fl.fieldvalue.equals(VALUE_NULL)) {
					value = null;
				} else {
					value = Base64.decodeBase64(fl.fieldvalue);
				}

				s.setBinField(fl.fieldname, value);
			} else if (fl.fieldtype.equals(Structure.TYPE_DECIMAL)) {
				double value = Double.parseDouble(fl.fieldvalue);
				s.setDecimalField(fl.fieldname, value);
			} else if (fl.fieldtype.equals(Structure.TYPE_INT)) {
				s.setIntField(fl.fieldname, Integer.parseInt(fl.fieldvalue));
			} else if (fl.fieldtype.equals(Structure.TYPE_SUBSTRUCT)) {
				if (fl.hasNullValue()) {
					s.setStructureToSubstructField(fl.fieldname, null);
				} else {
					Structure substr = new Structure();
					nextLine = fromFList(substr, br, level + 1);
					s.setStructureToSubstructField(fl.fieldname, substr);
				}
			} else if (fl.fieldtype.equals(Structure.TYPE_LIST)) {
				if (fl.hasListNullValue()) {
					s.setStructureToListField(fl.fieldname, null);
				} else if (fl.hasNullValue()) {
					int eltid = Integer.parseInt(fl.fieldeltid);
					s.addStructureToListField(fl.fieldname, null, eltid);
				} else {
					Structure substr = new Structure();
					nextLine = fromFList(substr, br, level + 1);
					int eltid = Integer.parseInt(fl.fieldeltid);
					s.addStructureToListField(fl.fieldname, substr, eltid);
				}
			} else if (fl.fieldtype.equals(Structure.TYPE_MAP)) {
				if (fl.hasMapNullValue()) {
					s.setStructureToMapField(fl.fieldname, null);
				} else if (fl.hasNullValue()) {
					s.addStructureToMapField(fl.fieldname, null, fl.fieldeltid);
				} else {
					Structure ss = new Structure();
					nextLine = fromFList(ss, br, level + 1);

					String key = ss.getStringField(Structure.FIELDNAME_KEY);
					Structure substr = ss
							.getStructureFromSubstructField(Structure.FIELDNAME_VALUE);
					s.addStructureToMapField(fl.fieldname, substr, key);
				}
			}
			if (nextLine == null) {
				nextLine = br.readLine();
			}

			if (nextLine == null) {
				break;
			}
			level = Integer.parseInt(nextLine.split("\\s+")[0]);
			if (level != expectedLevel) {
				break;
			}
		}
		return nextLine;

	}

	private String unescape(String value) {
		return StringEscapeUtils.unescapeJava(value);
	}

	private String toFList(Structure s, int level, TabInfo tabInfo) {
		try {
			String result = new String();
			result += format(level, "[" + s.getName() + "]", "", 0, "", tabInfo);
			LOG.finest("Object=" + s.getName() + " | Fields="
					+ s.getFields().keySet());
			String[] set = (String[]) s.getFields().keySet()
					.toArray(new String[0]);
			Arrays.sort(set, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			for (String name : set) {
				String type = s.getField(name).getType();
				LOG.fine("Field=" + name + " | Type=" + type);
				if (type.equals(Structure.TYPE_BYTES)) {
					byte[] array = (byte[]) s.getFields().get(name).getValue();
					String value = Base64.encodeBase64URLSafeString(array);
					result += format(level, name, type, 0, value, tabInfo);

				} else if (type.equals(Structure.TYPE_STRING)) {
					String value = (String) s.getFields().get(name).getValue();
					result += format(level, name, type, 0, value, tabInfo);
				} else if (type.equals(Structure.TYPE_SUBSTRUCT)) {
					Structure value = (Structure) s.getField(name).getValue();
					LOG.finest("Field=" + name + " | type=" + type + " | value="
							+ value);
					if (value == null) {
						result += format(level, name, type, 0, VALUE_NULL,
								tabInfo);
					} else {
						result += format(level, name, type, 0, "", tabInfo);
						result += toFList(value, level + 1, tabInfo);
					}
				} else if (type.equals(Structure.TYPE_LIST)) {
					@SuppressWarnings("unchecked")
					List<Structure> list = (List<Structure>) s.getField(name)
							.getValue();
					if (list == null) {
						result += format(level, name, type, 0, LIST_NULL,
								tabInfo);
					} else {
						int i = 0;
						for (Structure ss : list) {
							if (ss == null) {
								result += format(level, name, type, i,
										VALUE_NULL, tabInfo);
							} else {
								result += format(level, name, type, i, "",
										tabInfo);
								result += toFList(ss, level + 1, tabInfo);
							}
							i++;
						}
					}

				} else if (type.equals(Structure.TYPE_MAP)) {
					Map<String, Structure> map = s
							.getStructureFromMapField(name);
					if (map == null) {
						result += format(level, name, type, 0, MAP_NULL,
								tabInfo);
					} else {
						String[] keys = (String[]) map.keySet().toArray(
								new String[0]);
						Arrays.sort(keys, new Comparator<String>() {

							@Override
							public int compare(String o1, String o2) {
								return o1.compareTo(o2);
							}
						});
						// TODO: Review Map case to manage spaces in the keys.
						int i = 0;
						for (String key : keys) {
							Structure substr = new Structure(
									Structure.NAME_MAPENTRY);
							substr.setStringField(Structure.FIELDNAME_KEY, key);
							Structure ss = map.get(key);
							substr.setStructureToSubstructField(
									Structure.FIELDNAME_VALUE, ss);

							result += format(level, name, type, i, "", tabInfo);
							result += toFList(substr, level + 1, tabInfo);
							i++;
						}
					}
				} else {
					Object value = s.getField(name).getValue();
					String str = VALUE_NULL;
					if (value != null) {
						str = value.toString();
					}
					result += format(level, name, type, 0, str, tabInfo);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String format(int level, String name, String type, int eltid,
			String value, TabInfo tabInfo) {
		return format(level, name, type, "" + eltid, value, tabInfo);
	}

	private String format(int level, String name, String type, String eltid,
			String value, TabInfo tabInfo) {
		String sLevel = Integer.toString(level);
		String s = level
				+ space(Math.max(0, tabInfo.maxLevel - sLevel.length()) + 1
						+ (TabInfo.TABLENGTH * level)) + name;
		String type2 = type;
		if (!type.equals("")) {
			type2 += " ";
		}
		String str = s
				+ space(Math.max(1,
						tabInfo.tabeltid - type2.length() - s.length()))
				+ type2 + "[" + eltid + "] ";
		int maxLength = FILE_WIDTH - str.length();
		String val = value;
		if (value == null) {
			val = VALUE_NULL;
		}
		if (type.equals(Structure.TYPE_STRING)) {
			if (value != null) {
				val = escape(value);
			}
		}
		if (value != null && value.length() > maxLength) {
			if (type.equals(Structure.TYPE_BYTES)) {
				val = multiLineBin(val);
			} else {
				val = multiLineString(val);
			}
		} else {
			if (type.equals(Structure.TYPE_STRING)) {
				if (value != null) {
					val = "\"" + val + "\"";
				}
			}
		}
		return str + val + NL;
	}

	private String escape(String value) {
		return StringEscapeUtils.escapeJava(value);
	}

	private String multiLineString(String value) {
		StringBuilder result = new StringBuilder(value.length());
		int i = 0;
		int width = FILE_WIDTH - 2;
		int lineNbr = ((value.length() / width) + 1);
		result.append("<<EOF " + lineNbr + NL);
		for (i = 0; i < value.length() - width; i += width) {
			result.append("\"" + value.substring(i, i + width) + "\"" + NL);
		}

		result.append("\"" + value.substring(i) + "\"" + NL + "EOF");
		return result.toString();
	}

	private String multiLineBin(String value) {
		StringBuilder result = new StringBuilder(value.length());
		int i = 0;
		int width = FILE_WIDTH - BIN_PREFIX.length();
		int lineNbr = ((value.length() / width) + 1);
		result.append("<<EOF " + lineNbr + NL);
		for (i = 0; i < value.length() - width; i += width) {
			result.append(BIN_PREFIX + value.substring(i, i + width) + NL);
		}

		result.append(BIN_PREFIX + value.substring(i) + NL + "EOF");
		return result.toString();
	}

	private String space(int n) {
		StringBuilder space = new StringBuilder(Math.max(0, n));
		for (int i = 0; i < n; i++) {
			space.append(' ');
		}
		return space.toString();
	}
}
