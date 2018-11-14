package com.example.demo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import de.codecentric.boot.admin.notify.CompositeNotifier;
import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;

@Configuration
@EnableScheduling
public class NotifierConfig {

	/**
	 * used for multiple notifiation
	 * 
	 */
	private final ObjectProvider<List<Notifier>> otherNotifiers;

	public NotifierConfig(ObjectProvider<List<Notifier>> otherNotifiers) {
		this.otherNotifiers = otherNotifiers;
	}

	@Bean
	public FilteringNotifier filteringNotifier() {
		CompositeNotifier delegate = new CompositeNotifier(otherNotifiers.getIfAvailable());
		return new FilteringNotifier(delegate);
	}

	@Bean
	@Primary
	public RemindingNotifier remindingNotifier() {
		RemindingNotifier notifier = new RemindingNotifier(filteringNotifier());
		notifier.setReminderPeriod(TimeUnit.SECONDS.toMillis(2));
		return notifier;
	}

	/**
	 * set the reminder period for notification
	 */
	@Scheduled(fixedRate = 60_000L)
	public void remind() {
		remindingNotifier().sendReminders();
	}

}