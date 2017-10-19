package com.chiucheng.timestampgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import java.sql.Timestamp;

@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class TimestampGenApplication {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	private static Map uuidMap = new HashMap<String, AtomicLong>();

	@RequestMapping("/uuid")
	public static long uniqueTimestamp(@RequestParam(value = "v") String user) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		long now = Long.parseLong(sdf.format(timestamp));
		if(!uuidMap.containsKey(user)){
			uuidMap.put(user, new AtomicLong());
		}
		AtomicLong atomicLastTime = (AtomicLong)uuidMap.get(user);
		while(true) {
			long lastTime = atomicLastTime.get();
			if (lastTime >= now)
				now = lastTime+1;
			if (atomicLastTime.compareAndSet(lastTime, now))
				uuidMap.put(user, atomicLastTime);
			return now;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(TimestampGenApplication.class, args);
	}
}
