package controller.cron;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import common.FactoryDao;
import compile.CompileService;
import dao.CronhistoryDao;
import dao.PostDao;
import model.Cronhistory;
import model.Post;

@Component
public class Scheduller {
	@Scheduled(cron = "0 0 3 * * *")
	public void run() {
		boolean iscompile = false;
		Cronhistory history = new Cronhistory();
		history.setProcessedDate(new Date());
		history.setState(0);
		try {
			List<Post> posts = FactoryDao.getDao(PostDao.class).selectAllReservation();
			Date now = new Date();
			for (Post post : posts) {
				if (now.compareTo(post.getCreateddate()) >= 0) {
					post.setIsreservation(false);
					post.setCreateddate(new Date());
					post.setLastupdateddate(new Date());
					post.setCronhistory(history);
					FactoryDao.getDao(PostDao.class).update(post);
					iscompile = true;
				}
			}

			if (iscompile) {
				CompileService.getInstance().start(true);
			}
		} catch (Throwable e) {
			history.setState(1);
		}
		FactoryDao.getDao(CronhistoryDao.class).update(history);
	}
}
