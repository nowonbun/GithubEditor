package dao;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import common.AbstractDao;
import model.Category;
import model.Post;

public class PostDao extends AbstractDao<Post> {
	protected PostDao() {
		super(Post.class);
	}

	@SuppressWarnings("unchecked")
	public List<Post> findAll() {
		return transaction((em) -> {
			try {
				Query query = em.createNamedQuery("Post.findAll", Post.class);
				return (List<Post>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<Post> selectAll() {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT p FROM Post p WHERE p.isdeleted = false order by p.idx desc");
				return (List<Post>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	public long getCountByCategory(Category category) {
		return transaction((em) -> {
			Query query = em
					.createQuery("SELECT count(p) FROM Post p where p.isdeleted = false and p.category = :category");
			query.setParameter("category", category);
			return (long) query.getSingleResult();
		});
	}

	@SuppressWarnings("unchecked")
	public List<Post> selectByCategory(Category category, int start, int count) {
		return transaction((em) -> {
			Query query = em.createQuery(
					"SELECT p FROM Post p where p.isdeleted = false and p.category = :category order by p.idx desc");
			query.setParameter("category", category);
			query.setFirstResult(start);
			query.setMaxResults(count);
			return (List<Post>) query.getResultList();
		});
	}

	public Post select(int idx) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT p FROM Post p where p.idx = :idx");
			query.setParameter("idx", idx);
			return (Post) query.getSingleResult();
		});
	}
}