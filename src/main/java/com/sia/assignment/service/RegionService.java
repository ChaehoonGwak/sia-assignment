package com.sia.assignment.service;


import com.sia.assignment.domain.Aoi;
import com.sia.assignment.domain.Region;
import com.sia.assignment.dto.AreaRequestDto;
import com.sia.assignment.dto.AreaResponseDto;
import com.sia.assignment.repository.AoiRepository;
import com.sia.assignment.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class RegionService {
    private final RegionRepository regionRepository;
    private final AoiRepository aoiRepository;

    public Region getRegion(){
        Region region = regionRepository.getById(3L);

        System.out.println(region.getId());
        System.out.println(region.getName());
        System.out.println(region.getArea());
        return region;
    }

    public void saveRegion(AreaRequestDto requestDto) throws ParseException {
        Polygon area = (Polygon)wktToGeometry(requestDto.getArea());

        Region region = Region.builder()
                .name(requestDto.getName())
                .area(area)
                .build();

        regionRepository.save(region);
    }

    public List<AreaResponseDto> intersectRegionAndAoi(Long regionId){
        Optional<Region> region = regionRepository.findById(regionId);

        System.out.println(region);
        List<Aoi> aoiInRegionList = new ArrayList<>();

        if(region == null){
            return null;
        }
        else{
            aoiInRegionList = aoiRepository.findAoiListInRegion(regionId);
            List<AreaResponseDto> areaResponseDtoList = new ArrayList<>();

            for (Aoi aoi: aoiInRegionList){
                AreaResponseDto areaResponseDto = AreaResponseDto.builder()
                        .id(aoi.getId())
                        .name(aoi.getName())
                        .area(aoi.getArea().toString())
                        .build();
                areaResponseDtoList.add(areaResponseDto);
            }

            return areaResponseDtoList;
        }
    }

    private Geometry wktToGeometry(String wellKnownText) throws ParseException {
        WKTReader fromText = new WKTReader();
        Geometry geom = null;
        geom = fromText.read(wellKnownText);
        System.out.println(geom);
        System.out.println(geom.getClass());
        return geom;
    }

}
