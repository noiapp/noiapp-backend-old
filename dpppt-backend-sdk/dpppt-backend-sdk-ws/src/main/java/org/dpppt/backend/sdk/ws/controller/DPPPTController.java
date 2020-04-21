package org.dpppt.backend.sdk.ws.controller;

import org.dpppt.backend.sdk.data.DPPPTDataService;
import org.dpppt.backend.sdk.data.EtagGeneratorInterface;
import org.dpppt.backend.sdk.model.ExposedOverview;
import org.dpppt.backend.sdk.model.Exposee;
import org.dpppt.backend.sdk.model.ExposeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/v1")
public class DPPPTController {

	private final DPPPTDataService dataService;
	private final EtagGeneratorInterface etagGenerator;
	private final String appSource;
	private final int exposedListCacheContol;

	private static final String DATE_PATTERN = "yyyy-MM-dd";

	private static final Logger logger = LoggerFactory.getLogger(DPPPTController.class);

	public DPPPTController(DPPPTDataService dataService, EtagGeneratorInterface etagGenerator, String appSource,
			int exposedListCacheControl) {
		this.dataService = dataService;
		this.appSource = appSource;
		this.etagGenerator = etagGenerator;
		this.exposedListCacheContol = exposedListCacheControl;
	}

	@CrossOrigin(origins = { "https://editor.swagger.io" })
	@GetMapping(value = "")
	public @ResponseBody String hello() {
		return "Hello from DP3T WS";
	}

	@CrossOrigin(origins = { "https://editor.swagger.io" })
	@PostMapping(value = "/exposed")
	public @ResponseBody ResponseEntity<String> addExposee(@Valid @RequestBody ExposeeRequest exposeeRequest,
			@RequestHeader(value = "User-Agent", required = true) String userAgent) {
		if (isValidBase64(exposeeRequest.getKey())) {
			Exposee exposee = new Exposee();
			exposee.setKey(exposeeRequest.getKey());
			exposee.setOnset(exposeeRequest.getOnset());
			dataService.upsertExposee(exposee, appSource);
			return ResponseEntity.ok().build();

		} else {
			return new ResponseEntity<>("No valid base64 key", HttpStatus.BAD_REQUEST);
		}
	}

	@CrossOrigin(origins = { "https://editor.swagger.io" })
	@GetMapping(value = "/exposed/{dayDate}")
	public @ResponseBody ResponseEntity<ExposedOverview> getExposed(
			@PathVariable @DateTimeFormat(pattern = DATE_PATTERN) LocalDate dayDate,
			WebRequest request) {
		int max = dataService.getMaxExposedIdForDay(dayDate);
		String etag = etagGenerator.getEtag(max);
		if (request.checkNotModified(etag)) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		} else {
			List<Exposee> exposeeList = dataService.getSortedExposedForDay(dayDate);
			ExposedOverview overview = new ExposedOverview(exposeeList);
			return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofMinutes(exposedListCacheContol)))
					.body(overview);
		}
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<?> invalidArguments() {
		return ResponseEntity.badRequest().build();
	}

	private boolean isValidBase64(String value) {
		try {
			Base64.getDecoder().decode(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
