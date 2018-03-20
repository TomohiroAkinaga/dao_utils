package bis.iws.dao.support;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bis.iws.dao.support.exception.IllegalDatabaseAccessException;

/**
 * @author Tomohiro Akinaga
 */
public class EntityManagerFactoryProvider<T extends DataSource<U>, U> implements Closeable {

	private static final Logger log = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

	private Map<T, EntityManagerFactory> delegate;

	private ThreadLocal<Map<U, EntityManager>> threadLocal = new ThreadLocal<Map<U, EntityManager>>();

	public Map<T, EntityManagerFactory> getDelegate() {
		return delegate;
	}

	public void setDelegate(Map<T, EntityManagerFactory> delegate) {
		this.delegate = delegate;
	}

	public void init(T t) {
		if (threadLocal.get() == null) {
			threadLocal.set(new HashMap<>());
		} else {
			if (threadLocal.get().containsKey(t.getKey())) {
				log.warn("this thread has already entity manager. [key : {}]", t.getKey());
				return;
			}
		}

		threadLocal.get().put(t.getKey(), delegate.get(t).createEntityManager());
	}

	/**
	 * @return {@link EntityManager}
	 */
	public EntityManager getEntityManager(U u) {
		return threadLocal.get().get(u);
	}

	/**
	 * begin transaction
	 */
	public void beginTransaction(T t) {

		if (threadLocal.get() == null) {
			throw new IllegalDatabaseAccessException("no entitymanager to use.");
		}

		if (this.threadLocal.get().get(t.getKey()).isOpen()
				&& !this.threadLocal.get().get(t.getKey()).getTransaction().isActive()) {
			this.threadLocal.get().get(t.getKey()).getTransaction().begin();
		}
	}

	/**
	 * commit transaction if exists
	 */
	public void commit() {

		if (this.threadLocal.get() == null) {
			throw new IllegalDatabaseAccessException("no entitymanager to use.");
		}

		Map<U, EntityManager> emMap = this.threadLocal.get();
		emMap.values().forEach(em -> {
			if (em.isOpen() && em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
		});
	}

	/**
	 * rollback transaction if exists
	 */
	public void rollback() {

		if (this.threadLocal.get() == null) {
			throw new IllegalDatabaseAccessException("no entitymanager to use.");
		}

		Map<U, EntityManager> emMap = this.threadLocal.get();
		emMap.values().forEach(em -> {
			if (em.isOpen() && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		});
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() {

		if (this.threadLocal.get() != null) {
			Map<U, EntityManager> emMap = this.threadLocal.get();
			emMap.values().forEach(em -> {
				if (em.isOpen()) {
					em.close();
				}
			});
		}

		threadLocal.remove();
	}

}
