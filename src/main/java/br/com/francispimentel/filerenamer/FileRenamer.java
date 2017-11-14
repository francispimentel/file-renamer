package br.com.francispimentel.filerenamer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.francispimentel.filerenamer.util.FileUtils;

public class FileRenamer {

	private static String[] FILE_EXTENSIONS = { "pdf", "jpeg", "jpg", "gif", "png", "xls", "xlsx", "zip" };

	public int renameFiles(File inputDir, Map<String, String> names) {
		int count = 0;
		for (String nomeOriginal : names.keySet()) {
			File busca = FileUtils.findFileInFolder(nomeOriginal, inputDir, FILE_EXTENSIONS);
			if (busca != null) {
				String extensao = busca.getName();
				extensao = extensao.substring(extensao.lastIndexOf("."));
				boolean sucesso = busca.renameTo(new File(inputDir, names.get(nomeOriginal) + extensao));

				int i = 0;
				while (i < 10 && !sucesso) {
					sucesso = busca.renameTo(new File(inputDir, names.get(nomeOriginal) + " (" + i + ")" + extensao));
					i++;
				}
				if (sucesso) {
					count++;
				}
			}
		}
		return count;
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
	public Map<String, String> extractFilesNames(InputStream workbookStream) throws IOException {
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
				novoNome.append(cell.getStringCellValue().trim());
			}
			map.put(primeira.getStringCellValue().trim(), novoNome.toString());
		}

		workbook.close();
		return map;
	}
}
