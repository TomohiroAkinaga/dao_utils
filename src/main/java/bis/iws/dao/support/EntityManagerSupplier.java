package bis.iws.dao.support;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Tomohiro Akinaga
 */
public class EntityManagerSupplier<T extends DataSource<U>, U> {

	@Autowired
	protected EntityManagerFactoryProvider<T, U> emf;

	/**
	 * Get {@link EntityManager}
	 * 
	 * @return {@link EntityManager}
	 */
	public EntityManager getEntityManager(U u) {
		return emf.getEntityManager(u);
	}
}
