package dao;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import common.AbstractDao;
import model.Category;

public class CategoryDao extends AbstractDao<Category> {
	protected CategoryDao() {
		super(Category.class);
	}

	@SuppressWarnings("unchecked")
	public List<Category> findAll() {
		return transaction((em) -> {
			try {
				Query query = em.createNamedQuery("Category.findAll", Category.class);
				return (List<Category>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<Category> selectAll() {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT c FROM Category c WHERE c.isactive = true order by c.seq asc");
				return (List<Category>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	public Category select(String code) {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT c FROM Category c WHERE c.code = :code");
				query.setParameter("code", code);
				return (Category) query.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}
		});
	}
	
}