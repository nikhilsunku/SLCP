package com.health.slcsp.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.health.slcsp.controller.dto.PlansDto;
import com.health.slcsp.controller.dto.PlansRevisedDto;
import com.health.slcsp.controller.dto.SlcspDto;
import com.health.slcsp.controller.dto.ZipDto;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SlcspDataUtil {

	@Autowired
	private ModelMapper modelMapper;

	public void readAndUpdatePlanData() throws Exception {
		URL planResource = getClass().getClassLoader().getResource("plans.csv");
		URL zipResource = getClass().getClassLoader().getResource("zips.csv");
		URL slcspResource = getClass().getClassLoader().getResource("slcsp.csv");
		List<PlansDto> planDtoList = new CsvToBeanBuilder(new FileReader(planResource.getPath()))
				.withType(PlansDto.class).build().parse();
		// filter only silver plans
		List<PlansDto> silverPlanDtoList = planDtoList.stream()
				.filter(silverPlan -> "Silver".equalsIgnoreCase(silverPlan.getMetalLevel()))
				.collect(Collectors.toList());
		// GroupBy State
		Map<String, List<PlansDto>> stateSilverPlan = silverPlanDtoList.stream()
				.collect(Collectors.groupingBy(PlansDto::getState));

		List<ZipDto> zipDtoList = new CsvToBeanBuilder(new FileReader(zipResource.getPath())).withType(ZipDto.class)
				.build().parse();

		List<SlcspDto> slcspList = new CsvToBeanBuilder(new FileReader(slcspResource.getPath()))
				.withType(SlcspDto.class).build().parse();

		List<SlcspDto> SlcspResponse = new ArrayList<>();
		for (SlcspDto slcsp : slcspList) {
			List<ZipDto> zipcodeList = zipDtoList.stream()
					.filter(zipcode -> zipcode.getZipCode().equalsIgnoreCase(slcsp.getZipCode()))
					.collect(Collectors.toList());
			if (!zipcodeList.isEmpty()) {
				List<PlansDto> plans = stateSilverPlan.get(zipcodeList.get(0).getState());
				if (Objects.nonNull(plans) && !plans.isEmpty()) {
					List<PlansDto> plansPerRateArea = plans.stream()
							.filter(plan -> plan.getRateArea().equalsIgnoreCase(zipcodeList.get(0).getRateArea()))
							.collect(Collectors.toList());
					List<PlansRevisedDto> planRevisedDto = plansPerRateArea.stream()
							.map(planPerArea -> modelMapper.map(planPerArea, PlansRevisedDto.class))
							.collect(Collectors.toList());
					planRevisedDto.sort((PlansRevisedDto firstPlan, PlansRevisedDto secondPlan) -> firstPlan.getRate()
							.compareTo(secondPlan.getRate()));
					if (planRevisedDto.size() >= 2) {
						slcsp.setRate(planRevisedDto.get(1).getRate().toString());
					}
				}
			}
			SlcspResponse.add(slcsp);
		}
		writeDataForCustomSeparatorCSV(SlcspResponse);
	}

	public static void writeDataForCustomSeparatorCSV(List<SlcspDto> slcspList) throws Exception {

		// first create file object for file placed at location
		// specified by filepath
		String fileLocation = new File("src\\main\\resources").getAbsolutePath() + "\\" + "output.csv";
		FileWriter fileWriter = new FileWriter(fileLocation);

		try {
			ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
			mappingStrategy.setType(SlcspDto.class);

			// Creating StatefulBeanToCsv object
			StatefulBeanToCsvBuilder<SlcspDto> builder = new StatefulBeanToCsvBuilder(fileWriter);
			StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();

			// Write list to StatefulBeanToCsv object
			beanWriter.write(slcspList);

			// closing the writer object
			fileWriter.close();

			// closing writer connection
			fileWriter.close();
		} catch (IOException e) {
			log.error("Error in ", e);
		}
	}

}
