package eu.dnetlib.iis.common;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Ordered properties implementation. Based on:
 * http://stackoverflow.com/questions/3619796/how-to-read-a-properties-file-in-java-in-the-original-order
 * @author mhorst
 */

public class OrderedProperties extends Properties {

	private static final long serialVersionUID = 1L;

	private final Map<Object, Object> linkMap = new LinkedHashMap<Object, Object>();

	public void clear() {
		linkMap.clear();
	}

	public boolean contains(Object value) {
		return linkMap.containsValue(value);
	}

	public boolean containsKey(Object key) {
		return linkMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return linkMap.containsValue(value);
	}

	public Enumeration<Object> elements() {
		return Collections.enumeration(linkMap.values());
	}

	public Set<Map.Entry<Object, Object>> entrySet() {
		return linkMap.entrySet();
	}

	public boolean equals(Object o) {
		return linkMap.equals(o);
	}

	public Object get(Object key) {
		return linkMap.get(key);
	}

	public String getProperty(String key) {
		Object oval = get(key);
		return (oval instanceof String) ? (String) oval : null;
	}

	public boolean isEmpty() {
		return linkMap.isEmpty();
	}

	public Enumeration<Object> keys() {
		Set<Object> keys = linkMap.keySet();
		return Collections.enumeration(keys);
	}

	public Set<Object> keySet() {
		return linkMap.keySet();
	}

	public void list(PrintStream out) {
		this.list(new PrintWriter(out, true));
	}

	public void list(PrintWriter out) {
		out.println("-- listing properties --");
		for (Map.Entry<Object, Object> e : (Set<Map.Entry<Object, Object>>) this.entrySet()) {
			String key = (String) e.getKey();
			String val = (String) e.getValue();
			if (val.length() > 40) {
				val = val.substring(0, 37) + "...";
			}
			out.println(key + "=" + val);
		}
	}

	public Object put(Object key, Object value) {
		return linkMap.put(key, value);
	}

	public int size() {
		return linkMap.size();
	}

	public Collection<Object> values() {
		return linkMap.values();
	}
}