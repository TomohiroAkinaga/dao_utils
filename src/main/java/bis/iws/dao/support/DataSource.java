package bis.iws.dao.support;

/**
 * @author Tomohiro Akinaga
 */
public interface DataSource<T> {

	T getKey();

	boolean isEditable();
}
