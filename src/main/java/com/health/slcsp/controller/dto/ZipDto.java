package com.health.slcsp.controller.dto;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ZipDto {

	@CsvBindByPosition(position = 0)
	private String zipCode;

	@CsvBindByPosition(position = 1)
	private String state;

	@CsvBindByPosition(position = 2)
	private String countryCode;

	@CsvBindByPosition(position = 3)
	private String name;

	@CsvBindByPosition(position = 4)
	private String rateArea;

}
