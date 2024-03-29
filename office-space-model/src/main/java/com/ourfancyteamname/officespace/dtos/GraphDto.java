package com.ourfancyteamname.officespace.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GraphDto {
  private ProcessGeneralDto cluster;
  private List<ProcessGeneralDto> nodes;
  private List<ProcessGeneralDto> clusterNodesPath;
}
