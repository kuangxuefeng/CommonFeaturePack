package com.cfp.kxf.logs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogUtils {
	private LogListener listener;
	private LogType logType = LogType.INFO;
	private static final LogUtils instance = new LogUtils(new LogListener() {
		
		@Override
		public void w(String w) {
			System.out.println(w);
		}
		
		@Override
		public void i(String i) {
			System.out.println(i);
		}
		
		@Override
		public void e(String e) {
			System.err.println(e);
		}
		
		@Override
		public void d(String d) {
			System.out.println(d);
		}
	}, LogType.DEBUG);

	public static enum LogType {
		DEBUG(1), INFO(2), WARN(3), ERROR(4);
		public int value;

		private LogType(int value) {
			this.value = value;
		}
	}
	
	public static LogUtils getDefaultInstance() {
		return instance;
	}

	public LogUtils() {

	}

	public LogUtils(LogListener listener) {
		this.listener = listener;
	}
	
	public LogUtils(LogListener listener, LogType logType) {
		this.listener = listener;
		this.logType = logType;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
	}

	public void setListener(LogListener listener) {
		this.listener = listener;
	}

	public void i(String i) {
		if (null != listener && logType.value <= LogType.INFO.value) {
			listener.i(getModule() + "  " + i);
		}
	}

	public void d(String d) {
		if (null != listener && logType.value <= LogType.DEBUG.value) {
			listener.d(getModule() + "  " + d);
		}
	}

	public void w(String w) {
		if (null != listener && logType.value <= LogType.WARN.value) {
			listener.w(getModule() + "  " + w);
		}
	}

	public void e(String e) {
		if (null != listener && logType.value <= LogType.ERROR.value) {
			listener.e(getModule() + "  " + e);
		}
	}

	public void e(String e, Exception ex) {
		if (null != listener && logType.value <= LogType.ERROR.value) {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String result = writer.toString();
			listener.e(getModule() + "  " + e);
			listener.e(result);
		}
	}
	
	private String getModule() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = -1;
        int methodCount = 2;
        for (int i = 3; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LogUtils.class.getName())) {
                stackOffset = i;
                break;
            }
        }
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = stackOffset;
            String simpleClassName = getSimpleClassName(trace[stackIndex]
                    .getClassName());
            if (simpleClassName.startsWith("TLog")) {
                continue;
            } else {
                i = 0;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(") [")
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".").append(trace[stackIndex].getMethodName())
                    .append("]");
            return builder.toString();
        }
        return "-----";
    }
	
	private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

	public interface LogListener {
		void d(String d);

		void i(String i);

		void w(String w);

		void e(String e);
	}
}
