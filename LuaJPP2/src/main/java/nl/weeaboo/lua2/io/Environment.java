package nl.weeaboo.lua2.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Environment {

	private Map<Long, Object> idObjMap;
	private IdentityHashMap<Object, Long> objIdMap;
	
	public Environment() {
		idObjMap = new HashMap<Long, Object>();
		objIdMap = new IdentityHashMap<Object, Long>();
	}
	
	//Functions
	protected void add(long id, Object obj) {
		remove(id, obj);
		
		idObjMap.put(id, obj);
		objIdMap.put(obj, id);
	}
	protected void remove(long id, Object obj) {
		idObjMap.remove(id);
		objIdMap.remove(obj);
	}
	
	public Map<Object, Long> toMap() {
		return Collections.unmodifiableMap(objIdMap);
	}
	
	//Getters
	public Object get(long id) {
		return idObjMap.get(id);
	}
	public Long getId(Object obj) {
		return objIdMap.get(obj);
	}
	public int size() {
		return objIdMap.size();
	}
	
	//Setters
	
}
