package org.uoc.pfc.eventual.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.uoc.pfc.eventual.service.IFileService;

@Controller
@RequestMapping(value = "/api/file")
public class FileController {

	@Autowired
	IFileService fileService;

	@RequestMapping(value = "/show/{imageId}", method = RequestMethod.GET)
	public void show(@PathVariable("imageId") String imageId, HttpServletResponse response) {
		try {
			response.getOutputStream().write(fileService.getFileContent(imageId));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
