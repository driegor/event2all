package org.uoc.pfc.eventual.utils.integration.dto.generic;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class JsonDTO extends EntityDTO {

	public String toJson() {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			throw new RuntimeException("error converting " + this.getClass().getSimpleName() + " to JSON", e);
		}
	}

	public Map<String, Object> toMap() {

		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
			};
			HashMap<String, Object> map = mapper.readValue(toJson(), typeRef);
			return map;
		} catch (Exception e) {
			throw new RuntimeException("error converting " + this.getClass().getSimpleName() + " to Map", e);
		}
	}
}
