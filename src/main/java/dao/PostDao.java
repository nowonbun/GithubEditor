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
	
	@SuppressWarnings("unchecked")
	public List<Post> selectAllNotReservation() {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT p FROM Post p WHERE p.isdeleted = false and p.isreservation = false order by p.idx desc");
				return (List<Post>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Post> selectAllNotReservationOrderUpdatedate() {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT p FROM Post p WHERE p.isdeleted = false and p.isreservation = false order by p.lastupdateddate desc");
				return (List<Post>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Post> selectAllReservation() {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT p FROM Post p WHERE p.isdeleted = false and p.isreservation = true order by p.idx desc");
				return (List<Post>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	public long getCountByCategory(Category category) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT count(p) FROM Post p where p.isdeleted = false and p.category = :category");
			query.setParameter("category", category);
			return (long) query.getSingleResult();
		});
	}
	
	public long getCountByCategoryNotReservation(Category category) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT count(p) FROM Post p where p.isdeleted = false and p.category = :category and p.isreservation = false");
			query.setParameter("category", category);
			return (long) query.getSingleResult();
		});
	}

	@SuppressWarnings("unchecked")
	public List<Post> selectByCategory(Category category, int start, int count) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT p FROM Post p where p.isdeleted = false and p.category = :category order by p.idx desc");
			query.setParameter("category", category);
			query.setFirstResult(start);
			query.setMaxResults(count);
			return (List<Post>) query.getResultList();
		});
	}

	@SuppressWarnings("unchecked")
	public List<Post> selectByCategoryAll(Category category) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT p FROM Post p where p.isdeleted = false and p.category = :category order by p.idx desc");
			query.setParameter("category", category);
			return (List<Post>) query.getResultList();
		});
	}

	public long getCountByTitleLike(String keyword) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT count(p) FROM Post p where p.isdeleted = false and (p.title like CONCAT('%',:keyword,'%') or p.tag like CONCAT('%',:keyword,'%'))");
			query.setParameter("keyword", keyword);
			return (long) query.getSingleResult();
		});
	}

	@SuppressWarnings("unchecked")
	public List<Post> selectByTitleLike(String keyword, int start, int count) {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT p FROM Post p where p.isdeleted = false and (p.title like CONCAT('%',:keyword,'%') or p.tag like CONCAT('%',:keyword,'%'))  order by p.idx desc");
			query.setParameter("keyword", keyword);
			query.setFirstResult(start);
			query.setMaxResults(count);
			return (List<Post>) query.getResultList();
		});
	}

	public long getCount() {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT count(p) FROM Post p where p.isdeleted = false");
			return (long) query.getSingleResult();
		});
	}
	
	public long getCountNotReservation() {
		return transaction((em) -> {
			Query query = em.createQuery("SELECT count(p) FROM Post p where p.isdeleted = false and p.isreservation = false");
			return (long) query.getSingleResult();
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