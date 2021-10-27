package dao;

import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import common.AbstractDao;
import common.Util;
import model.Analysis;

public class AnalysisDao extends AbstractDao<Analysis> {
  protected AnalysisDao() {
    super(Analysis.class);
  }

  public long getCount(Date date) {
    return transaction((em) -> {
      try {
        var query = em.createQuery("SELECT count(a) FROM Analysis a WHERE function('date_format', a.createddate, '%Y%m%d') = :date");
        query.setParameter("date", Util.convertDatabaseFormat(date));
        return (long) query.getSingleResult();
      } catch (NoResultException e) {
        return null;
      }
    });
  }
  @SuppressWarnings("unchecked")
  public List<Analysis> getList(Date date) {
    return transaction((em) -> {
      try {
        var query = em.createQuery("SELECT a FROM Analysis a WHERE function('date_format', a.createddate, '%Y%m%d') = :date order by a.idx desc");
        query.setParameter("date", Util.convertDatabaseFormat(date));
        return (List<Analysis>) query.getResultList();
      } catch (NoResultException e) {
        return null;
      }
    });
  }
}
