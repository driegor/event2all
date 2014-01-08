package org.uoc.pfc.eventual.repository;

import java.io.InputStream;
import java.util.Map;

public interface IFileStorageRepository {

	String save(InputStream inputStream, String contentType, String filename);

	String save(InputStream inputStream, String contentType, String filename, Map<String, String> metaData);

}
