package com.health.slcsp.controller.dto;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SlcspDto {

	@CsvBindByPosition(position = 0)
	private String zipCode;

	@CsvBindByPosition(position = 1)
	private String rate;

}
