package controller.ajax;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import common.FactoryDao;
import dao.AnalysisDao;
import dao.AttachmentDao;
import dao.CategoryDao;
import dao.PostDao;

@Configuration
public class ApplicationConfig {
  @Bean(name = "AttachmentDao")
  public AttachmentDao getUserDao() {
    return FactoryDao.getDao(AttachmentDao.class);
  }

  @Bean(name = "CategoryDao")
  public CategoryDao getUuidgeneratorDao() {
    return FactoryDao.getDao(CategoryDao.class);
  }

  @Bean(name = "PostDao")
  public PostDao getStateDao() {
    return FactoryDao.getDao(PostDao.class);
  }

  @Bean(name = "AnalysisDao")
  public AnalysisDao getAnalysisDao() {
    return FactoryDao.getDao(AnalysisDao.class);
  }
}
