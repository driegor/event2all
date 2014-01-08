package org.uoc.pfc.eventual.utils.integration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Adapter {

	@Autowired
	DozerBeanMapper mapper;

	public <B> B toBean(Object entity, Class<B> beanClass) {
		return toBean(entity, beanClass, null);
	}

	public <B> B toBean(Object entity, Class<B> beanClass, String mappingCase) {

		if (entity == null) {
			return null;
		}

		B destObject = null;

		if (mappingCase != null) {
			destObject = mapper.map(entity, beanClass, mappingCase);
		} else {
			destObject = mapper.map(entity, beanClass);
		}
		return destObject;
	}

	public <B> B[] toBeans(Object[] sourceArray, Class<B> beanClass) {

		return toBeans(sourceArray, beanClass, null);
	}

	@SuppressWarnings("unchecked")
	public <B> B[] toBeans(Object[] sourceArray, Class<B> beanClass, String mappingCase) {

		if (sourceArray == null) {
			return (B[]) Array.newInstance(beanClass, 0);
		}

		B[] beanArray = (B[]) Array.newInstance(beanClass, sourceArray.length);
		int indx = 0;
		for (Object o : sourceArray) {
			beanArray[indx] = toBean(o, beanClass, mappingCase);
			indx++;
		}

		return beanArray;
	}

	public <B> Collection<B> toBeans(Collection<?> sourceList, Class<B> beanClass) {
		return toBeans(sourceList, beanClass, null);
	}

	public <B> Collection<B> toBeans(Collection<?> sourceList, Class<B> beanClass, String mappingCase) {
		Collection<B> beanList = new ArrayList<B>();

		if (sourceList == null) {
			return beanList;
		}

		for (Object o : sourceList) {
			beanList.add(toBean(o, beanClass, mappingCase));
		}
		return beanList;
	}
}
