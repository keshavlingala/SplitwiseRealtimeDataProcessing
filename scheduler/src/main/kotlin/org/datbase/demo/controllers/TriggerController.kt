package org.datbase.demo.controllers

import org.datbase.demo.jobs.SplitwiseJob
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import retrofit2.http.Query

@RestController
@RequestMapping("/job")
class TriggerController(
	val splitwiseJob: SplitwiseJob,
) {
	@GetMapping("splitwise")
	fun triggerSplitwiseJob(
		@Query("debug") debug: Boolean = false,
	): String {
		splitwiseJob.setDebug(debug)
		splitwiseJob.runEveryDay()
		return "Splitwise Job Triggered!"
	}
}