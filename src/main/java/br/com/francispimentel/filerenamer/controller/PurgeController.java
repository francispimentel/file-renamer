package br.com.francispimentel.filerenamer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.francispimentel.filerenamer.util.FileUtils;

@Controller
@RequestMapping("purge")
public class PurgeController {

	@GetMapping
	@ResponseBody
	public String purgeTempDirectory() {
		FileUtils.purgeDirectory(FileUtils.TEMP_DIRECTORY);
		return "OK";
	}
}