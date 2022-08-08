package com.health.slcsp.controller.dto;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlansDto {

	@CsvBindByPosition(position = 0)
	private String planId;

	@CsvBindByPosition(position = 1)
	private String state;

	@CsvBindByPosition(position = 2)
	private String metalLevel;

	@CsvBindByPosition(position = 3)
	private String rate;

	@CsvBindByPosition(position = 4)
	private String rateArea;

}
