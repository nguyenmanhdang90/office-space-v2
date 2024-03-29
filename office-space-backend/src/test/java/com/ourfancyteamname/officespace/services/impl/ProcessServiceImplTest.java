package com.ourfancyteamname.officespace.services.impl;

import com.ourfancyteamname.officespace.db.converters.dtos.ProcessGeneralConverter;
import com.ourfancyteamname.officespace.db.entities.ClusterNode;
import com.ourfancyteamname.officespace.db.entities.ClusterNodePath;
import com.ourfancyteamname.officespace.db.entities.ProcessCluster;
import com.ourfancyteamname.officespace.db.repos.ClusterNodePathRepository;
import com.ourfancyteamname.officespace.db.repos.ClusterNodeRepository;
import com.ourfancyteamname.officespace.db.repos.ProcessClusterRepository;
import com.ourfancyteamname.officespace.dtos.ClusterNodeEditDto;
import com.ourfancyteamname.officespace.dtos.GraphDto;
import com.ourfancyteamname.officespace.dtos.ProcessGeneralDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ProcessServiceImplTest {

  @InjectMocks
  private ProcessServiceImpl service;

  @Mock
  private EntityManager entityManager;

  @Mock
  private ProcessClusterRepository clusterRepository;

  @Mock
  private ClusterNodeRepository clusterNodeRepository;

  @Mock
  private ClusterNodePathRepository pathRepository;

  @Mock
  private ProcessGeneralConverter processGeneralConverter;

  @Test(expected = IllegalArgumentException.class)
  public void getGraph_notFound() {
    Integer clusterId = 1;
    Mockito.when(clusterRepository.findById(clusterId)).thenReturn(Optional.empty());
    service.getGraph(clusterId);
  }

  @Test
  public void getGraph_success() {
    Integer clusterId = 1;
    ProcessCluster cluster = ProcessCluster.builder().id(clusterId).build();
    ProcessGeneralDto clusterDto = ProcessGeneralDto.builder().id(clusterId).build();
    Mockito.when(clusterRepository.findById(clusterId)).thenReturn(Optional.of(cluster));
    Mockito.when(processGeneralConverter.fromClusterToDto(cluster))
        .thenReturn(clusterDto);
    Mockito.when(pathRepository.findAllByCLusterId(clusterId))
        .thenReturn(Arrays.asList(ClusterNodePath.builder().build()));
    service.getGraph(clusterId);
    Mockito.verify(processGeneralConverter, Mockito.times(1)).fromClusterToDto(cluster);
    Mockito.verify(clusterRepository, Mockito.times(1)).findById(clusterId);
    Mockito.verify(clusterNodeRepository, Mockito.times(1)).getClusterNodesByClusterId(clusterId);
    Mockito.verify(pathRepository, Mockito.times(1)).findAllByCLusterId(clusterId);
    Mockito.verify(processGeneralConverter, Mockito.times(1)).fromPathToDto(Mockito.any());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addNodeToCluster_duplicated() {
    Integer clusterId = 1;
    Integer nodeId = 1;
    ProcessGeneralDto cluster = ProcessGeneralDto.builder().id(clusterId).build();
    ProcessGeneralDto node = ProcessGeneralDto.builder().id(nodeId).build();
    GraphDto graphDto = GraphDto.builder().cluster(cluster).nodes(Arrays.asList(node)).build();
    Mockito.when(clusterNodeRepository.existsByClusterIdAndNodeId(clusterId, nodeId)).thenReturn(true);
    service.addNodeToCluster(graphDto);
  }

  @Test
  public void addNodeToCluster_success() {
    Integer clusterId = 1;
    Integer nodeId = 1;
    ProcessGeneralDto cluster = ProcessGeneralDto.builder().id(clusterId).build();
    ProcessGeneralDto node = ProcessGeneralDto.builder().id(nodeId).build();
    GraphDto graphDto = GraphDto.builder().cluster(cluster).nodes(Arrays.asList(node)).build();
    Mockito.when(clusterNodeRepository.existsByClusterIdAndNodeId(clusterId, nodeId)).thenReturn(false);
    service.addNodeToCluster(graphDto);
    Mockito.verify(clusterNodeRepository, Mockito.times(1)).save(Mockito.any(ClusterNode.class));
  }

  @Test
  public void addSinglePath_success() {
    int from = 1;
    int to = 2;
    service.addSinglePath(from, to);
    Mockito.verify(pathRepository, Mockito.times(1)).save(Mockito.any(ClusterNodePath.class));
  }

  @Test
  public void removePath_success() {
    int pathId = 1;
    service.removePath(pathId);
    Mockito.verify(pathRepository, Mockito.times(1)).deleteById(pathId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeNodeFromCluster_notFound() {
    int clusterNodeId = 1;
    Mockito.when(clusterNodeRepository.findById(clusterNodeId)).thenReturn(Optional.empty());
    service.removeNodeFromCluster(clusterNodeId);
  }

  @Test
  public void removeNodeFromCluster_success() {
    int clusterNodeId = 1;
    ClusterNode clusterNode = ClusterNode.builder().id(clusterNodeId).build();
    Mockito.when(clusterNodeRepository.findById(clusterNodeId)).thenReturn(Optional.of(clusterNode));
    service.removeNodeFromCluster(clusterNodeId);
    Mockito.verify(clusterNodeRepository, Mockito.times(1)).findById(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1))
        .removeByClusterNodeIdToOrClusterNodeIdFrom(clusterNodeId, clusterNodeId);
    Mockito.verify(entityManager, Mockito.times(1)).flush();
    Mockito.verify(clusterNodeRepository, Mockito.times(1)).delete(clusterNode);
  }

  @Test
  public void editClusterNode_emptyInputEmptyOutput() {
    int clusterNodeId = 1;
    List<ProcessGeneralDto> inputs = Collections.emptyList();
    List<ProcessGeneralDto> outputs = Collections.emptyList();
    ClusterNodeEditDto editDto = ClusterNodeEditDto.builder().id(clusterNodeId).input(inputs).output(outputs).build();
    Mockito.when(pathRepository.findByClusterNodeIdFrom(clusterNodeId)).thenReturn(Collections.emptyList());
    Mockito.when(pathRepository.findByClusterNodeIdTo(clusterNodeId)).thenReturn(Collections.emptyList());
    service.editClusterNode(editDto);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdFrom(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdTo(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(2)).deleteAll(Collections.emptyList());
  }

  @Test
  public void editClusterNode_emptyInput() {
    int clusterNodeId = 1;
    List<ProcessGeneralDto> inputs = Collections.emptyList();
    List<ProcessGeneralDto> outputs = Arrays.asList(ProcessGeneralDto.builder().build());
    ClusterNodeEditDto editDto = ClusterNodeEditDto.builder().id(clusterNodeId).input(inputs).output(outputs).build();
    Mockito.when(pathRepository.findByClusterNodeIdFrom(clusterNodeId)).thenReturn(Collections.emptyList());
    Mockito.when(pathRepository.findByClusterNodeIdTo(clusterNodeId)).thenReturn(Collections.emptyList());
    Mockito.when(processGeneralConverter.fromClusterNodeEditToPath(outputs.get(0)))
        .thenReturn(ClusterNodePath.builder().build());
    service.editClusterNode(editDto);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdFrom(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdTo(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).deleteAll(Collections.emptyList());
    Mockito.verify(processGeneralConverter, Mockito.times(1)).fromClusterNodeEditToPath(Mockito.any());
    Mockito.verify(pathRepository, Mockito.times(1)).saveAll(Mockito.any());
  }

  @Test
  public void editClusterNode_emptyInput2() {
    int clusterNodeId = 1;
    int clusterIdNodeTo = 2;
    int clusterIdNodeFrom = 3;
    List<ProcessGeneralDto> inputs = Collections.emptyList();
    List<ProcessGeneralDto> outputs =
        Arrays.asList(ProcessGeneralDto.builder().clusterNodeIdTo(clusterIdNodeTo).build());
    List<ClusterNodePath> clusterNodePathsFrom =
        Arrays.asList(ClusterNodePath.builder().clusterNodeIdTo(clusterIdNodeTo).build());
    ClusterNodeEditDto editDto = ClusterNodeEditDto.builder().id(clusterNodeId).input(inputs).output(outputs).build();
    Mockito.when(pathRepository.findByClusterNodeIdFrom(clusterNodeId)).thenReturn(clusterNodePathsFrom);
    Mockito.when(pathRepository.findByClusterNodeIdTo(clusterNodeId)).thenReturn(Collections.emptyList());
    service.editClusterNode(editDto);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdFrom(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdTo(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).deleteAll(Collections.emptyList());
    Mockito.verify(pathRepository, Mockito.times(1)).saveAll(Mockito.any());
  }


  @Test
  public void editClusterNode_emptyOutput() {
    int clusterNodeId = 1;
    List<ProcessGeneralDto> inputs = Arrays.asList(ProcessGeneralDto.builder().build());
    List<ProcessGeneralDto> outputs = Collections.emptyList();
    ClusterNodeEditDto editDto = ClusterNodeEditDto.builder().id(clusterNodeId).input(inputs).output(outputs).build();
    Mockito.when(pathRepository.findByClusterNodeIdFrom(clusterNodeId)).thenReturn(Collections.emptyList());
    Mockito.when(pathRepository.findByClusterNodeIdTo(clusterNodeId)).thenReturn(Collections.emptyList());
    Mockito.when(processGeneralConverter.fromClusterNodeEditToPath(inputs.get(0)))
        .thenReturn(ClusterNodePath.builder().build());
    service.editClusterNode(editDto);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdFrom(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdTo(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).deleteAll(Collections.emptyList());
    Mockito.verify(processGeneralConverter, Mockito.times(1)).fromClusterNodeEditToPath(Mockito.any());
    Mockito.verify(pathRepository, Mockito.times(1)).saveAll(Mockito.any());
  }

  @Test
  public void editClusterNode_emptyOutput2() {
    int clusterNodeId = 1;
    int clusterIdNodeTo = 2;
    int clusterIdNodeFrom = 3;
    List<ProcessGeneralDto> inputs =
        Arrays.asList(ProcessGeneralDto.builder().clusterNodeIdFrom(clusterIdNodeFrom).build());
    List<ProcessGeneralDto> outputs = Collections.emptyList();
    List<ClusterNodePath> clusterNodePathsFrom =
        Arrays.asList(ClusterNodePath.builder().clusterNodeIdFrom(clusterIdNodeFrom).build());
    ClusterNodeEditDto editDto = ClusterNodeEditDto.builder().id(clusterNodeId).input(inputs).output(outputs).build();
    Mockito.when(pathRepository.findByClusterNodeIdFrom(clusterNodeId)).thenReturn(Collections.emptyList());
    Mockito.when(pathRepository.findByClusterNodeIdTo(clusterNodeId)).thenReturn(clusterNodePathsFrom);
    service.editClusterNode(editDto);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdFrom(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).findByClusterNodeIdTo(clusterNodeId);
    Mockito.verify(pathRepository, Mockito.times(1)).deleteAll(Collections.emptyList());
    Mockito.verify(pathRepository, Mockito.times(1)).saveAll(Mockito.any());
  }
}
