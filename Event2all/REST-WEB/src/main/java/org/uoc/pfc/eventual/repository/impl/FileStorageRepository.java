package org.uoc.pfc.eventual.repository.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;
import org.uoc.pfc.eventual.repository.IFileStorageRepository;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Component
public class FileStorageRepository implements IFileStorageRepository {

    @Autowired
    private GridFsOperations gridOperations;

    @Override
    public String save(InputStream inputStream, String contentType, String filename) {

	return save(inputStream, contentType, filename, null);
    }

    @Override
    public String save(InputStream inputStream, String contentType, String filename, Map<String, String> metaData) {

	if (metaData != null) {
	    for (Entry<String, String> entry : metaData.entrySet()) {
		metaData.put(entry.getKey(), entry.getValue());
	    }
	}
	GridFSFile file = gridOperations.store(inputStream, filename, metaData);
	return file.getId().toString();

    }

    public byte[] getContent(String imageId) throws IOException {
	List<GridFSDBFile> result = gridOperations.find(new Query().addCriteria(Criteria.where("_id").is(new ObjectId(imageId))));

	byte[] output = null;

	if ((result != null) && (result.size() > 0)) {
	    output = IOUtils.toByteArray(result.get(0).getInputStream());
	}
	return output;
    }
}