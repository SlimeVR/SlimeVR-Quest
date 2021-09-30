package io.eiren.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class FileLogFormatter extends Formatter {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(dateFormat.format(record.getMillis()));
		Level localLevel = record.getLevel();
		if(localLevel == Level.FINEST)
			sb.append(" [FINEST] ");
		else if(localLevel == Level.FINER)
			sb.append(" [FINER] ");
		else if(localLevel == Level.FINE)
			sb.append(" [FINE] ");
		else if(localLevel == Level.INFO)
			sb.append(" [INFO] ");
		else if(localLevel == Level.WARNING)
			sb.append(" [WARNING] ");
		else if(localLevel == Level.SEVERE)
			sb.append(" [SEVERE] ");
		else
			sb.append(" [" + localLevel.getLocalizedName() + "] ");

		sb.append(record.getMessage());
		sb.append('\n');

		Throwable localThrowable = record.getThrown();
		if(localThrowable != null) {
			StringWriter localStringWriter = new StringWriter();
			localThrowable.printStackTrace(new PrintWriter(localStringWriter));
			sb.append(localStringWriter.toString());
		}

		String message = sb.toString();		
		Object parameters[] = record.getParameters();
		if(parameters == null || parameters.length == 0)
			return message;
		if(message.indexOf("{0") >= 0 || message.indexOf("{1") >= 0 || message.indexOf("{2") >= 0 || message.indexOf("{3") >= 0)
			return java.text.MessageFormat.format(message, parameters);
		return message;
	}
}
