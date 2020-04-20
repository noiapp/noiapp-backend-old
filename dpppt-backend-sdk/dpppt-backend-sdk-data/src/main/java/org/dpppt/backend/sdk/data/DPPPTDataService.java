/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2020. All rights reserved.
 */

package org.dpppt.backend.sdk.data;

import java.util.List;

import org.dpppt.backend.sdk.model.Exposee;
import org.joda.time.DateTime;

public interface DPPPTDataService {

	/**
	 * Upserts the given exposee
	 * 
	 * @param exposee the exposee to upsert
	 * @param appSource the app name
	 */
	void upsertExposee(Exposee exposee, String appSource);

	/**
	 * Returns all exposees for the given day [day: 00:00, day+1: 00:00] ordered by id
	 * 
	 * @param day the day for which exposees are requested
	 * @return exposee list
	 */
	List<Exposee> getSortedExposedForDay(DateTime day);

	/**
	 * Returns the maximum id of the stored exposed entries for the given day date
	 * 
	 * @param day the day for which id is required
	 * 
	 * @return the max id or 0
	 */
	Integer getMaxExposedIdForDay(DateTime day);

}
