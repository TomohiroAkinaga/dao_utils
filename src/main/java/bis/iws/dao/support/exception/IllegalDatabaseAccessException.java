package bis.iws.dao.support.exception;

import biz.iws.core.exception.ApplicationException;

public class IllegalDatabaseAccessException extends ApplicationException {

	private static final long serialVersionUID = -1080966645605210576L;

	public IllegalDatabaseAccessException(String logMessage) {
		this(null, logMessage);
	}

	public IllegalDatabaseAccessException(Throwable t, String logMessage) {
		super(t, logMessage);
	}

}
