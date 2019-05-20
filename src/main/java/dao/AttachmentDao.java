package dao;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import common.AbstractDao;
import model.Attachment;
import model.Post;

public class AttachmentDao extends AbstractDao<Attachment> {
	protected AttachmentDao() {
		super(Attachment.class);
	}

	@SuppressWarnings("unchecked")
	public List<Attachment> findAll() {
		return transaction((em) -> {
			try {
				Query query = em.createNamedQuery("Attachment.findAll", Post.class);
				return (List<Attachment>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<Attachment> selectAll() {
		return transaction((em) -> {
			try {
				Query query = em.createQuery("SELECT a FROM Attachment a WHERE a.isdeleted = false order by a.idx desc");
				return (List<Attachment>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}
		});
	}
}