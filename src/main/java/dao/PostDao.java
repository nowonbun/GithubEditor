package dao;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import common.AbstractDao;
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
}