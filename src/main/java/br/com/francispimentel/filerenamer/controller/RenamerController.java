package br.com.francispimentel.filerenamer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.francispimentel.filerenamer.util.FileUtils;
import br.com.francispimentel.filerenamer.util.ZipUtils;

@Controller
@RequestMapping
public class RenamerController {

	private static String[] FILE_EXTENSIONS = { "pdf", "jpeg", "jpg", "gif", "png", "xls", "xlsx", "zip" };

	@GetMapping
	public String primeiroAcesso() {
		return "renamer";
	}

	@PostMapping
	public void uploadArquivos(@RequestParam("workbook") MultipartFile workbook, @RequestParam("zip") MultipartFile zip,
			HttpServletResponse response) throws Exception {

		Map<String, String> nomes = extractFilesNames(workbook.getInputStream());
		File arquivosNoZip = ZipUtils.extractZip(zip.getInputStream());

		for (String nomeOriginal : nomes.keySet()) {
			File busca = FileUtils.findFileInFolder(nomeOriginal, arquivosNoZip, FILE_EXTENSIONS);
			if (busca != null) {
				String extensao = busca.getName();
				extensao = extensao.substring(extensao.lastIndexOf("."));
				busca.renameTo(new File(arquivosNoZip, nomes.get(nomeOriginal) + extensao));
			}
		}

		File zipPronto = ZipUtils.createZipFile(arquivosNoZip);

		response.setContentType("application/zip, application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=arquivos.zip");

		OutputStream out = response.getOutputStream();
		FileInputStream in = new FileInputStream(zipPronto);
		byte[] buffer = new byte[4096];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();

		FileUtils.purgeDirectory(arquivosNoZip);
		zipPronto.delete();

		out.flush();
	}

	/**
	 * Opens the Excel workbook and extracts the original and replacement file
	 * names
	 * 
	 * @param stream
	 *            The workbook file (xls or xlsx)
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> extractFilesNames(InputStream workbookStream) throws IOException {
		Map<String, String> map = new HashMap<>();

		Workbook workbook = null;

		try {
			workbook = new XSSFWorkbook(workbookStream);
		} catch (Exception e) {
			workbook = new HSSFWorkbook(workbookStream);
		}

		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();

		while (iterator.hasNext()) {
			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();

			Cell primeira = cellIterator.next();
			primeira.setCellType(CellType.STRING);

			StringBuffer novoNome = new StringBuffer();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				cell.setCellType(CellType.STRING);
				novoNome.append(cell.getStringCellValue());
			}
			map.put(primeira.getStringCellValue(), novoNome.toString());
		}

		workbook.close();
		return map;
	}
}