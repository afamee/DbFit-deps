/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.batch.resultsOut;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import fit.Counts;
import fitlibrary.batch.trinidad.TestResult;
import fitlibrary.batch.trinidad.TestResultRepository;

public class FolderTestResultRepository implements TestResultRepository {
	private final String outputPath;
	private final PrintStream err;
	private final boolean showPasses;
	
	public FolderTestResultRepository(String outputPath, String suiteName, PrintStream err, boolean showPasses) {
		this.outputPath = selectFolderName(outputPath + "/" + suiteName+ "/" + formattedDateTime());
		new File(this.outputPath).mkdirs();
		this.err = err;
		this.showPasses = showPasses;
		new File(outputPath).mkdirs();
	}
	private static String formattedDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
	}
	private static String selectFolderName(String fileName) {
		if (new File(fileName).exists()) {
			for (int i = 1; i < 10000; i++) {
				String logFileName = fileName+"-"+i;
				if (!new File(logFileName).exists()) {
					return logFileName;
				}
			}
		}
		return fileName;
	}
	public void recordTestResult(TestResult tr) throws IOException {
		Counts counts = tr.getCounts();
		err.println(tr.getName()+  " right="+counts.right +", wrong="+counts.wrong+ ", ignores="+counts.ignores+", exceptions= "+counts.exceptions);
		if (showPasses || tr instanceof SuiteResult || (counts.wrong + counts.exceptions) > 0) {
			String finalPath=new File(outputPath,tr.getName()+".html").getAbsolutePath();
			FileWriter fw=new FileWriter(finalPath);
			String content = tr.getContent();
			content = content.replace("img src=\"/files/images/", "img src=\"");
			fw.write(content);
			fw.close();
		}
	}
	public void addFile(File f, String relativeFilePath)throws IOException {
		copy(f, new File(outputPath,relativeFilePath));
	}
	private void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);
	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
}