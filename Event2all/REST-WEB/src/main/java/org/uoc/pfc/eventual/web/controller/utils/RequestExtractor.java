package org.uoc.pfc.eventual.web.controller.utils;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.context.request.WebRequest;

public class RequestExtractor {

	public static <T> T extractPojo(WebRequest request, Class<T> pojoClass) {

		T pojo;
		Map<String, String[]> parameters = request.getParameterMap();

		try {
			pojo = pojoClass.newInstance();
			BeanUtils.populate(pojo, parameters);
		} catch (Exception e) {
			throw new RuntimeException("error extracting pojo from parameters", e);
		}

		return pojo;

	}
}
