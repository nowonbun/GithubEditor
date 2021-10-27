package test;

import java.util.Date;
import common.FactoryDao;
import dao.AnalysisDao;

public class DatabaseTest {
  public static void main(String[] args) {
    var count = FactoryDao.getDao(AnalysisDao.class).getCount(new Date());
    System.out.println(count);
  }
}
